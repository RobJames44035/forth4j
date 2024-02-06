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

/**
 * The forth4j interpreter.
 */
@Component
class ForthInterpreter {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    /**
     * This is the input
     */
    String line
    Word word
    String token
    Integer instructionPointer

    Queue<String> tokens = new ConcurrentLinkedQueue<>()
    Queue<Word> words = new ConcurrentLinkedQueue<>()

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


    /**
     * Main entry point from the REPL
     * @param line The line from REPL we are executing
     * @return A boolean flag to indicate if we print a blank line after execution or not.
     * The return value may be removed in the future when REPL is modified to better handle ANSI escape codes.
     * making for a "prettier UI."
     */
    boolean interpretAndExecute(String line) {

//        this.line = line.toLowerCase().trim() as String
//        this.tokens = new LinkedList<>(line.tokenize())
//        boolean forthOutput = false

        configureForthInterpreter(line)

        return execution()
    }

    /**
     * When we enter the interpreter with a line from REPL it needs to be broken out into
     * a number of things to prepare it for execution:
     * We iterate through all of the tokens in the line and set interpreter fields accordingly.
     *
     */
    private void configureForthInterpreter(String line) {
        this.instructionPointer = 0
        this.line = line.toLowerCase().trim() as String
        this.tokens = new LinkedList<>(line.tokenize())
        boolean forthOutput = false

        while (!tokens.isEmpty()) {
            this.token = tokens.poll()

            this.word = wordService.findByName(token)
            if (word != null) {
                words.add(word)
            } else {
                try {
                    dataStack.push(Integer.parseInt(token) as Integer)
                } catch (NumberFormatException e) {
                    throw new ForthInterpreterException("Opps! That word is not in the Dictionary!", e)
                }
            }
        }
    }


    /**
     * execution is the responsable method for executing all the forth words from a line of input.
     * @return see interpretAndExecute() for more
     */
    private Boolean execution() {
        Boolean forthOutput = null
        instructionPointer = 0
        while (!words.isEmpty()) {
            Word exec = words.poll()
            forthOutput = executeWord(exec, exec.parentWord)
        }
        return forthOutput
    }

    /**
     * This is called in the execution() method to actually execute a FORTH "WORD:
     * @param word This is the word we will be executing.
     * @return the forthOutput flag for REPL.
     */
    boolean executeWord(Word word, Word parentWord) {
        if (word.compileOnly) {
            throw new ForthInterpreterException("Compile Only.")
        }

        if (word.runtimeClass != null) {
            return executePrimitiveWord(word, parentWord)
        } else if (word.forthWords.size() > 0) {
            return executeComplexWord(word, parentWord)
        }
        return false
    }

    /**
     * A FORTH "word" may or may noy have a primitive runtime behavior/action. If the runtimeClass exists
     * in the words definition The class (thus the behavior) will be instantiated and the RunTime interface
     * execute method will be invoked.
     * @param word The primitive word to execute.
     * @return The boolean flag for REPL.
     */
    private boolean executePrimitiveWord(Word word, Word parentWord) {
        Boolean forthOutput = false
        try {
            String runtimeBehaviorClass = word?.runtimeClass?.trim()
            if (runtimeBehaviorClass != null && !runtimeBehaviorClass.isEmpty() && !runtimeBehaviorClass.isBlank()) {
                def classLoader = new GroovyClassLoader()
                Class groovyClass = classLoader.parseClass(runtimeBehaviorClass)
                IRuntime runTime = groovyClass.getDeclaredConstructor().newInstance() as IRuntime
                forthOutput = runTime.execute(this, word, parentWord)
                if (forthOutput == null) {
                    forthOutput = false
                } // edge cases here. We CAN return anything we want in reality.
            }
        } catch (ForthInterpreterException interpreterException) {
            log.error(interpreterException.message)
        } catch (ForthCompilerException compilerException) {
            log.error(compilerException.message, compilerException)
        }
        return forthOutput
    }

    /**
     * A complex/compound word is a List<String> of other words
     * already existing in the system. Since both primitive and complex/compound words may make up the definition
     * of any word this method is recursive. It will "unwind" itself until all "primitive" words are executed.
     * @param word the complex/compound word to execute.
     * @return same old boolean flag for REPL.
     */
    boolean executeComplexWord(Word word, Word parentWord) {
        word.executionIndex = 0
        Boolean output = false
        while (word.executionIndex < word.forthWords.size()) {

            Word nextWord = wordService.findByName(word.forthWords.get(word.executionIndex)) as Word
            if (nextWord.forthWords.size() > 0 && nextWord != null) {
                output = executeComplexWord(nextWord, word)
            } else if (nextWord.runtimeClass != null) {
                output = executePrimitiveWord(nextWord, word)
            }
            word.executionIndex++
        }
        // Reset index after executing this word
        word.executionIndex = 0
        return output
    }

}
