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

package runtime

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

import java.util.concurrent.ConcurrentLinkedQueue

class If extends AbstractRuntime {
//    : ?full 12 = if ." It's full " then ;
    @Override
    Object execute(ForthInterpreter interpreter, Word word) {

        if (interpreter.dataStack.pop() != 0) {
            // execute tp to else or then
            ConcurrentLinkedQueue<Word> parentDefinition = interpreter.word.forthWords as ConcurrentLinkedQueue
            while (!parentDefinition.isEmpty()) {
                if (parentDefinition.remove() == word) {
                    break
                }
            }
            while (!parentDefinition.isEmpty()) {
                Word next = parentDefinition.remove()
                if (next.name == "else" || next.name == "then") {
                    break
                }
                interpreter.executeWord(next)
            }
        }
        return null
    }
}
