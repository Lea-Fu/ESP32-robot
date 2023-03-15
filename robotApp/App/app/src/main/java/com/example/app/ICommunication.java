package com.example.app;

/**
 * this interface is an abstraction of the robot.
 */
public interface ICommunication {

    /**
     * Callback for the connection status
     */
    interface OnConnectionCallback {
        /**
         * Should be called if connection was successful.
         */
        void success();

        /**
         * Should be called on failures.
         * @param info Reason for the failure.
         */
        void failure(String info);
    }

    /**
     * Tries to establish a connection to the robot.
     * @param connectionCallback The callback for status.
     */
    void connect(OnConnectionCallback connectionCallback);

    /**
     * start motors
     * @param forward1 move motor1 forward
     * @param forward2 move motor2 forward
     * @param speed the speed of the motors
     */
    void motorStart(boolean forward1, boolean forward2, int speed);

    /**
     * start motors
     * overloaded method
     * @param forward1 move motor1 forward
     * @param forward2 move motor2 forward
     * @param speed the speed of the motors
     * @param time the duration the motors should be turned on
     */
    void motorStart(boolean forward1, boolean forward2, int speed, int time); //overloaded method

    /**
     * stop motors
     */
    void motorStop();

    /**
     * print on display
     * @param message the message to print
     */
    void printOnDisplay(String message);

    /**
     * Start sending images from robot to phone
     */
    void sendImages();

    /**
     * stop sending images from robot to phone
     */
    void stopSendImages();
}
