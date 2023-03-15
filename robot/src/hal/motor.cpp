//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST

#include "hal/motor.h"

//Constructor
motor::motor(int pin1, int pin2, int pwmChannel1, int pwmChannel2) {
    this->pin1 = pin1;
    this->pin2 = pin2;
    this->pwmChannel1 = pwmChannel1;
    this->pwmChannel2 = pwmChannel2;

    pinMode(pin1, OUTPUT);
    pinMode(pin2, OUTPUT);

    ledcSetup(pwmChannel1, 100, 8);
    ledcSetup(pwmChannel2, 100, 8);

    ledcAttachPin(pin1, pwmChannel1);
    ledcAttachPin(pin2, pwmChannel2);

    //timer for the overloaded start function
    esp_timer_create_args_t oneshot_timer_args;
    oneshot_timer_args.callback = [](void *motor_p) {
        ((motor *) motor_p)->stop();
    };
    /* argument specified here will be passed to timer callback function */
    oneshot_timer_args.arg = (void *) (this);
    oneshot_timer_args.name = (String("motor") + String(pwmChannel1)).c_str();
    ESP_ERROR_CHECK(esp_timer_create(&oneshot_timer_args, &timer));
}

//Destructor
motor::~motor() {
    ESP_ERROR_CHECK(esp_timer_delete(timer));
}

void motor::start(bool forward, uint8_t speed) {
    if (forward) {
        ledcWrite(pwmChannel1, 0);
        ledcWrite(pwmChannel2, speed);
    } else { //backwards
        ledcWrite(pwmChannel2, 0);
        ledcWrite(pwmChannel1, speed);
    }
}

//overloaded function
void motor::start(bool forward, uint8_t speed, uint16_t time) {
    if (forward) {
        ledcWrite(pwmChannel1, 0);
        ledcWrite(pwmChannel2, speed);
    } else { //backwards
        ledcWrite(pwmChannel2, 0);
        ledcWrite(pwmChannel1, speed);
    }

    ESP_ERROR_CHECK(esp_timer_start_once(timer, time * 1000)); //milliseconds
}

void motor::stop() {
    ledcWrite(pwmChannel1, 0);
    ledcWrite(pwmChannel2, 0);
}

#endif