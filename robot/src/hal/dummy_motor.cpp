//
// Created by Lea Wü on 28.03.21.
//

#include <iostream>
#include "hal/dummy_motor.h"

//this dummy_motor is for testing without real motor

//Constructor
dummy_motor::dummy_motor() {

}

//Destructor
dummy_motor::~dummy_motor() {

}

void dummy_motor::start(bool forward, uint8_t speed) {
    if(forward){
        std::cout << "[This is the Motor simulation:] The motor started and the robot is moving forward" << std::endl;
    } else{ //backwards
        std::cout << "[This is the Motor simulation:] The motor started and the robot is moving backwards" << std::endl;
    }

}

void dummy_motor::start(bool forward, uint8_t speed, uint16_t time) {
    if(forward){
        std::cout << "[This is the Motor simulation:] The motor started and the robot is moving forward" << std::endl;
    } else{ //backwards
        std::cout << "[This is the Motor simulation:] The motor started and the robot is moving backwards" << std::endl;
    }

}

void dummy_motor::stop() {
    std::cout << "[This is the Motor simulation:] The motor stopped and the robot isn't moving any more" << std::endl;
}
