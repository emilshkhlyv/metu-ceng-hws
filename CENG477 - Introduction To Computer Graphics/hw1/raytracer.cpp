#include <iostream>
#include <cmath>
#include <vector>
#include <float.h>
#include "parser.h"
#include "ppm.h"
#include <pthread.h>
#include <thread>
#include <algorithm>

using namespace parser;
using namespace std;
typedef struct{
    Vec3f position;
    Vec3f direction;
} Ray;

typedef struct{
    Vec3f intersectionPoint;
    Vec3f normal;
    float intersectionT;
    std::string type;
    int materialId;
    bool intersected = false;
} Intersection;

// personalized Sphere with center 
typedef struct{
    Sphere sphere;
    Vec3f center;
} Spheros;

// input sphere, output spheros with center vector
Spheros sphering(const Scene& scene, const Sphere& sphere){
    return {sphere, scene.vertex_data[sphere.center_vertex_id - 1]};
}

float dist_two_vecs(const Vec3f& a, const Vec3f& b){
    return sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y) + (a.z - b.z)*(a.z - b.z));
}
float dotProduct(const Vec3f& v1, const Vec3f& v2){
    return (v1.x*v2.x+v1.y*v2.y+v1.z*v2.z);
}
float length_of_vector(const Vec3f& v){
    return sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
} 
float determinant(const Vec3f& v0, const Vec3f& v1, const Vec3f& v2){
    float a = v0.x * (v1.y * v2.z - v2.y * v1.z);
    float b = v0.y * (v2.x * v1.z - v1.x * v2.z);
    float c = v0.z * (v1.x * v2.y - v1.y * v2.x);
    return a+b+c ;
}
float gett(const Ray& ray, const Vec3f& p) {
    return (p.x-ray.position.x)/ray.direction.x;
}

Vec3f vector_sum        (const Vec3f& vector1,  const Vec3f& vector2){
    return {vector1.x + vector2.x, vector1.y + vector2.y, vector1.z + vector2.z};
}
Vec3f vector_subs       (const Vec3f& vector1,  const Vec3f& vector2){
   return {vector1.x - vector2.x, vector1.y - vector2.y, vector1.z - vector2.z};
}
Vec3f vector_scalar_mult(const Vec3f& vector1,  const float& scalar){
   return {vector1.x * scalar, vector1.y * scalar, vector1.z * scalar};
}
Vec3f cross             (const Vec3f& vector1,  const Vec3f& vector2){
    return {vector1.y * vector2.z - vector2.y * vector1.z, vector2.x * vector1.z - vector1.x * vector2.z, vector1.x * vector2.y - vector2.x * vector1.y};
}
Vec3f normalize         (const Vec3f& vector){
    float d = length_of_vector(vector);
    return {vector.x/d, vector.y/d, vector.z/d};
}
Vec3f intersectionPoint (const Ray& ray,        const float& t){
    return {(ray.position.x+(t*ray.direction.x)), (ray.position.y+(t*ray.direction.y)), (ray.position.z+(t*ray.direction.z))};
}
Vec3f vector_mult       (const Vec3f& v1,       const Vec3f& v2){
    return {v1.x*v2.x, v1.y*v2.y, v1.z*v2.z};
}
// ray calculation
Ray rayGenerate(const Camera& camera, const int& row, const int& col){
    Ray ray;
    Vec3f gaze = normalize(camera.gaze);

    float su = (camera.near_plane.y - camera.near_plane.x)*(col+0.5)/camera.image_width;
    float sv = (camera.near_plane.w - camera.near_plane.z)*(row+0.5)/camera.image_height;

    Vec3f m = vector_sum(camera.position,vector_scalar_mult(gaze,camera.near_distance));
    Vec3f u = normalize(cross(gaze, camera.up));
    Vec3f v = cross(u,gaze);
    Vec3f q = vector_sum(vector_sum(m,vector_scalar_mult(u,camera.near_plane.x)),vector_scalar_mult(v,camera.near_plane.w));
    Vec3f s = vector_sum(vector_sum(q,vector_scalar_mult(u,su)),vector_scalar_mult(vector_scalar_mult(v,sv),-1));
    ray.position = camera.position;
    ray.direction = vector_subs(s,camera.position);
    ray.direction = normalize(ray.direction);
    return ray;
}


