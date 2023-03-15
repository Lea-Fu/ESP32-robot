//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_DUMMY_MOTOR_H
#define ROBOT_DUMMY_MOTOR_H

#include "i_motor.h"

class dummy_motor : public i_motor {

public:
    //Constructor
    dummy_motor();
    //Destructor
    ~dummy_motor();
    void start(bool forward, uint8_t speed) override;
    void start(bool forward, uint8_t speed, uint16_t time) override;
    void stop() override;

};


#endif //ROBOT_DUMMY_MOTOR_H
