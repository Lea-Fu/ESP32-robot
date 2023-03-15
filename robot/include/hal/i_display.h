//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_I_DISPLAY_H
#define ROBOT_I_DISPLAY_H

#include <string>
#include <cstdint>

//Interface for the display
class i_display{

public:
    //Destructor
    //virtual ~i_display() = 0;

    virtual void print(std::string message) = 0;
};

#endif //ROBOT_I_DISPLAY_H