Intersection findClosestObject(const std::vector<Intersection> &intersectionPoints){
    Intersection closestObject;
    closestObject.intersected = false;
    if(!intersectionPoints.empty()) {
        closestObject = intersectionPoints[0];
        for(auto &intersection : intersectionPoints){
            if(intersection.intersectionT < closestObject.intersectionT){
                closestObject = intersection;
            }
        }
        closestObject.intersected = true;
    }
    return closestObject;
}

Intersection sphereIntersection     (const Ray& ray, const Spheros& spheros,    const Scene& scene){
    Intersection intersection;
    Vec3f origin    = ray.position;
    Vec3f direction = ray.direction;
    float A,B,C; 
    float sradius  = spheros.sphere.radius;
    float t;
    Vec3f scenter   = spheros.center; //scenein vertex datasından vertex id ye göre center vectoru cektim
    //C=(o-c)(o-c)-R^2 
    Vec3f ominusc = vector_subs(origin,scenter);
    C = ominusc.x*ominusc.x+
        ominusc.y*ominusc.y+
        ominusc.z*ominusc.z - sradius*sradius;
    //B=2d(o-c)
    B = 2*direction.x*ominusc.x+
        2*direction.y*ominusc.y+
        2*direction.z*ominusc.z;
    //A=d^2
    A = direction.x*direction.x+direction.y*direction.y+direction.z*direction.z;
    float delta = B*B-4*A*C;
    if(delta == 0){ //delta=0, one t
        t = -B /(2*A);
        if( t < 0 ) {
            intersection.intersected = false;
        }
        else{
            intersection.intersected    = true;
            intersection.materialId     = spheros.sphere.material_id;
            intersection.type           = "sphere";
            intersection.intersectionPoint = intersectionPoint(ray,t);
            Vec3f scenter               = spheros.center;
            intersection.normal         = normalize(vector_subs(intersection.intersectionPoint, scenter));
            intersection.intersectionT  = t;
        }
    }
    else if( delta > 0 ){ //delta>0 , two t
        
        float t1 = (-1*B + sqrt(delta)) /(2*A);
        float t2 = (-1*B - sqrt(delta)) /(2*A);
        
        if(t1 < 0 && t2 < 0){
            intersection.intersected = false;
        }
        else if(t1 <= t2){
            if(t1 < 0) t = t2; 
            else t = t1;
            intersection.intersected = true;
            intersection.materialId = spheros.sphere.material_id;
            intersection.type = "sphere";
            intersection.intersectionPoint = intersectionPoint(ray,t);
            Vec3f scenter = spheros.center;
            intersection.normal = vector_subs(intersection.intersectionPoint,spheros.center);
            intersection.normal = vector_scalar_mult(intersection.normal,(1/(spheros.sphere.radius)));
            intersection.intersectionT = t;
        }
        else {
            t=t2;
            intersection.intersected = true;
            intersection.materialId = spheros.sphere.material_id;
            intersection.type = "sphere";
            intersection.intersectionPoint = intersectionPoint(ray,t);
            Vec3f scenter = spheros.center;
            intersection.normal = normalize(vector_subs(intersection.intersectionPoint,scenter));
            intersection.intersectionT = t;
        }
    }
    else{ // no real t
        intersection.intersected = false;
    }
    return intersection;

} 
Intersection triangleIntersection   (const Ray& ray, const Triangle& triangle,  const Scene& scene){
    Intersection intersection;
    Vec3f origin = ray.position;
    Vec3f direction = ray.direction;
    Vec3f v0,v1,v2;
    v0 = scene.vertex_data[triangle.indices.v0_id-1];
    v1 = scene.vertex_data[triangle.indices.v1_id-1]; //v0,v1,v2 are the corners in counter clockwise
    v2 = scene.vertex_data[triangle.indices.v2_id-1];
                                         //      a         b        d
    Vec3f a = vector_subs(v0,v1);       //   (v0-v1).x  (v0-v2).x  d.x
    Vec3f b = vector_subs(v0,v2);      //    (v0-v1).y  (v0-v2).y  d.y
    Vec3f d = direction;              //     (v0-v1).z  (v0-v2).z  d.z

    Vec3f c = vector_subs(v0,origin); // c-> ((v0-o).x,(v0-o).y,(v0-o).z)
    float determinantA = determinant(a,b,d);
    if(determinantA==0) {
        intersection.intersected=false;
        return intersection;
    }
    
    float determinantBeta = determinant(c,b,d);
    float determinantGama = determinant(a,c,d);
    
    float beta = determinantBeta/determinantA;
    float gama = determinantGama/determinantA;
    float determinantT = determinant(a,b,c);
    float t = determinantT/determinantA;

    if(beta>=0 && gama>=0 && (gama+beta)<=1&&t>0) 
    {
        intersection.intersected = true;
        intersection.intersectionT = t;
        intersection.materialId = triangle.material_id;
        intersection.type = "triangle";
        intersection.intersectionPoint = intersectionPoint(ray,t);
        intersection.normal = cross(vector_subs(v1,v0),vector_subs(v2,v0));
        intersection.normal = normalize(intersection.normal);
        return intersection;
    }
    else{
        intersection.intersected = false;
        return intersection;
    }
}
Intersection meshIntersectionHelper (const Ray& ray, const Face& face,          const Scene& scene, const int& material_id){
    Intersection intersection;
    intersection.intersected = false;
    intersection.intersectionT =0;
    Vec3f origin = ray.position;
    Vec3f direction = ray.direction;
    Vec3f v0,v1,v2;
    v0 = scene.vertex_data[face.v0_id-1];
    v1 = scene.vertex_data[face.v1_id-1]; //v0,v1,v2 are the corners in counter clockwise
    v2 = scene.vertex_data[face.v2_id-1];
   
    Vec3f a = vector_subs(v0,v1);       //   (v0-v1).x  (v0-v2).x  d.x
    Vec3f b = vector_subs(v0,v2);      //    (v0-v1).y  (v0-v2).y  d.y

    Vec3f d = direction;              //     (v0-v1).z  (v0-v2).z  d.z
    Vec3f c = vector_subs(v0,origin); // c-> ((v0-o).x,(v0-o).y,(v0-o).z)
    float determinantA = determinant(a,b,d);
    if(determinantA==0) {
        intersection.intersected=false;
        intersection.intersectionT = 0;
        return intersection;
    }
    float determinantBeta = determinant(c,b,d);
    float determinantGama = determinant(a,c,d);
    float determinantT = determinant(a,b,c);
    
    
    float gama =(float) determinantGama/determinantA;
    if(gama < 0 || gama > 1) {
        intersection.intersected = false;
        intersection.intersectionT = 0;

		return intersection;
	}
    float beta = (float)determinantBeta/determinantA;
    if(beta < 0 || beta > (1 - gama)) {
        intersection.intersected = false;
        intersection.intersectionT = 0;

		return intersection;
	}
    
    float t = (float)determinantT/determinantA;
    if(t<=0.0){
        intersection.intersected=false;
        intersection.intersectionT =0;
        return intersection;
    }

    if(beta >= 0 && gama >= 0 && (gama+beta) <= 1 && t > 0.0) 
    {
        intersection.intersected = true;
        intersection.intersectionT = t;
        intersection.type = "mesh";
        intersection.intersectionPoint = intersectionPoint(ray,t);
        intersection.normal = cross(vector_subs(v1,v0), vector_subs(v2,v0));
        intersection.normal = normalize(intersection.normal);
        intersection.materialId = material_id;
        return intersection;
    }
    else{
        intersection.intersected = false;
            intersection.intersectionT = 0;

        return intersection;
    }
}
Intersection meshIntersection       (const Ray& ray, const Mesh& mesh,          const Scene& scene){
    Intersection intersection;
    std::vector<Intersection> meshIntersections;
    for(auto &face : mesh.faces){
        Vec3f v0,v1,v2;
        v0 = scene.vertex_data[face.v0_id-1];
        v1 = scene.vertex_data[face.v1_id-1]; //v0,v1,v2 are the corners in counter clockwise
        v2 = scene.vertex_data[face.v2_id-1];
        intersection = meshIntersectionHelper(ray,face,scene,mesh.material_id);
        if(intersection.intersected && intersection.intersectionT>0 && intersection.intersectionT<FLT_MAX){ 
            intersection.materialId = mesh.material_id;
            meshIntersections.push_back(intersection);
        }
    }
    if(!meshIntersections.empty())
    intersection = findClosestObject(meshIntersections);
    return intersection;
}

