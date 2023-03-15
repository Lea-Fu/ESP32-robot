//
// Created by Lea Wü on 19.04.21.
//

#ifndef ROBOT_UNITTEST_TRANSPORT_H
#define ROBOT_UNITTEST_TRANSPORT_H

#include <stdio.h>

void unittest_uart_begin() {

}

void unittest_uart_putchar(char c) {
    putchar(c);
}

void unittest_uart_flush() {
    fflush(stdout);
}

void unittest_uart_end() {

}

#endif //ROBOT_UNITTEST_TRANSPORT_H
