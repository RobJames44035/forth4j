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

import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.ConcurrentLinkedQueue

class Colon extends AbstractRuntime {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    @Override
    Object execute(ForthInterpreter interpreter, Word word) {
        // Fail Fast
        if (interpreter.words.stream().noneMatch(w -> w.getName().equals(";"))) {
            interpreter.words.clear()
            throw new ForthCompilerException("No ending semicolon")
        }

        ConcurrentLinkedQueue<String> nonWords = interpreter.nonWords
        ConcurrentLinkedQueue<Word> words = interpreter.words
        ConcurrentLinkedQueue<Integer> arguments = new ConcurrentLinkedQueue<>()

        while (interpreter.dataStack.size() > 0) {
            Integer argument = interpreter.dataStack.pop() as Integer
            arguments.add(argument)
        }

        Word w = interpreter.forthCompiler.compileWord(words, arguments, nonWords)
        w = interpreter.wordService.findByName(w.name)
        log.trace("Colon: Word after 'interpreter.wordService.findByName(w.name)' this?.word?.name = ${w?.name} this?.word?.forthWords = ${w?.forthWords}")
        return null
    }
}