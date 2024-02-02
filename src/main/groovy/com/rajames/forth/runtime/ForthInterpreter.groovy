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
//file:noinspection GroovyUnusedAssignment

package com.rajames.forth.runtime

import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService
import com.rajames.forth.memory.DataStack
import com.rajames.forth.memory.Memory
import com.rajames.forth.memory.ReturnStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

@Component
class ForthInterpreter {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    String line
    Word word
    String token
    String noWord

    Queue<String> tokens = new ConcurrentLinkedQueue<>()
    Queue<Word> words = new ConcurrentLinkedQueue<>()
    Queue<String> nonWords = new ConcurrentLinkedQueue<>()

    @Autowired
    Memory memory

    @Autowired
    DataStack dataStack

    @Autowired
    ReturnStack returnStack

    @Autowired
    WordService wordService

    @Autowired
    ForthCompiler forthCompiler

    boolean interpretAndExecute(String line) {
        this.line = line.toLowerCase().trim() as String
        this.tokens = new LinkedList<>(line.tokenize())
        boolean forthOutput = false
        try {
            log.trace("Interpreting line ${line}.")
            while (!tokens.isEmpty()) {
                this.token = tokens.remove()

                this.word = null
//                log.debug("Interpreter: Word before 'wordService.findByName(${token})' this?.word?.name = ${this?.word?.name} this?.word?.forthWords = ${this?.word?.forthWords}")
                this.word = wordService.findByName(token)
//                log.debug("Interpreter: Word after 'wordService.findByName(${token})' this?.word?.name = ${this?.word?.name} this?.word?.forthWords = ${this?.word?.forthWords}")

                if (word != null) {
                    words.add(word)
                } else {
                    try {
                        dataStack.push(Integer.parseInt(token) as Integer)
                    } catch (NumberFormatException ignored) {
                        nonWords.add(token)
                    }
                }
            }
        } catch (Exception e) {
            log.error("Invalid Input or Undefined Word: ${tokens}\n\t" + e.message, e)
        }

        while (!words.isEmpty()) {
            Word exec = words.remove()
            forthOutput = executeWord(exec)
        }

        log.trace("Interpreted ${line}")
        return forthOutput
    }


    boolean executeWord(Word word) {
        if (word.compileOnly) {
            throw new ForthInterpreterException("Compile Only.")
        }
        if (word.runtimeClass != null) {
            return executePrimitiveWord(word)
        } else if (word.forthWords.size() > 0) {
            return executeComplexWord(word)
        } else {
            // Should be unreachable.
            return false
        }
    }

    private boolean executePrimitiveWord(Word word) {
        Boolean forthOutput = false
        try {
            String runtimeClass = word?.runtimeClass?.trim()
            if (runtimeClass != null && !word.runtimeClass.isEmpty()) {
                def classLoader = new GroovyClassLoader()
                Class groovyClass = classLoader.parseClass(runtimeClass)
                RunTime runTime = groovyClass.getDeclaredConstructor().newInstance() as RunTime
                forthOutput = runTime.execute(this, word)
                if (forthOutput == null) {
                    forthOutput = false
                } // edge cases here
            }
        } catch (ForthInterpreterException interpreterException) {
            log.error(interpreterException.message)
        } catch (ForthCompilerException compilerException) {
            log.error(compilerException.message, compilerException)
        }
        return forthOutput
    }

    private boolean executeComplexWord(Word word) {
        boolean forthOutput = false
//        log.debug("Interpreter output: " + wordService?.findByName(this?.word?.name as String)?.forthWords)
        word.forthWords.each { Word childWord ->
            if (childWord != null) {
                if (childWord?.runtimeClass) {
                    forthOutput = executePrimitiveWord(childWord)
                } else {
                    forthOutput = executeComplexWord(childWord)
                }
            }
        }
        return forthOutput
    }
}
