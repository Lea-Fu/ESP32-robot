//
// Created by Lea Wü on 28.03.21.
//

#include <iostream>
#include <string.h>
#include "dispatcher.h"

//Constructor
dispatcher::dispatcher(image_preprocessing &image_preprocessing, i_motor &motor1, i_motor &motor2, i_display &display,
                       i_communication &communication) :
        preprocessing(image_preprocessing),
        motor1(motor1),
        motor2(motor2),
        display(display),
        communication(communication) {
    send_images = false;
}

//Destructor
dispatcher::~dispatcher() {

}

//get image from camera/preprocessing and send it to communication
//not quite TCP but a better UDP now
void dispatcher::dispatch() {
    if (send_images) {
        i_camera::image_t image = preprocessing.get_image();
        std::cout << image.len << std::endl;

        //image is 9.216 KB thats to big, just 0.6KB allowed, so need to put it in seperate packages
        int header[4];
        header[0] = 0;
        header[1] = (image.len + 500) / 500; //Ceiling division
        header[2] = 96;
        header[3] = 96;
        //header (sequence number of package, size of packages that need to be send, pixelsize x, pixelsize y)
        communication.send_message_bluetooth((uint8_t *) &header, sizeof(header));

        // 500 byte + 4 byte for the header of each package with the sequence number in it
        char package[504];

        //j is the sequence number for the package
        for (int i = 0, j = 1; i < image.len; i += 500, j++) {
            if (image.len - i >= 500) {
                *((int *) package) = j;
                if (memcpy(package + 4, image.buf + i, 500) == nullptr) {
                    std::cout << "memcpy failed" << std::endl;
                } else {
                    communication.send_message_bluetooth((uint8_t *) package, 504);
                }
            } else {
                *((int *) package) = j;
                if (memcpy(package + 4, image.buf + i, image.len - i) == nullptr) {
                    std::cout << "memcpy failed" << std::endl;
                } else {
                    communication.send_message_bluetooth((uint8_t *) package, image.len - i + 4);
                }
            }
        }
    }
}

//for callback messages (checks the header and decide what to do)
void dispatcher::callback(std::string message) {
    std::cout << "Got message: " << message << std::endl;
    switch (message[0]) {
        case '0':
            switch (message[1]) {
                case '0':
                    send_images = true;
                    break;
                case '1':
                    send_images = false;
                    break;
            }
            break;
        case '1':
            switch (message[1]) {
                case '0':
                    if (message.length() == 6) {
                        //build 16 bit value from two 8 bit values, by shifting byte 5 8 bits to the left and mask it with byte 4 (little endian)
                        motor1.start(false, message[3], (message[5] << 8) | message[4]);
                    } else {
                        motor1.start(false, message[3]);
                    }
                    break;
                case '1':
                    if (message.length() == 6) {
                        motor1.start(true, message[3], (message[5] << 8) | message[4]);
                    } else {
                        motor1.start(true, message[3]);
                    }
                    break;
                case '2':
                    motor1.stop();
                    break;
                default:;
            }
            switch (message[2]) {
                case '0':
                    if (message.length() == 6) {
                        motor2.start(false, message[3], (message[5] << 8) | message[4]);
                    } else {
                        motor2.start(false, message[3]);
                    }
                    break;
                case '1':
                    if (message.length() == 6) {
                        motor2.start(true, message[3], (message[5] << 8) | message[4]);
                    } else {
                        motor2.start(true, message[3]);
                    }
                    break;
                case '2':
                    motor2.stop();
                    break;
                default:;
            }
            break;
        case '3':
            std::cout << "Substring: " << message.substr(1).c_str() << std::endl;
            display.print(message.substr(1));
            break;
        default:;
    }
}