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

import com.rajames.forth.compiler.AbstractCompile
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter

class IfC extends AbstractCompile {
//    : ?full 12 = if ." It's full " then ;
    @Override
    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {
        for (word in interpreter.words) {
            if (word.name == "if" || word.name == "else") {
                compiler.ctrlFlowStack.push(word.name)
            } else if (word.name == "then") {
                if (compiler.ctrlFlowStack.isEmpty()) {
                    throw new ForthCompilerException("Unmatched THEN")
                }
            }
            String wordPopped = compiler.ctrlFlowStack.pop()
            if (wordPopped == "else" && compiler.ctrlFlowStack.isEmpty()) {
                throw new ForthCompilerException("Unmatched ELSE")
            }
            if (!compiler.ctrlFlowStack.isEmpty()) {
                throw new ForthCompilerException("Unmatched IF or ELSE")
            }
        }

        return false
    }
}
