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

package compiler
//    : ?full 12 = if ." It's full " then ." Hello World" ;

import com.rajames.forth.compiler.AbstractCompile
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter

class IfC extends AbstractCompile {
    @Override
    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {

        List<String> tokens = newWord.getForthWords()
        Stack<Integer> ctrlFlowStack = new Stack<>()
        int currentIndex = 0

        for (token in compiler.forthWordsBuffer) {
            Word word = interpreter.wordService.findByName(token)
            if (word != null) {
                if (word.name == "if") {
                    // When we see an IF, we add a placeholder (use a unique string)
                    // where we'll later put a jump instruction
                    tokens[currentIndex] = "<placeholder for jump if false>"
                    // push the current index onto the control flow stack
                    ctrlFlowStack.push(currentIndex)
                } else if (word.name == "then") {
                    if (ctrlFlowStack.size() == 0) {
                        throw new ForthCompilerException("Mismatched THEN")
                    }
                    // We've encountered a THEN. Replace the jump placeholder with a real jump location
                    int ifIndex = ctrlFlowStack.pop()
                    String jumpTo = Integer.toString(currentIndex)
                    tokens[ifIndex] = jumpTo
                }
            }
            currentIndex++
        }

        // Check if we have any unmatched IFs
        if (!ctrlFlowStack.isEmpty()) {
            throw new ForthCompilerException("Unmatched IF")
        }

        newWord.setForthWords(tokens)
        return false
    }
}
