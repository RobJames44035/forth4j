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

package primitives_classes.runtime

import com.rajames.forth.ForthException
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

class Word extends AbstractRuntime {
/**
 * Execute the FORTH word from the interpreter.
 * @param interpreter The FORTH interpreter instance.
 * @param word The word that is being executed.
 * @param parentWord It's parent word (if any).
 * @return An object of any type. By convention we are returning a Boolean to indicate if the REPL
 * should print a newline or not. If you do anything with a returned Object, be sure to set
 * forthOutput to to a Boolean for REPL.
 */
    @Override
    Object execute(ForthInterpreter interpreter, com.rajames.forth.dictionary.Word word, com.rajames.forth.dictionary.Word parentWord) {
        try {
            char chr = interpreter.dataStack.pop() as char
            Integer addr = 0
            // Read a line from input.
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
            String userInput = reader.readLine()

            // Split the input into words and take the first one.
            String[] words = userInput.split(new String(chr))
            String firstWord = words[0]

            // Convert the first word to bytes and store it in memory.
            byte[] wordBytes = firstWord.getBytes()
            // Allocate memory for the word + null terminator.
            wordBytes.eachWithIndex { byte b, int i ->
                interpreter.blockService.store(addr + i, b, true)
            }

            // Afterwards, add the null byte at the end of the word.
            interpreter.blockService.store(addr + wordBytes.length, (byte) 0x00, true)

            // Push the address of the word in memory to the stack.
            interpreter.dataStack.push(addr)
        }
        catch (Exception e) {
            throw new ForthException(e.message)
        }

        return null

    }
}
