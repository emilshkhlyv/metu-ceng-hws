#include "scene.h"
#include <cmath>
#include <stdlib.h>
#include <string>
#include <iostream>
#include "matrixInverse.h"
#include "jpeg.h"
namespace fst
{
    
    void Scene::loadFromParser(const parser::Scene& parser)
    {
        for (auto& camera : parser.cameras)
        {
            cameras.push_back(Camera(
                math::Vector3f(camera.position.x, camera.position.y, camera.position.z),
                math::Vector3f(camera.gaze.x, camera.gaze.y, camera.gaze.z),
                math::Vector3f(camera.up.x, camera.up.y, camera.up.z),
                math::Vector4f(camera.near_plane.x, camera.near_plane.y, camera.near_plane.z, camera.near_plane.w),
                math::Vector2f(camera.image_width, camera.image_height),
                camera.image_name,
                camera.near_distance));
        }

        for (auto& pointlight : parser.point_lights)
        {
            point_lights.push_back(PointLight(
                math::Vector3f(pointlight.position.x, pointlight.position.y, pointlight.position.z),
                math::Vector3f(pointlight.intensity.x, pointlight.intensity.y, pointlight.intensity.z)));
        }

        for (auto& material : parser.materials)
        {
            materials.push_back(Material(
                math::Vector3f(material.ambient.x, material.ambient.y, material.ambient.z),
                math::Vector3f(material.diffuse.x, material.diffuse.y, material.diffuse.z),
                math::Vector3f(material.specular.x, material.specular.y, material.specular.z),
                math::Vector3f(material.mirror.x, material.mirror.y, material.mirror.z),
                material.phong_exponent));
        }

        for (auto &translation : parser.translations)
        {
            translations.push_back(
                Translation(translation.x, translation.y, translation.z));
        }

        for (auto &scaling : parser.scalings)
        {
            scalings.push_back(
                Scaling(scaling.x, scaling.y, scaling.z));
        }

        for (auto &rotation : parser.rotations)
        {
            rotations.push_back(
                Rotation(rotation.angle, rotation.x, rotation.y, rotation.z));
        }

        for (auto& vertex : parser.vertex_data)
        {
            vertex_data.push_back(math::Vector3f(vertex.x, vertex.y, vertex.z));
        }

        for (auto& tex_coord : parser.tex_coord_data)
        {
            tex_coord_data.push_back(math::Vector2f(tex_coord.x, tex_coord.y));
        }

        for (auto& mesh : parser.meshes)
        {
            std::vector<Triangle> triangles;
            for (auto& face : mesh.faces)
            {
                triangles.push_back(Triangle(
                    vertex_data[face.v0_id - 1],
                    vertex_data[face.v1_id - 1] - vertex_data[face.v0_id - 1],
                    vertex_data[face.v2_id - 1] - vertex_data[face.v0_id - 1], 
                    vertex_data[face.v1_id - 1],
                    vertex_data[face.v2_id - 1],
                    face.v0_id,
                    face.v1_id,
                    face.v2_id
                    )
                );
            }
            Mesh nmesh = Mesh(std::move(triangles), mesh.material_id, mesh.transformations, mesh.texture_id);
            nmesh = modelingTransformation(nmesh,mesh.material_id);
            meshes.push_back(nmesh);
        }

        for (auto& triangle : parser.triangles)
        {
            std::vector<Triangle> triangles;
            triangles.push_back(Triangle(
                vertex_data[triangle.indices.v0_id - 1],
                vertex_data[triangle.indices.v1_id - 1] - vertex_data[triangle.indices.v0_id - 1],
                vertex_data[triangle.indices.v2_id - 1] - vertex_data[triangle.indices.v0_id - 1],
                vertex_data[triangle.indices.v1_id - 1],
                vertex_data[triangle.indices.v2_id - 1],
                triangle.indices.v0_id,
                triangle.indices.v1_id,
                triangle.indices.v2_id)
                );
            Mesh nmesh = Mesh(std::move(triangles), triangle.material_id,triangle.transformations, triangle.texture_id);
            nmesh = modelingTransformation(nmesh,triangle.material_id);
            meshes.push_back(nmesh);
        }

        for (auto& sphere : parser.spheres)
        {
            Sphere init = Sphere(vertex_data[sphere.center_vertex_id - 1], sphere.radius, sphere.material_id, sphere.transformations, sphere.texture_id);
            Sphere nsphere = sphereModelingTransformation(init, sphere.material_id);
            spheres.push_back(nsphere);
        }

        for (auto& texture: parser.textures){
            int width, height;
            read_jpeg_header((char *)(texture.imageName.c_str()), width, height);
            unsigned char* image = (unsigned char*) malloc(sizeof(unsigned char) * width * height * 3);
            read_jpeg((char *)(texture.imageName.c_str()), image, width, height);
            textures.push_back(Texture(width, height, image, texture.imageName, texture.interpolation, texture.decalMode, texture.appearance));
        }
        background_color = math::Vector3f(parser.background_color.x, parser.background_color.y, parser.background_color.z);
        ambient_light = math::Vector3f(parser.ambient_light.x, parser.ambient_light.y, parser.ambient_light.z);
        shadow_ray_epsilon = parser.shadow_ray_epsilon;
        max_recursion_depth = parser.max_recursion_depth;
    }

