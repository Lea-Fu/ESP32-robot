//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_DISPATCHER_H
#define ROBOT_DISPATCHER_H

#include <string>
#include "image_preprocessing.h"
#include "hal/i_motor.h"
#include "hal/i_display.h"
#include "hal/i_communication.h"

//dispatcher communicates with all classes
class dispatcher {

private:
    image_preprocessing &preprocessing;
    i_motor &motor1;
    i_motor &motor2;
    i_display &display;
    i_communication &communication;
    volatile bool send_images;

public:
    //Constructor //volatile: stop the compiler from optimizing
    dispatcher(image_preprocessing &image_preprocessing, i_motor &motor1, i_motor &motor2, i_display &display, i_communication &communication);
    //Destructor
    ~dispatcher();

    void dispatch();

    //for callback messages
    void callback(std::string message);
};


#endif //ROBOT_DISPATCHER_H
