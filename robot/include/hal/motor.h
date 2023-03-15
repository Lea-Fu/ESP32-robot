//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST
#ifndef ROBOT_MOTOR_H
#define ROBOT_MOTOR_H

#include <Arduino.h>
#include <tuple>
#include "i_motor.h"

class motor : public i_motor{

public:
    //Constructor
    motor(int pin1, int pin2, int pwmChannel1, int pwmChannel2);
    //Destructor
    ~motor();
    void start(bool forward, uint8_t speed) override;
    void start(bool forward, uint8_t speed, uint16_t time) override;
    void stop() override;

private:
    int pin1;
    int pin2;
    int pwmChannel1;
    int pwmChannel2;
    esp_timer_handle_t timer;
};


#endif //ROBOT_MOTOR_H
#endif