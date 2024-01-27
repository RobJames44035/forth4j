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

import javax.script.Bindings
import javax.script.SimpleBindings
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class ForthInterpreter {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    String line
    Queue<String> tokens = new ConcurrentLinkedQueue<>()

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
        this.line = line.toLowerCase() as String
        this.tokens = new LinkedList<>(line.tokenize())
        boolean forthOutput = false
        try {
            log.trace("Interpreting line ${line}.")
            while (!tokens.isEmpty()) {
                String token = tokens.remove()
                Word word = wordService.findByName(token)
                if (word != null) {
                    if (word.compileOnly) {
                        throw new ForthInterpreterException("Compile Only.")
                    }
                    forthOutput = executeWord(word)
                } else {
                    try {
                        dataStack.push(Integer.parseInt(token) as Integer)
                    } catch (NumberFormatException ignored) {
                        throw new ForthInterpreterException("Token is not a Word or Number")
                    }
                }
            }
        } catch (Exception e) {
            log.error("Invalid Input or Undefined Word:\n\t" + e.message, e)
        }
        log.trace("Interpreted ${line}")
        return forthOutput
    }


    private boolean executeWord(Word word) {
        if (word.behaviorScript) {
            return executePrimitiveWord(word)
        } else if (word.childWords.size() > 0) {
            return executeComplexWord(word)
        } else {
            // Should be unreachable.
            throw new ForthCompilerException("Nothing to do.")
        }
    }

    private boolean executePrimitiveWord(Word word) {
        Boolean forthOutput = false
        try {
            String behaviorScript = word?.behaviorScript?.trim()
            if (behaviorScript.startsWith("class")) {
                log.error("Holding off on this situation")
            } else {

                String tryS = "try {\n"
                String catchS = "\n} catch(Exception e) { log.error(e.message, e) }"
                behaviorScript = tryS + behaviorScript + catchS

                GroovyShell shell = new GroovyShell()
                Bindings bindings = new SimpleBindings()
                bindings.put("line", line)
                bindings.put("tokens", tokens)
                bindings.put("log", log)
                bindings.put("forthCompiler", forthCompiler)
                bindings.put("word", word)
                Script script = null
                Integer argumentCount = word.argumentCount
                if (argumentCount == 0) {
                    script = shell.parse(behaviorScript as String, bindings as Binding)
                } else {
                    (0..<argumentCount).each {
                        bindings.put("arg${it + 1}" as String, dataStack.pop())
                    }
                    script = shell.parse(behaviorScript as String, bindings as Binding)
                }
                Object result = script.run()
                if (result instanceof Boolean) {
                    dataStack.push(result ? -1 : 0)
                } else if (result) {
                    dataStack.push(result)
                } else {
                    forthOutput = true
                }
            }
        } catch (ForthInterpreterException interpreterException) {
            log.error(interpreterException.message)
        } catch (ForthCompilerException compilerException) {
            log.error(compilerException.message)
        }
        return forthOutput
    }

    private boolean executeComplexWord(Word word) {
        boolean forthOutput = false
        word.childWords.each { Word childWord ->
            if (childWord.behaviorScript) {
                forthOutput = executePrimitiveWord(childWord)
            } else {
                forthOutput = executeComplexWord(childWord)
            }
        }
        return forthOutput
    }

}