Vec3f findPixelColor(Ray& ray, const Scene& scene, const Camera& camera){
    Vec3f pixelColor = {0, 0, 0};
    Vec3f mirrorness = {1, 1, 1};
    Vec3f background = {(float)scene.background_color.x,
                        (float)scene.background_color.y, 
                        (float)scene.background_color.z};
    Vec3f tcolor;
    int max = scene.max_recursion_depth;
    bool bingo = false;
    for(int i = 0; i <= max; i++){
        float tmin = FLT_MAX; 
        Intersection intersection;
        Spheros spheros;
                        
        for(auto &sphere:   scene.spheres){
            spheros = sphering(scene, sphere);
            Intersection sphereHit = sphereIntersection(ray, spheros, scene);
            if(sphereHit.intersected == false ) continue;
            if(sphereHit.intersectionT<tmin && sphereHit.intersectionT>0 ){
                tmin = sphereHit.intersectionT;
                intersection = sphereHit;
            }
        }
        for(auto &triangle: scene.triangles){
            Intersection triangleHit = triangleIntersection(ray, triangle, scene);
            if(triangleHit.intersected == false ) continue;
            if(triangleHit.intersectionT<tmin && triangleHit.intersectionT>0 ){
                tmin = triangleHit.intersectionT;
                intersection = triangleHit;
            }
        }
        for(auto &mesh:     scene.meshes){
            Intersection meshHit = meshIntersection(ray, mesh, scene);
            if(meshHit.intersected == false ) continue;
            if(meshHit.intersectionT<tmin && meshHit.intersectionT>0 ){
                tmin = meshHit.intersectionT;
                intersection = meshHit;
            }
        }

        Vec3f PtoCamNorm = normalize(
                                vector_subs(
                                    camera.position,
                                    intersection.intersectionPoint
                                )
                            );          
        if(intersection.intersected){
            bingo = true;
            Material material = scene.materials[intersection.materialId-1];
            //Ambient
            Vec3f color = vector_mult(scene.ambient_light, material.ambient); 

            bool shadowHit;
            for(auto &light : scene.point_lights){
                shadowHit = false;
                float lightToCam = dist_two_vecs(light.position,intersection.intersectionPoint);
                Vec3f lightD        = vector_subs(light.position, intersection.intersectionPoint);
                Vec3f lightDnormal  = normalize(lightD);
                Ray   lightRay      = { 
                                        vector_sum(
                                            intersection.intersectionPoint, 
                                            vector_scalar_mult(
                                                lightDnormal,
                                                scene.shadow_ray_epsilon
                                            )
                                        ), 
                                        lightDnormal
                                      };
                
                //shadow
                Spheros spheros;
                for(auto &sphere:   scene.spheres){
                    spheros = sphering(scene, sphere);
                    Intersection sphereHit = sphereIntersection(lightRay, spheros, scene);
                    if(sphereHit.intersected == true && gett(lightRay,light.position) > sphereHit.intersectionT ){
                        shadowHit = true;
                        break;
                    }
                }
                if(shadowHit != true){
                    for(auto &triangle: scene.triangles){
                        Intersection triangleHit = triangleIntersection(lightRay, triangle, scene);
                        if(triangleHit.intersected == true && gett(lightRay,light.position) > triangleHit.intersectionT){
                            shadowHit = true;
                            break;
                        }
                    }
                }
                if(shadowHit != true){
                    for(auto &mesh :    scene.meshes) {
                        Intersection meshHit = meshIntersection(lightRay, mesh, scene);
                        if(meshHit.intersected == true && gett(lightRay,light.position) > meshHit.intersectionT){
                            shadowHit = true;
                            break;
                        }
                    }
                }
                if(shadowHit == true) continue;
                //diffuse 
                float diffuse = dotProduct(lightD, lightD);
                float lnormal = dotProduct(lightDnormal, intersection.normal) > 0 ? dotProduct(lightDnormal, intersection.normal) : 0.0;
                float ln    = lnormal > 0 ? lnormal : 0;

                //specular 
                Vec3f spec      = normalize(vector_subs(lightDnormal, ray.direction));
                float specdot     = dotProduct(intersection.normal, spec) > 0.0 ? dotProduct(intersection.normal, spec) : 0.0;
                float specPhong = pow(specdot, material.phong_exponent);
                float sp = specPhong > 0 ? specPhong : 0;
                Vec3f all = vector_scalar_mult(material.diffuse, ln/diffuse);
                all = vector_sum(all,vector_scalar_mult(material.specular,sp/diffuse));

                color = vector_sum((vector_mult(all, light.intensity)), color); 
                
                
            }
            pixelColor = vector_sum(
                            pixelColor, 
                            vector_mult(
                                color, 
                                mirrorness
                            )
                        );
            //mirrorness
            mirrorness = vector_mult(mirrorness, material.mirror);  
           
            if(material.mirror.x==0 && material.mirror.y==0 && material.mirror.z==0) {
                break;
            }
            
            ray.position =  vector_sum(
                                vector_scalar_mult(
                                     intersection.normal,
                                    scene.shadow_ray_epsilon
                                ), 
                                intersection.intersectionPoint
                            );
           

            ray.direction = normalize(
                                vector_sum(
                                    ray.direction, 
                                    vector_scalar_mult(
                                        intersection.normal, 
                                            dotProduct(
                                                intersection.normal, 
                                                ray.direction
                                            )
                                         * -2
                                    )
                                )
                            );
        }
        else{
            max = 0;
        }
    }
    return bingo ? pixelColor : background;
}

