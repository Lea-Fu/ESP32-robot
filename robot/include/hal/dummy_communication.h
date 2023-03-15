//
// Created by Lea Wü on 28.03.21.
//

#ifndef ROBOT_DUMMY_COMMUNICATION_H
#define ROBOT_DUMMY_COMMUNICATION_H

#include "i_communication.h"

class dummy_communication : public i_communication {

private:
    message_callback_t bt_callback;

public:
    //Constructor
    dummy_communication();
    //Destructor
    ~dummy_communication();
    void connect_wlan() override;
    void connect_bluetooth() override;
    bool send_message_wlan(uint8_t * message, size_t size) override;
    bool send_message_bluetooth(uint8_t * message, size_t size) override;
    void bind_callback_wlan(message_callback_t callback) override;
    void bind_callback_bluetooth(message_callback_t callback) override;

    void onWrite(std::string message);
};


#endif //ROBOT_DUMMY_COMMUNICATION_H
