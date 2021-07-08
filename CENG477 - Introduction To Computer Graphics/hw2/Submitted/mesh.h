#pragma once

#include "triangle.h"

#include <vector>

namespace fst
{
    class Mesh : public Triangular
    {
    public:
        Mesh(const std::vector<Triangle>& triangles, int material_id,std::string tranformations, int texture);
        Mesh(std::vector<Triangle>&& triangles, int material_id,std::string transformations, int texture);

        bool intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const override;
        bool intersectShadowRay(const Ray& ray, float max_distance) const override;
        std::string getTransformations();
        std::vector<Triangle> getTriangles();
        int getTexture(){return m_texture;}
    private:
	    std::vector<Triangle> m_triangles;
        int m_material_id;
        std::string m_transformations;
        int m_texture;

    };
}
