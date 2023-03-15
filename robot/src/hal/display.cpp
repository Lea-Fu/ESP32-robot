//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST
#include <iostream>
#include "hal/display.h"

#define SDA_PIN 14
#define SCL_PIN 15

//Constructor
display::display() {
    Wire.begin(SDA_PIN, SCL_PIN);
    disp = new Adafruit_SSD1306(128, 64, &Wire);
    disp->begin(SSD1306_SWITCHCAPVCC, 0x3C);
    disp->setTextSize(1); // Normal 1:1 pixel scale
    disp->setTextColor(SSD1306_WHITE);

    ready = false;
}

//Destructor
display::~display() {

}

void display::print(std::string message) {
    std::unique_lock<std::mutex> lk(mutex);
    // From now on, the current thread is the only one that can access
    cv.wait(lk, [this](){return !ready;});
    string = message;
    ready = true;
    lk.unlock();
    // other threads can lock the mutex now
}

//this has to be called in the main loop, because the library (Adafruit -> now and Thingpulse -> tried before) is not thread safe
void display::update() {

    if(mutex.try_lock()){

        disp->clearDisplay();
        disp->setCursor(0,32);
        disp->print(string.c_str());
        disp->display();

        ready = false;
        mutex.unlock();
        // other threads can lock the mutex now
        cv.notify_all();
    }

}

#endif