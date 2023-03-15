//
// Created by Lea Wü on 28.03.21.
//
#ifndef UNIT_TEST
#include <iostream>
#include "hal/communication.h"
//#include <soc/soc.h>           // Disable brownout problems
//#include <soc/rtc_cntl_reg.h>  // Disable brownout problems

#define SERVICE_UUID        "476d099e-1ff4-43c0-9e36-23f4280ec5f7"
#define CHARACTERISTIC_UUID "19f220b8-2adf-4c46-867c-c75b208ba652"

//Constructor
communication::communication() {
    //WiFi.mode(WIFI_STA);
    //WiFi.disconnect();
    //SerialBT.begin("ESP32");

    //WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0); //disable brownout detector

    uint64_t MAC = ESP.getEfuseMac();
    char buffer[17];
    sprintf(buffer, "%016llx", MAC);
    std::string name = "Robot" + std::string(buffer);
    BLEDevice::init(name);

    pServer = BLEDevice::createServer();
    pService = pServer->createService(SERVICE_UUID);
    pCharacteristic = pService->createCharacteristic(
            CHARACTERISTIC_UUID,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE |
            BLECharacteristic::PROPERTY_NOTIFY |
            BLECharacteristic::PROPERTY_INDICATE
    );
    pCharacteristic->setCallbacks(this);
    pService->start();

    pAdvertising = pServer->getAdvertising();
    pAdvertising->start();
};

//Destructor
communication::~communication() {

}

void communication::connect_wlan() {
//scan wifi networks and show them
    // WiFi.scanNetworks will return the number of networks found
    int n = WiFi.scanNetworks();
    Serial.println("scan done");
    if (n == 0) {
        Serial.println("no networks found");
    } else {
        Serial.print(n);
        Serial.println(" networks found");
        for (int i = 0; i < n; ++i) {
            // Print SSID and RSSI for each network found
            Serial.print(i + 1);
            Serial.print(": ");
            Serial.print(WiFi.SSID(i));
            Serial.print(" (");
            Serial.print(WiFi.RSSI(i));
            Serial.print(")");
            Serial.println((WiFi.encryptionType(i) == WIFI_AUTH_OPEN)?" ":"*");
        }
    }
    Serial.println("");
}

void communication::connect_bluetooth() {
    //you can now find the esp in your bluetooth settings on the phone
    if (Serial.available()) {
        SerialBT.write(Serial.read());
    }
    if (SerialBT.available()) {
        Serial.write(SerialBT.read());
    }
}

bool communication::send_message_wlan(uint8_t * message, size_t size) {

}

bool communication::send_message_bluetooth(uint8_t * message, size_t size) {
    if(pServer->getConnectedCount() > 0){
        pCharacteristic->setValue(message, size);
        pCharacteristic->notify();
    }
}

void communication::bind_callback_wlan(communication::message_callback_t callback) {

}

void communication::bind_callback_bluetooth(communication::message_callback_t callback) {
    bt_callback = callback;
}

void communication::onWrite(BLECharacteristic *pCharacteristic) {
    bt_callback(pCharacteristic->getValue());
}

#endif