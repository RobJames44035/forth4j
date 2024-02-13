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

package com.rajames.forth.memory.storage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockService {

    public static final int BLOCK_SIZE = 1024
    BlockRepository blockRepository

    @Autowired
    BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository
    }

    @Transactional
    Block save(Block block) {
        return blockRepository.save(block)
    }

    @Transactional
    Block getBlock(Integer blockNumber) {
        Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

        if (blockOptional.isPresent()) {
            return blockOptional.get()
        } else {
            Block newBlock = new Block()
            byte[] newBytes = new byte[BLOCK_SIZE]
            Arrays.fill(newBytes as Byte[], null)

            newBlock.setBlockNumber(blockNumber)
            newBlock.setBytes(newBytes)
            return blockRepository.save(newBlock)
        }
    }

    @Transactional
    Block putBlock(Integer blockNumber) {
        // Check if block with given blockNumber already exists
        Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

        Block block = blockOptional.orElseGet(Block::new)
        byte[] newBytes = new byte[BLOCK_SIZE]
        Arrays.fill(newBytes as Byte[], null)

        block.setBlockNumber(blockNumber)
        block.setBytes(fill)

        return blockRepository.save(block)
    }

    @Transactional
    Byte fetch(Integer address) {
        Integer blockNumber = (address / BLOCK_SIZE) as Integer
        Integer index = address % BLOCK_SIZE

        // Find block or create new one if it doesn't exist
        Block block = blockRepository.findByBlockNumber(blockNumber)
                .orElseGet(() -> {
                    byte[] newBytes = new byte[BLOCK_SIZE]
                    Arrays.fill(newBytes as Byte[], null)

                    Block newBlock = new Block()
                    newBlock.setBlockNumber(blockNumber)
                    newBlock.setBytes(newBytes)

                    return blockRepository.save(newBlock)
                })

        return block.getBytes()[index]
    }

    @Transactional
    void store(Integer address, Byte value) {
        int blockNumber = address / BLOCK_SIZE as Integer
        int index = address % BLOCK_SIZE

        // Find block or create new one if it doesn't exist
        Block block = blockRepository.findByBlockNumber(blockNumber)
                .orElseGet(() -> {
                    byte[] newBytes = new byte[BLOCK_SIZE]
                    Arrays.fill(newBytes as Byte[], null)

                    Block newBlock = new Block()
                    newBlock.setBlockNumber(blockNumber)
                    newBlock.setBytes(newBytes)

                    return blockRepository.save(newBlock)
                })

        byte[] bytes = block.getBytes()
        bytes[index] = value

        block.setBytes(bytes)
        blockRepository.save(block)
    }
}
