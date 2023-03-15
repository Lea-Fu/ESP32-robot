//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_I_CAMERA_H
#define ROBOT_I_CAMERA_H

#include <cstdint>
#include <cstddef>

//Interface for the camera
class i_camera {

public:
    //the type for the image, consisting of the buffer(the image) and the length in byte
    typedef struct {
        uint8_t *buf;
        size_t len;
    } image_t;
    //Destructor
    //virtual ~i_camera() = 0;

    virtual image_t get_image() = 0;

};


#endif //ROBOT_I_CAMERA_H
