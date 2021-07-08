#pragma once

#include "triangular.h"
#include "vector3f.h"
#include <string>

namespace fst
{
    class Ray;
    struct HitRecord;

    class Triangle : public Triangular
    {
    public:
        Triangle(const math::Vector3f& v0, const math::Vector3f& edge1, const math::Vector3f& edge2,const math::Vector3f& v1,const math::Vector3f& v2, int v0_id, int v1_id, int v2_id);

        bool intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const override;
        bool intersectShadowRay(const Ray& ray, float max_distance) const override;
        math::Vector3f getFirstV();
        math::Vector3f getSecondV();
        math::Vector3f getThirdV();
        math::Vector3f getNormal();
        int getM_v0_id() {return m_v0_id;}
        int getM_v1_id() {return m_v1_id;}
        int getM_v2_id() {return m_v2_id;}

        void setFirstV(math::Vector3f v);
        void setSecondV(math::Vector3f v);
        void setThirdV(math::Vector3f v);
        void setNormal(math::Vector3f n);
    private:
        math::Vector3f m_v0, m_v1, m_v2, m_edge1, m_edge2;
        int m_v0_id, m_v1_id, m_v2_id;
        math::Vector3f m_normal;
    };
}
