package com.rajames.forth.runtime

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

@Component
class Interpreter {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    String line
    Queue<String> tokens

    @Autowired
    Memory memory

    @Autowired
    DataStack dataStack

    @Autowired
    ReturnStack returnStack

    @Autowired
    WordService wordService

    Interpreter() {
    }

    void interpretAndExecute(String line) {
        this.line = line.toLowerCase() as String
        this.tokens = line.tokenize() as Queue<String>

        try {
            log.debug("Interpreting line ${line}.")
            tokens.each { String token ->
                Optional<Word> wordOptional = null
                Word word = null

                log.debug(token)
                wordOptional = wordService.findByName(token)
                if (wordOptional.isPresent()) {
                    word = wordOptional.get()
                }
                log.debug(word)
                if (!word) { // Assume it MUST be an integer.
                    dataStack.push(Integer.parseInt(token) as Integer)
                } else {
                    executeWord(word)
                }
            }
        } catch (Exception e) {
            log.error("TODO ", e)
        }
        log.debug("Interpreted ${line}")
    }

//    TODO
    private void executeWord(Word word) {
        if (word.behaviorScript) {
            executePrimitiveWord(word)
        } else {
            executeComplexWord(word)
        }
    }

    private void executePrimitiveWord(Word word) {
        String behaviorScript = word?.behaviorScript?.trim()
        if (behaviorScript.startsWith("class")) {
            log.info("Holding off on this situation")
        } else {
            GroovyShell shell = new GroovyShell()
            Bindings bindings = new SimpleBindings()
            Script script = null
            Integer argumentCount = word.argumentCount
            if (argumentCount == 0) {
                script = shell.parse(behaviorScript)
            } else {
                (0..<argumentCount).each {
                    bindings.put("arg${it + 1}" as String, dataStack.pop())
                }
                script = shell.parse(behaviorScript as String, bindings as Binding)
            }
            Object result = script.run()
            if (result) {
                dataStack.push(result)
            }
        }
    }

    private static void executeComplexWord(Word word) {
        log.info("We'll get here.")
    }

}
