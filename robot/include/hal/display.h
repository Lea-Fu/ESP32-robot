//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST
#ifndef ROBOT_DISPLAY_H
#define ROBOT_DISPLAY_H

#include "i_display.h"
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <thread>
#include <mutex>
#include <condition_variable>

class display : public i_display {

private:
    std::string string;
    std::mutex mutex;
    volatile bool ready;
    std::condition_variable cv;
    Adafruit_SSD1306 *disp;

public:
    //Constructor
    display();
    //Destructor
    ~display();
    void print(std::string message) override;
    void update();
};


#endif //ROBOT_DISPLAY_H
#endif