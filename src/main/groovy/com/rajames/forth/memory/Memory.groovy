/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.memory

import com.rajames.forth.runtime.ForthInterpreterException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component

@Component
class Memory {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    Map<Integer, MemoryAddress> addressMap = [:]

    Integer numOfBlks = 512
    Integer blockSize = 1024

    // Adds or updates a memory address
    void set(int address, int value) {
        addressMap[address, value]
    }

    // Retrieves a memory address or returns null if not set
    MemoryAddress get(int address) {
        return addressMap[address]
    }

    void init() {
        log.info("\tInitializing virtual memory...")
        println("\tInitializing virtual memory...")
        for (int i = 0; i < numOfBlks; i++) {
            for (int j = 0; j < blockSize; j++) {
                MemoryAddress memoryAddress = new MemoryAddress()
                int address = i * blockSize + j
                memoryAddress.address = address
                memoryAddress.value = null
                addressMap.put(address, memoryAddress)
            }
        }
    }

    Integer getAddressFromBlock(Integer blockNumber) {
        if (blockNumber < 0 || blockNumber >= addressMap.size() / blockSize) {
            throw new ForthInterpreterException("BlockNumber " + blockNumber + " is out of bounds.")
        }
        MemoryAddress startAddress = addressMap.get(blockNumber * blockSize)
        return startAddress.address
    }
}
