package com.example.app;

import org.opencv.core.Mat;

/**
 * Callback to update images.
 */
public interface ICommunicationCallback {
    /**
     * Should be called, whenever a new image arrived from the robot
     * @param image The image.
     */
    void waitForImage(Mat image);

}
