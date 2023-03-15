//
// Created by Lea Wü on 19.04.21.
//

#ifdef UNIT_TEST

#include "hal/dummy_communication.h"
#include "hal/dummy_motor.h"
#include "hal/dummy_display.h"
#include "dispatcher.h"
#include <string>
#include <iostream>

int main(void) {

    //testing dummy_motor
    dummy_motor motor1;
    dummy_motor motor2;

    motor1.start(true);
    motor2.start(true);
    motor1.stop();
    motor2.stop();
    motor1.start(false);
    motor2.start(false);
    motor1.stop();
    motor2.stop();

    //testing dummy_display
    dummy_display display;

    display.print("Hello testing. It is working. This is a message!");

    //testing communication
    dummy_communication communication;
    communication.connect_wlan();
    communication.connect_bluetooth();
    std::string message = "image";
    communication.send_message_wlan((uint8_t*)message.c_str(), message.size());
    communication.send_message_bluetooth((uint8_t*)message.c_str(), message.size());

    //testing dispatcher
    dispatcher disp(display, communication);
    while(true) {
        //testing dispatcher display and communication
        std::string message;
        std::cin >> message;
        disp.callback(message);
    }
}

#endif