    bool Scene::intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const
    {
        HitRecord temp;
        float min_distance = max_distance;
        for (auto& sphere : spheres)
        {
            if (sphere.intersect(ray, temp, min_distance))
            {
                min_distance = temp.distance;
                HitRecord nrec = pointModelingTransformation(temp, sphere.m_transformations);
                temp.sintersectPoint = nrec.intersectPoint;
                temp.snormal = nrec.normal;
                hit_record = temp;
            } 
        }

        for (auto& mesh : meshes)
        {
            if (mesh.intersect(ray, temp, min_distance))
            {
                min_distance = temp.distance;
                hit_record = temp;
                hit_record.direction = ray.get_direction();
            }
        }

        return min_distance != max_distance;
    }
    bool Scene::intersectShadowRay(const Ray& ray, float max_distance) const
    {
        for (auto& sphere : spheres)
        {
            if (sphere.intersectShadowRay(ray, max_distance))
            {
                return true;
            }
        }


        for (auto& mesh : meshes)
        {
            if (mesh.intersectShadowRay(ray, max_distance))
            {
		return true;
            }
        }

	return false;
    }
    Matrix matrixMult(float m1[4][4], float m2[4][4]){
        Matrix f;
        float plus;
        for(int i=0; i<4;i++){
            for(int j=0;j<4;j++){
                plus=0;
                for(int k=0;k<4;k++){
                    plus+= m1[i][k]*m2[k][j];
                }
                f.m[i][j]=plus;
            }
        }
        return f;
    }
    math::Vector3f matrixMultV3(float m[4][4],math::Vector3f vec){
        math::Vector4f vec4;
        math::Vector3f res;
        vec4.x = vec.x; vec4.y = vec.y; vec4.z = vec.z; vec4.w=1;
        res.x = m[0][0]*vec4.x + m[0][1]*vec4.y + m[0][2]*vec4.z + m[0][3]*vec4.w;
        res.y = m[1][0]*vec4.x + m[1][1]*vec4.y + m[1][2]*vec4.z + m[1][3]*vec4.w;
        res.z = m[2][0]*vec4.x + m[2][1]*vec4.y + m[2][2]*vec4.z + m[2][3]*vec4.w;
        return res;
    }
    Matrix translation_matrix(Translation translation){
        Matrix translation_m;
        translation_m.m[0][0] = 1; translation_m.m[0][1] = 0; translation_m.m[0][2] = 0; translation_m.m[0][3] = translation.x;
        translation_m.m[1][0] = 0; translation_m.m[1][1] = 1; translation_m.m[1][2] = 0; translation_m.m[1][3] = translation.y;
        translation_m.m[2][0] = 0; translation_m.m[2][1] = 0; translation_m.m[2][2] = 1; translation_m.m[2][3] = translation.z;
        translation_m.m[3][0] = 0; translation_m.m[3][1] = 0; translation_m.m[3][2] = 0; translation_m.m[3][3] = 1;
        return translation_m;
    }
    Matrix scaling_matrix(Scaling scaling){
        Matrix scaling_m;
        scaling_m.m[0][0] = scaling.x; scaling_m.m[0][1] = 0; scaling_m.m[0][2] = 0; scaling_m.m[0][3] = 0;
        scaling_m.m[1][0] = 0; scaling_m.m[1][1] = scaling.y; scaling_m.m[1][2] = 0; scaling_m.m[1][3] = 0;
        scaling_m.m[2][0] = 0; scaling_m.m[2][1] = 0; scaling_m.m[2][2] = scaling.z; scaling_m.m[2][3] = 0; 
        scaling_m.m[3][0] = 0; scaling_m.m[3][1] = 0; scaling_m.m[3][2] = 0; scaling_m.m[3][3] = 1;
        return scaling_m;
    }
    Matrix rotation_matrix(Rotation rotation){
        Matrix rotating_m;
        math::Vector3f u;
        //normalize arbitrary rotation axis
        u.x = rotation.x; u.y = rotation.y; u.z = rotation.z;
        u = math::normalize(u);
        /* Use alternative form*/
        //Find v, setting smallest component of u to 0 
        math::Vector3f v,w;
        if(abs(u.x)<=abs(u.y) && abs(u.x)<=abs(u.z)){
            v.x = 0; v.y = - u.z; v.z = u.y;
        }
        else if(abs(u.y)<=abs(u.x)&&abs(u.y)<=abs(u.z)){
            v.x= -u.z; v.y=0; v.z=u.x;
        }
        else{
            v.x= -u.y; v.y = u.x; v.z =0;
        }
        v = math::normalize(v);
        w = math::cross(u,v);
        w = math::normalize(w);
        //Find M and MInverse
        float M[4][4]= {{u.x,u.y,u.z,0},{v.x,v.y,v.z,0},{w.x,w.y,w.z,0},{0,0,0,1}};
        float MInv[4][4] = {{u.x,v.x,w.x,0},{u.y,v.y,w.y,0},{u.z,v.z,w.z,0},{0,0,0,1}};
        //given theta as degree, make as radian
        float theta = (rotation.angle*M_PI)/180.0;
        //rotation matrix Rx(theta)
        float rotM[4][4];
        rotM[0][0] = 1; rotM[0][1] = 0;             rotM[0][2] = 0;             rotM[0][3] = 0;
        rotM[1][0] = 0; rotM[1][1] = cos(theta);    rotM[1][2] = -sin(theta);   rotM[1][3] = 0;
        rotM[2][0] = 0; rotM[2][1] = sin(theta);    rotM[2][2] = cos(theta);    rotM[2][3] = 0;
        rotM[3][0] = 0; rotM[3][1] = 0;             rotM[3][2] = 0;             rotM[3][3] = 1;

        //Rotation transform MInverse*Rx(theta)*M
        Matrix rotMM= matrixMult(rotM,M);
        
        rotating_m=matrixMult(MInv,rotMM.m);
       
        return rotating_m;
    }
    Matrix normal_matrix(float m[4][4]){
        Matrix res;
        invert(m,res.m);
        Matrix tres;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                tres.m[j][i] = res.m[i][j];
            }
        }
        return tres;

    }
    std::vector<std::string> makeTransformationVector(std::string transformations){
        std::vector<std::string> tvector;
        std::string singleT="";
        while(transformations.size()){
            int i = transformations.find(" ");
            if(i!=std::string::npos){
                tvector.push_back(transformations.substr(0,i));
                transformations = transformations.substr(i+1);
            }
            else{
                tvector.push_back(transformations);
                transformations ="";
            }
        }
        return tvector;
    }
    HitRecord Scene::pointModelingTransformation(HitRecord temp, std::string transformations) const{
        Matrix transforms;
        Matrix result;
        math::Vector3f resvec;
        result.m[0][0] = 1; result.m[0][1] = 0; result.m[0][2] = 0; result.m[0][3] = 0;
        result.m[1][0] = 0; result.m[1][1] = 1; result.m[1][2] = 0; result.m[1][3] = 0;
        result.m[2][0] = 0; result.m[2][1] = 0; result.m[2][2] = 1; result.m[2][3] = 0;
        result.m[3][0] = 0; result.m[3][1] = 0; result.m[3][2] = 0; result.m[3][3] = 1;
        std::vector<std::string> sphereTransform = makeTransformationVector(transformations);
        for(auto& t:sphereTransform){
            if(t[0]=='r'){
                std::string rid = t.substr(1);
                int id = std::stoi(rid);
                Rotation rotas = rotations[id-1];
                rotas.angle = (-1)*rotas.angle;
                transforms=rotation_matrix(rotas);
                result =matrixMult(transforms.m,result.m);
            }
        }
        math::Vector3f n = matrixMultV3(normal_matrix(result.m).m,temp.normal);

        resvec = matrixMultV3(result.m,temp.intersectPoint);
        HitRecord nrec;
        nrec.intersectPoint = resvec;
        nrec.normal = n;
        return nrec ;
    }

    Sphere Scene::sphereModelingTransformation(Sphere &sphere,int material_id){
        Matrix transforms;
        math::Vector3f ncenter;
        Matrix result;
        float nradius = sphere.getRadius();
        result.m[0][0] = 1; result.m[0][1] = 0; result.m[0][2] = 0; result.m[0][3] = 0;
        result.m[1][0] = 0; result.m[1][1] = 1; result.m[1][2] = 0; result.m[1][3] = 0;
        result.m[2][0] = 0; result.m[2][1] = 0; result.m[2][2] = 1; result.m[2][3] = 0;
        result.m[3][0] = 0; result.m[3][1] = 0; result.m[3][2] = 0; result.m[3][3] = 1;
        std::vector<std::string> sphereTransform = makeTransformationVector(sphere.getTransformations());
        for(auto& t:sphereTransform){
            if(t[0]=='t'){
                std::string tid =t.substr(1);
                int id = std::stoi(tid);
                transforms = translation_matrix(translations[id-1]);
                result = matrixMult(transforms.m,result.m);
            }
            else if(t[0]=='s'){
                std::string sid =t.substr(1);
                int id = std::stoi(sid);
                transforms = scaling_matrix(scalings[id-1]);
                nradius *=scalings[id-1].x;
                result =matrixMult(transforms.m,result.m);
            }
        }
        ncenter = matrixMultV3(result.m,sphere.getCenter());
        Sphere nsphere = Sphere (ncenter, nradius, material_id, sphere.getTransformations(), sphere.m_texture);
        return nsphere;
    }

    Mesh Scene::modelingTransformation(Mesh &mesh, int material_id){
        Matrix transforms;
        Matrix result;
        result.m[0][0] = 1; result.m[0][1] = 0; result.m[0][2] = 0; result.m[0][3] = 0;
        result.m[1][0] = 0; result.m[1][1] = 1; result.m[1][2] = 0; result.m[1][3] = 0;
        result.m[2][0] = 0; result.m[2][1] = 0; result.m[2][2] = 1; result.m[2][3] = 0;
        result.m[3][0] = 0; result.m[3][1] = 0; result.m[3][2] = 0; result.m[3][3] = 1;
        std::vector<std::string> meshTransform = makeTransformationVector(mesh.getTransformations());
        for(auto& t:meshTransform){
            if(t[0]=='t'){
                std::string tid =t.substr(1);
                int id = std::stoi(tid);
                transforms = translation_matrix(translations[id-1]);
                result = matrixMult(transforms.m,result.m);
            }
            else if(t[0]=='s'){
                std::string sid =t.substr(1);
                int id = std::stoi(sid);
                transforms = scaling_matrix(scalings[id-1]);
                result =matrixMult(transforms.m,result.m);
            }
            else{
                std::string rid =t.substr(1);
                int id = std::stoi(rid);
                transforms = rotation_matrix(rotations[id-1]);
                result = matrixMult(transforms.m,result.m);
            }
        }
        std::vector<Triangle> ntriangles;
        for(auto& tri: mesh.getTriangles()){
            math::Vector3f v0 = matrixMultV3(result.m,tri.getFirstV());
            tri.setFirstV(v0);
            math::Vector3f v1 = matrixMultV3(result.m,tri.getSecondV());
            tri.setSecondV(v1);
            math::Vector3f v2 = matrixMultV3(result.m,tri.getThirdV());
            ntriangles.push_back(Triangle(v0, v1-v0, v2-v0, v1, v2, tri.getM_v0_id(), tri.getM_v1_id(), tri.getM_v2_id()));
            tri.setThirdV(v2);
            math::Vector3f n = matrixMultV3(normal_matrix(result.m).m,tri.getNormal());
            tri.setNormal(n);
        }
        Mesh nmesh = Mesh(std::move(ntriangles), material_id, mesh.getTransformations(), mesh.getTexture());
       return nmesh;
    }
    
    
}
