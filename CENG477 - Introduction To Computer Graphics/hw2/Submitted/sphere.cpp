#include "sphere.h"
#include "hit_record.h"
#include "ray.h"
#include <iostream>
namespace fst
{
    Sphere::Sphere(const math::Vector3f& center, float radius, int material_id, std::string transformations, int texture)
        : m_center(center)
        , m_radius(radius)
        , m_material_id(material_id)
        , m_transformations(transformations)
        , m_texture(texture)
    {}

    bool Sphere::intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const
    {
        //geometrical approach
        auto e = m_center - ray.get_origin();
        auto a = math::dot(e, ray.get_direction());
        auto x = m_radius * m_radius + a * a - math::dot(e, e);

        

        if (x < 0.0f)
        {
            return false;
        }

        auto distance = a - sqrtf(x);
        if(math::length(ray.get_origin() - this->m_center) < this->m_radius){
            distance = -1*distance;
        }
        if (distance > 0.0f && distance < max_distance)
        {
            //Fill the intersection record.
            hit_record.distance = distance;
            hit_record.intersectPoint = ray.getPoint(hit_record.distance);
            hit_record.normal       = math::normalize(ray.getPoint(hit_record.distance) - m_center);
            hit_record.material_id  = m_material_id;
            hit_record.Type         = "sphere";
            hit_record.texture_id   = m_texture;
            hit_record.center       = m_center;
            hit_record.radius       = m_radius;
            return true;
        }
        return false;
    }

    bool Sphere::intersectShadowRay(const Ray& ray, float max_distance) const
    {
        //geometrical approach
        auto e = m_center - ray.get_origin();
        auto a = math::dot(e, ray.get_direction());
        auto x = m_radius * m_radius + a * a - math::dot(e, e);

        if (x < 0.0f)
        {
            return false;
        }

        auto distance = a - sqrtf(x);
        return distance > 0.0f && distance < max_distance;
    }
    std::string Sphere::getTransformations(){
        return m_transformations;
    }
    math::Vector3f Sphere::getCenter(){
        return m_center;
    }
    float   Sphere::getRadius (){
        return m_radius;
    }
    int     Sphere::getTexture(){
        return m_texture;
    }
}