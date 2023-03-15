//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST
#ifndef ROBOT_COMMUNICATION_H
#define ROBOT_COMMUNICATION_H

#include "i_communication.h"
#include <WiFi.h>
#include <BluetoothSerial.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEService.h>
#include <BLEAdvertising.h>
#include <BLECharacteristic.h>
#include <WString.h>
#include <Esp.h>

class communication : public i_communication, public BLECharacteristicCallbacks{

private:
    BLEServer *pServer;
    BLEService *pService;
    BLECharacteristic *pCharacteristic;
    BLEAdvertising *pAdvertising;
    message_callback_t bt_callback;

public:
    BluetoothSerial SerialBT;
    //Constructor
    communication();
    //Destructor
    ~communication();
    void connect_wlan() override;
    void connect_bluetooth() override;
    bool send_message_wlan(uint8_t * message, size_t size) override;
    bool send_message_bluetooth(uint8_t * message, size_t size) override;
    void bind_callback_wlan(message_callback_t callback) override;
    void bind_callback_bluetooth(message_callback_t callback) override;

    void onWrite(BLECharacteristic *pCharacteristic) override;
};


#endif //ROBOT_COMMUNICATION_H
#endif