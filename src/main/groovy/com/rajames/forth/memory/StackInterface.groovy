package com.rajames.forth.memory

interface StackInterface {
    void push(Object value)

    Object pop()
    void clear()
    int size()
}
