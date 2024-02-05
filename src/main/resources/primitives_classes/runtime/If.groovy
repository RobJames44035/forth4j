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

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

class If extends AbstractRuntime {
// : test 5 = if ." Five " else ." Not Fove " then ;
    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        Integer conditionValue = interpreter.dataStack.pop() as Integer
        parentWord.executionIndex = word.executionIndex + 1; // Move to next word following 'if'

        if (conditionValue == 0) {
            // If condition is not met, skip words until 'else' or 'then' is found
            while (parentWord.forthWords.size() > parentWord.executionIndex) {
                Word nxtWord = interpreter.wordService.findByName(parentWord.forthWords.get(parentWord.executionIndex))
                if (nxtWord.name.equals("else") || nxtWord.name.equals("then")) {
                    if (nxtWord.name.equals("else")) {
                        // Match found, push conditionValue back onto the stack for 'else' statement
                        interpreter.dataStack.push(conditionValue)
                    }
                    break
                }
                parentWord.executionIndex++
            }
        }
        return null
    }
}
