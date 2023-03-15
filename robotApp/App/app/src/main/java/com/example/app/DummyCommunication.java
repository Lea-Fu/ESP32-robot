package com.example.app;

/**
 * A communication dummy for testing.
 */
public class DummyCommunication implements ICommunication {

    @Override
    public void connect(OnConnectionCallback connectionCallback) {
        System.out.println("[This is the DummyCommunication] Scanning BLE devices nearby.");
        System.out.println("[This is the DummyCommunication] Connected to the Robo.");
    }

    @Override
    public void motorStart(boolean forward1, boolean forward2, int speed) {
        if (forward1) {
            System.out.println("[This is the DummyCommunication] Motor1 started forward.");
        } else {
            System.out.println("[This is the DummyCommunication] Motor1 started backwards.");
        }

        if (forward2) {
            System.out.println("[This is the DummyCommunication] Motor2 started forward.");
        } else {
            System.out.println("[This is the DummyCommunication] Motor2 started backwards.");
        }
    }

    //overloaded method
    @Override
    public void motorStart(boolean forward1, boolean forward2, int speed, int time) {
        if (forward1) {
            System.out.println("[This is the DummyCommunication] Motor1 started forward.");
        } else {
            System.out.println("[This is the DummyCommunication] Motor1 started backwards.");
        }

        if (forward2) {
            System.out.println("[This is the DummyCommunication] Motor2 started forward.");
        } else {
            System.out.println("[This is the DummyCommunication] Motor2 started backwards.");
        }
    }

    @Override
    public void motorStop() {
        System.out.println("[This is the DummyCommunication] Motor1 and Motor2 stopped.");

    }

    @Override
    public void printOnDisplay(String message) {
        System.out.println("[This is the DummyCommunication] Printed this message: " + message);
    }

    @Override
    public void sendImages() {
        System.out.println("[This is the DummyCommunication] Sending Images from ESP32 Cam to the phone.");
    }

    @Override
    public void stopSendImages() {
        System.out.println("[This is the DummyCommunication] Stopped sending Images from ESP32 Cam to the phone.");
    }


}
