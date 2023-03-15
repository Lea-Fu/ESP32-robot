//
// Created by Lea Wü on 28.03.21.
//

#include <iostream>
#include "hal/dummy_display.h"

//the dummy_display is for testing without a real display

//Constructor
dummy_display::dummy_display() {

}

//Destructor
dummy_display::~dummy_display() {

}

void dummy_display::print(std::string message) {
    std::cout << "[This is the Display simulation:] " << message.c_str() << std::endl;
}