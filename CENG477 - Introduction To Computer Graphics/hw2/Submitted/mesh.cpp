#include "mesh.h"
#include "hit_record.h"
#include "ray.h"
#include "vector3f.h"

namespace fst
{
    Mesh::Mesh(const std::vector<Triangle>& triangles, int material_id,std::string transformations, int texture)
	: m_triangles(triangles)
        , m_material_id(material_id),m_transformations(transformations)
        , m_texture(texture)
    {}

    Mesh::Mesh(std::vector<Triangle>&& triangles, int material_id,std::string transformations, int texture)
	: m_triangles(std::move(triangles))
        , m_material_id(material_id),m_transformations(transformations), m_texture(texture)
    {}

    bool Mesh::intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const
    {
        HitRecord temp;
        float min_distance = max_distance;
        for (auto& triangle : m_triangles)
        {
            if (triangle.intersect(ray, temp, min_distance) && math::dot(temp.normal, ray.get_direction()) < 0.0f)
            {
                min_distance = temp.distance;
                hit_record = temp;
		        hit_record.material_id = m_material_id;
                hit_record.texture_id = m_texture;
                hit_record.Type = "mesh";
                hit_record.tv0 = temp.tv0;
                hit_record.tv1 = temp.tv1;
                hit_record.tv2 = temp.tv2;
            }
        }

        return min_distance != max_distance;
    }

    bool Mesh::intersectShadowRay(const Ray& ray, float max_distance) const
    {
        for (auto& triangle : m_triangles)
        {
            if (triangle.intersectShadowRay(ray, max_distance))
            {
		return true;
            }
        }

	return false;
    }
    std::string Mesh::getTransformations(){
        return m_transformations;
    }
    std::vector<Triangle> Mesh::getTriangles(){
        return m_triangles;
    }

}
