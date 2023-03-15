#ifndef UNIT_TEST //this means, this class is not for unit testing, because of arduino specific stuff

#include <Arduino.h>
#include <iostream>

#include "hal/communication.h"
#include "hal/motor.h"
#include "hal/display.h"
#include "hal/dummy_display.h"
#include "hal/camera.h"
#include "image_preprocessing.h"
#include "dispatcher.h"

#define MOTOR1_PIN1 2
#define MOTOR1_PIN2 14
#define MOTOR2_PIN1 13
#define MOTOR2_PIN2 12

static dispatcher *test_disp;

void setup() __attribute__((noreturn));
void setup() {
    Serial.begin(115200);
    delay(2000);

    std::cout << "Starting!" << std::endl;

    communication test_obj = communication();

    motor test_motor = motor(MOTOR1_PIN1, MOTOR1_PIN2, LEDC_CHANNEL_4, LEDC_CHANNEL_5);
    motor test_motor2 = motor(MOTOR2_PIN1, MOTOR2_PIN2, LEDC_CHANNEL_6, LEDC_CHANNEL_7);
    dummy_display test_display;
    camera test_camera = camera();
    image_preprocessing prepro(test_camera);
    test_disp = new dispatcher{prepro, test_motor, test_motor2, test_display, test_obj};
    communication::message_callback_t callback = [](std::string message){test_disp->callback(message);};
    test_obj.bind_callback_bluetooth(callback);

    while(true) {
        test_disp->dispatch();
        delay(100); //to avoid to much traffic (backpressure)
        //test_display.update();
    }
}

void loop() {

}

#endif