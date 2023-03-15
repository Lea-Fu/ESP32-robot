package com.example.app;

/**
 * Class to represent xy-coordinates
 */
public class Position {
    int xPosition;
    int yPosition;

    /**
     * Constructor
     * @param xPosition x position
     * @param yPosition y position
     */
    public Position(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * Copy constructor
     * @param position The object to copy
     */
    public Position(Position position) {
        xPosition = position.xPosition;
        yPosition = position.yPosition;
    }
}
