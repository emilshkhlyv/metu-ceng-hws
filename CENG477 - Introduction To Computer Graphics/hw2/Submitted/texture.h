#pragma once
#include <string>
// #include "jpeg.h"
#include "vector3f.h"

namespace fst
{
    class Texture
    {
    public:
        Texture();
        Texture(int width,
                int height,
                unsigned char *image,
                std::string imageName,
                std::string interpolation,
                std::string decalMode,
                std::string appearance);
        std::string getDecalMode(){return m_decalMode;}
        std::string getAppearance(){return m_appearance;}
        int getWidth(){return m_width;}
        int getHeight(){return m_height;}
        std::string getInterpolation(){return m_interpolation;}
        unsigned char* getImage(){return m_image;}
        std::string getImageName(){return m_imageName;}
        
        math::Vector3f nearest(float i, float j){
            unsigned int val = ((int) floor(j))*m_width*3 + ((int) floor(i))*3;
            math::Vector3f color;
            color.x = m_image[val];
            color.y = m_image[val+1];
            color.z = m_image[val+2];
            return color;
        }
        
        math::Vector3f bilinear(float i, float j){
            unsigned int val = ((int) floor(j))*m_width*3 + ((int) floor(i))*3;

            unsigned int p = (unsigned int) floor(i);
            unsigned int q = (unsigned int) floor(j);

            unsigned int dx = i - p;
            unsigned int dy = j - q;

            math::Vector3f color;
            color.x      = m_image[val]               * (1-dx)    * (1-dy);
            color.x     += m_image[val+3]             * (dx)      * (1-dy);
            color.x     += m_image[val+3*m_width+3]   * (dx)      * (dy);
            color.x     += m_image[val+3*m_width]     * (1-dx)    * (dy);

            color.y      = m_image[val+1]             * (1-dx)    * (1-dy);
            color.y     += m_image[val+4]             * (dx)      * (1-dy);
            color.y     += m_image[val+3*m_width+4]   * (dx)      * (dy);
            color.y     += m_image[val+3*m_width+1]   * (1-dx)    * (dy);
            
            color.z      = m_image[val+2]             * (1-dx)    * (1-dy);
            color.z     += m_image[val+5]             * (dx)      * (1-dy);
            color.z     += m_image[val+3*m_width+5]   * (dx)      * (dy);
            color.z     += m_image[val+3*m_width+2]   * (1-dx)    * (dy);
            return color;
        }

    private:
        int m_width;
        int m_height;
        unsigned char *m_image;
        std::string m_imageName;
        std::string m_interpolation;
        std::string m_decalMode;
        std::string m_appearance;
    };
}