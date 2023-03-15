//
// Created by Lea Wü on 28.03.21.
//
#include "image_preprocessing.h"
#include "hal/camera.h"


image_preprocessing::image_preprocessing(i_camera &camera) :
                                        camera(camera)
                                        {}

i_camera::image_t image_preprocessing::get_image() {
    return camera.get_image();
}


