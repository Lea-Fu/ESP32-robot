//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_DUMMY_CAMERA_H
#define ROBOT_DUMMY_CAMERA_H

#include "i_camera.h"

class dummy_camera : public i_camera{

private:
    image_t image;
    int imageNumber;
    char fileNumber[6];

public:
    //Constructor
    dummy_camera();
    //Destructor
    ~dummy_camera();
    image_t get_image() override;
};


#endif //ROBOT_DUMMY_CAMERA_H