void RenderThread(const Camera& camera,const int& ks, const int& ke, const Scene& scene, const int& corenum, unsigned char* image){
    int h = ke - ks;
    int w = camera.image_width;
    for(int j = ks; j < ke; ++j){
        for(int k = 0; k < camera.image_width; ++k){
            Ray ray = rayGenerate(camera,j, k);
            Vec3f pixelColor = findPixelColor(ray, scene, camera);
            image[3*j*w+3*k]   = pixelColor.x > 255 ? 255 : floor(pixelColor.x);
            image[3*j*w+3*k+1] = pixelColor.y > 255 ? 255 : floor(pixelColor.y);
            image[3*j*w+3*k+2] = pixelColor.z > 255 ? 255 : floor(pixelColor.z);
        }
    }
}

int main(int argc, char* argv[]){
    Scene scene;
    scene.loadFromXml(argv[1]);
    int corenum = thread::hardware_concurrency();
    if(corenum == 0)    corenum = 8;
    else                corenum *= 4;
    for(auto &camera : scene.cameras){
        const int w = camera.image_width, h = camera.image_height;
        unsigned char* image = new unsigned char [w * h * 3];
        thread* threads = new thread[corenum];
        int level = h/corenum;
        for(int i = 0; i < corenum; ++i){
            const int min_h = i * level;
            const int max_h = (i == corenum-1)? h : (i+1)*level; 
            threads[i] = thread(&RenderThread, camera, min_h, max_h, scene, corenum, ref(image));
        }
        for(int i = 0; i< corenum; ++i){
            threads[i].join();
        }
        delete[] threads;
        write_ppm(camera.image_name.c_str(), image, camera.image_width, camera.image_height);
    } 
    return 0;
}
