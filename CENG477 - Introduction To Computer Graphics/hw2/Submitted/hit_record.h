#pragma once

#include "vector3f.h"
#include "parser.h"

namespace fst
{
    typedef struct HitRecord
    {
        math::Vector3f normal;
        math::Vector3f snormal;
        float distance;
        int material_id;
        std::string Type;
        int texture_id;
        math::Vector3f intersectPoint;
        math::Vector3f sintersectPoint;
        math::Vector3f center;
        float radius;
        int v_0;
        int v_1;
        int v_2;
        math::Vector3f direction;
        math::Vector3f tv0;
        math::Vector3f tv1;
        math::Vector3f tv2;
    } HitRecord;
}