//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_IMAGE_PREPROCESSING_H
#define ROBOT_IMAGE_PREPROCESSING_H

#include <string>
#include "hal/i_camera.h"

class image_preprocessing {

private:
    i_camera &camera;

public:
    //Constructor
    image_preprocessing(i_camera &camera);
    i_camera::image_t get_image();

};


#endif //ROBOT_IMAGE_PREPROCESSING_H
