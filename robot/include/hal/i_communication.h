//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_I_COMMUNICATION_H
#define ROBOT_I_COMMUNICATION_H

#include <string>
#include <cstdint>

//Interface for the communication (WLAN and bluetooth)
class i_communication{

public:
    //Destructor
    //virtual ~i_communication() = 0;

    virtual void connect_wlan() = 0;
    virtual void connect_bluetooth() = 0;
    virtual bool send_message_wlan(uint8_t * message, size_t size) = 0;
    virtual bool send_message_bluetooth(uint8_t * message, size_t size) = 0;

    //Callbacks with function pointers (to get message) [https://stackoverflow.com/questions/2298242/callback-functions-in-c]
    typedef void  (*message_callback_t) (std::string);
    virtual void bind_callback_wlan(message_callback_t callback) = 0;
    virtual void bind_callback_bluetooth(message_callback_t callback) = 0;
};

#endif //ROBOT_I_COMMUNICATION_H
