//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_DUMMY_DISPLAY_H
#define ROBOT_DUMMY_DISPLAY_H

#include "i_display.h"

class dummy_display : public i_display{

public:
    //Constructor
    dummy_display();
    //Destructor
    ~dummy_display();
    void print(std::string message) override;
};


#endif //ROBOT_DUMMY_DISPLAY_H
