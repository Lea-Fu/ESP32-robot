//
// Created by Lea Wü on 28.03.21.
//

#include <iostream>
#include <utility>
#include "hal/dummy_communication.h"

//this dummy_communication is for communication without a real connection

//Constructor
dummy_communication::dummy_communication() {

}

//Destructor
dummy_communication::~dummy_communication() {

}

void dummy_communication::connect_wlan() {
    std::cout << "[This is the Communication simulation:] You are connected with WLAN" << std::endl;
}

void dummy_communication::connect_bluetooth() {
    std::cout << "[This is the Communication simulation:] You are connected with Bluetooth" << std::endl;
}

bool dummy_communication::send_message_wlan(uint8_t * message, size_t size) {
    std::cout << "[This is the Communication simulation:] This is a message send via WLAN: " << message << std::endl;
}

bool dummy_communication::send_message_bluetooth(uint8_t * message, size_t size) {
    std::cout << "[This is the Communication simulation:] This is a message send via Bluetooth: " << message << std::endl;
}

void dummy_communication::bind_callback_wlan(dummy_communication::message_callback_t callback) {

}

void dummy_communication::bind_callback_bluetooth(dummy_communication::message_callback_t callback) {
    bt_callback = callback;
}

void dummy_communication::onWrite(std::string message) {
    //move constructor gets called, so that it's not a copy
    bt_callback(std::move(message));
}
