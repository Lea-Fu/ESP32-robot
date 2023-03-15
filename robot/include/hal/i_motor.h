//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_I_MOTOR_H
#define ROBOT_I_MOTOR_H


//Interface for the motor
class i_motor{

public:
    //Destructor
    //virtual ~i_motor() = 0;

    //should start moving
    virtual void start(bool forward, uint8_t speed) = 0;
    virtual void start(bool forward, uint8_t speed, uint16_t time) = 0;

    //should stop moving
    virtual void stop() = 0;
};

#endif //ROBOT_I_MOTOR_H
