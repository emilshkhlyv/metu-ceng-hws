#include "integrator.h"
#include "image.h"
#include "ctpl_stl.h"
#include <iostream>
#include "vector3f.h"

namespace fst
{
    Integrator::Integrator(const parser::Scene& parser)
    {
        m_scene.loadFromParser(parser);
    }

    void clamp(float& u, float& v){
        float temp1, temp2;
        temp1 = fmin(1.0, u);
        temp2 = fmin(1.0, v);
        u = fmax(0, temp1);
        v = fmax(0, temp2);
    }

    float determinant(const math::Vector3f& v0, const math::Vector3f& v1, const math::Vector3f& v2){
        float a = v0.x * (v1.y * v2.z - v2.y * v1.z);
        float b = v0.y * (v2.x * v1.z - v1.x * v2.z);
        float c = v0.z * (v1.x * v2.y - v1.y * v2.x);
        return a+b+c ;
    }
    void repeat(float& u, float& v){
        u = u - floor(u);
        v = v - floor(v); 
    }

    void iandj(float& i, float& j, float& u, float& v, Texture& texture){
        int width, height;
        width = texture.getWidth();
        height = texture.getHeight();
        i = u * width;
        j = v * height;
        if(i >= width)  i -= 1;
        if(j >= height) j -= 1;
    }

    math::Vector3f Integrator::renderPixel(const Ray& ray, int depth) const
    {
        if (depth > m_scene.max_recursion_depth) {
            return math::Vector3f(0.0f, 0.0f, 0.0f);
        }

        HitRecord hit_record;
        auto result = m_scene.intersect(ray, hit_record, std::numeric_limits<float>::max());

        if (!result) {
            return m_scene.background_color;
        }

        auto& material              = m_scene.materials[hit_record.material_id - 1];
        auto intersection_point     = ray.getPoint(hit_record.distance);
        auto sintersection_point    = hit_record.sintersectPoint;
        auto normal                 = hit_record.snormal;

        math::Vector3f k_d  = material.get_diffuse();
        math::Vector3f renk = material.get_diffuse();
        Texture texture;
        auto color = material.get_ambient() * m_scene.ambient_light;
        if(hit_record.Type == "sphere" && hit_record.texture_id != -1){
            float theta, phi;
            if(math::length(ray.get_origin() - hit_record.center) < hit_record.radius){
                theta = acos((intersection_point.y - hit_record.center.y) / hit_record.radius);
                phi   = atan2(intersection_point.z - hit_record.center.z, intersection_point.x - hit_record.center.x);
            }
            else{
                theta = acos((normal.y));
                phi   = atan2(normal.z, normal.x);
            }

            float u = (-phi + M_PI) / (2* M_PI);
            float v = theta / M_PI;

            texture = m_scene.textures[hit_record.texture_id-1];

            if(texture.getAppearance() == "clamp"){
                clamp(u, v);
            }
            else if(texture.getAppearance() == "repeat") {
                repeat(u, v);
            }
            float i, j;
            iandj(i, j, u, v, texture);


            if(texture.getInterpolation() == "nearest"){
                renk = texture.nearest(i, j);
            }
            else if(texture.getInterpolation() == "bilinear"){
                renk = texture.bilinear(i, j);
            }
            
            if(texture.getDecalMode() == "blend_kd"){
                renk = ((renk / 255) + k_d) / 2;
            }
            else if(texture.getDecalMode() == "replace_kd"){
                renk = renk / 255;
            }
            else if(texture.getDecalMode() == "replace_all"){
                color = renk + color;
            }
        }
        if(hit_record.Type == "mesh"   && hit_record.texture_id != -1){
            math::Vector2f u_a, u_b, u_c;
            u_a = this->m_scene.tex_coord_data[hit_record.v_0 - 1];
            u_b = this->m_scene.tex_coord_data[hit_record.v_1 - 1];
            u_c = this->m_scene.tex_coord_data[hit_record.v_2 - 1];

            math::Vector3f v0, v1, v2;
            v0 = hit_record.tv0;
            v1 = hit_record.tv1;
            v2 = hit_record.tv2;

            math::Vector3f a = v0 - v1;
            math::Vector3f b = v0 - v2;
            math::Vector3f d = hit_record.direction;
            math::Vector3f c = v0 - ray.get_origin();

            float detA = determinant(a, b, d);
            float Beta = determinant(c, b, d)/detA;
            float Gama = determinant(a, c, d)/detA;

            float u, v;

            u = u_a.x + Beta*(u_b.x - u_a.x) + Gama*(u_c.x - u_a.x);
            v = u_a.y + Beta*(u_b.y - u_a.y) + Gama*(u_c.y - u_a.y);

            texture = m_scene.textures[hit_record.texture_id-1];

            if(texture.getAppearance() == "clamp"){
                clamp(u, v);
            }
            else if(texture.getAppearance() == "repeat") {
                repeat(u, v);
            }
            float i, j;
            iandj(i, j, u, v, texture);

            if(texture.getInterpolation() == "nearest"){
                renk = texture.nearest(i, j);
            }
            else if(texture.getInterpolation() == "bilinear"){
                renk = texture.bilinear(i, j);
            }
            
            if(texture.getDecalMode() == "blend_kd"){
                renk = ((renk / 255) + k_d) / 2;
            }
            else if(texture.getDecalMode() == "replace_kd"){
                renk = renk / 255;
            }
            else if(texture.getDecalMode() == "replace_all"){
                color = renk + color;
            }
        }
        for (auto& light : m_scene.point_lights) {
            auto to_light = light.get_position() - intersection_point;
            auto light_pos_distance = math::length(to_light);
            to_light = to_light / light_pos_distance;

            Ray shadow_ray(intersection_point + m_scene.shadow_ray_epsilon * to_light, to_light);
            if (!m_scene.intersectShadowRay(shadow_ray, light_pos_distance)) {
                color = color +  light.computeRadiance(light_pos_distance) * material.computeBrdf(to_light, -ray.get_direction(), hit_record.normal, renk, texture.getDecalMode());
            }
        }

        auto& mirror = material.get_mirror();
        if (mirror.x + mirror.y + mirror.z > 0.0f) {
            auto new_direction = math::reflect(ray.get_direction(), hit_record.normal);
            Ray secondary_ray(intersection_point + m_scene.shadow_ray_epsilon * new_direction, new_direction);

            return color + mirror * renderPixel(secondary_ray, depth + 1);
        }
        else {
            return color;
        }
    }

    void Integrator::integrate() const
    {
        for (auto& camera : m_scene.cameras)
        {
            auto& resolution = camera.get_screen_resolution();
            Image image(resolution.x, resolution.y);

            ctpl::thread_pool pool(128);
            for (int i = 0; i < resolution.x; ++i)
            {
                pool.push([i, &resolution, &camera, &image, this](int id) {
                    for (int j = 0; j < resolution.y; ++j)
                    {
                        auto ray = camera.castPrimayRay(static_cast<float>(i), static_cast<float>(j));
                        auto color = renderPixel(ray, 0);
                        image.setPixel(i, j, math::clamp(color, 0.0f, 255.0f));
                    }
                });
            }
            pool.stop(true);
            image.save(camera.get_image_name());
        }
    }
}
