package com.rajames.forth.memory

import org.springframework.stereotype.Component

@Component
class MemoryAddress {

    private Integer address
    private Integer value

    int getAddress() {
        return address
    }

    void setAddress(Integer address) {
        this.address = address
    }

    int getValue() {
        return value
    }

    void setValue(int value) {
        this.value = value
    }
}
