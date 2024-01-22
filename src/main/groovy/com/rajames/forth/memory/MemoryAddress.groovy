package com.rajames.forth.memory

class MemoryAddress {

    private final int address
    private int value

    MemoryAddress(int address, int value) {
        this.address = address
        this.value = value
    }

    int getAddress() {
        return address
    }

    int getValue() {
        return value
    }

    void setValue(int value) {
        this.value = value
    }
}
