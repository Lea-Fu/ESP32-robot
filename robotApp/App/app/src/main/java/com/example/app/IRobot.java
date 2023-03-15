package com.example.app;

/**
 * Interface to connect this software to the game logic. Currently unused.
 */
public interface IRobot {

    void choosePrey();

    Position calculateMovement();

    void move(Position newPosition);

    void showOnDisplay(String message);
}
