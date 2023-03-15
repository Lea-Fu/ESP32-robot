//
// Created by Lea Wü on 28.03.21.
//

#ifndef UNIT_TEST
#ifndef ROBOT_CAMERA_H
#define ROBOT_CAMERA_H

#include <esp_camera.h>
#include "i_camera.h"

class camera : public i_camera{

public:
    //Constructor
    camera();
    //Destructor
    ~camera();
    image_t get_image() override;
};


#endif //ROBOT_CAMERA_H
#endif
