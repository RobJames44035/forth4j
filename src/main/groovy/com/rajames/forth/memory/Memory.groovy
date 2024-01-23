package com.rajames.forth.memory

import org.springframework.stereotype.Component

@Component
class Memory {
    protected Map<Integer, MemoryAddress> addressMap = [:]

    // Adds or updates a memory address
    void set(int address, int value) {
        addressMap[address] = new MemoryAddress(address, value)
    }

    // Retrieves a memory address or returns null if not set
    MemoryAddress get(int address) {
        return addressMap[address]
    }
}
