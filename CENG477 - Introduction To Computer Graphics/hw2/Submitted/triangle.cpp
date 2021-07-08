#include "hit_record.h"
#include "triangle.h"
#include "ray.h"
#include <iostream>
namespace fst
{
    Triangle::Triangle(const math::Vector3f& v0, const math::Vector3f& edge1, const math::Vector3f& edge2, const math::Vector3f& v1, const math::Vector3f& v2, int v0_id, int v1_id, int v2_id)
        : m_v0(v0)
        , m_v1(v1)
        , m_v2(v2)
        , m_edge1(edge1)
        , m_edge2(edge2)
        , m_normal(math::normalize(math::cross(edge1, edge2)))
        , m_v0_id(v0_id)
        , m_v1_id(v1_id)
        , m_v2_id(v2_id)
    {}

    bool Triangle::intersect(const Ray& ray, HitRecord& hit_record, float max_distance) const
    {
        //M�ller-Trumbore algorithm
       
        auto pvec       = math::cross(ray.get_direction(), m_edge2);
        auto inv_det    = 1.0f / math::dot(m_edge1, pvec);

        auto tvec   = ray.get_origin() - m_v0;
        auto w1     = math::dot(tvec, pvec) * inv_det;

        if (w1 < 0.0f || w1 > 1.0f)
        {
            return false;
        }

        auto qvec = math::cross(tvec, m_edge1);
        auto w2 = math::dot(ray.get_direction(), qvec) * inv_det;

        if (w2 < 0.0f || (w1 + w2) > 1.0f)
        {
            return false;
        }

        auto distance = math::dot(m_edge2, qvec) * inv_det;
        if (distance > 0.0f && distance < max_distance)
        {
            //Fill the intersection record.
            hit_record.normal   = m_normal;
            hit_record.distance = distance;
            hit_record.v_0 = this->m_v0_id;
            hit_record.v_1 = this->m_v1_id;
            hit_record.v_2 = this->m_v2_id;
            hit_record.tv0 = this->m_v0;
            hit_record.tv1 = this->m_v1;
            hit_record.tv2 = this->m_v2;
            return true;
        }
        return false;
    }

    bool Triangle::intersectShadowRay(const Ray& ray, float max_distance) const
    {
        //M�ller-Trumbore algorithm
        auto pvec = math::cross(ray.get_direction(), m_edge2);
        auto inv_det = 1.0f / math::dot(m_edge1, pvec);

        auto tvec = ray.get_origin() - m_v0;
        auto w1 = math::dot(tvec, pvec) * inv_det;

        if (w1 < 0.0f || w1 > 1.0f)
        {
            return false;
        }

        auto qvec = math::cross(tvec, m_edge1);
        auto w2 = math::dot(ray.get_direction(), qvec) * inv_det;

        if (w2 < 0.0f || (w1 + w2) > 1.0f)
        {
            return false;
        }

        auto distance = math::dot(m_edge2, qvec) * inv_det;
        return distance > 0.0f && distance < max_distance;
    }
    math::Vector3f Triangle::getFirstV(){
        return m_v0;
    }
    math::Vector3f Triangle::getSecondV(){
        return m_v1;
    }
    math::Vector3f Triangle::getThirdV(){
        return m_v2;
    }
    math::Vector3f Triangle::getNormal(){
        return m_normal;
    }
    void Triangle::setFirstV(math::Vector3f v){
        m_v0 = v;
    }
    void Triangle::setSecondV(math::Vector3f v){
        m_v1 = v;
    }
    void Triangle::setThirdV(math::Vector3f v){
        m_v2 = v;
    }
    void Triangle::setNormal(math::Vector3f n){
        m_normal=n;
    }
}
