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

    boolean interpretAndExecute(String line) {
        this.line = line.toLowerCase() as String
        this.tokens = line.tokenize() as Queue<String>
        boolean forthOutput = false
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
                    forthOutput = executeWord(word)
                }
            }
        } catch (Exception ignored) {
            log.error("Invalid Input or Undefined Word")
        }
        log.debug("Interpreted ${line}")
        return forthOutput
    }

    private boolean executeWord(Word word) {
        if (word.behaviorScript) {
            return executePrimitiveWord(word)
        } else {
            return executeComplexWord(word)
        }
    }

    private boolean executePrimitiveWord(Word word) {
        boolean forthOutput = false
        String behaviorScript = word?.behaviorScript?.trim()
        if (behaviorScript.startsWith("class")) {
            log.error("Holding off on this situation")
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
            } else {
                forthOutput = true
            }
        }
        return forthOutput
    }

    private boolean executeComplexWord(Word word) {
        boolean forthOutput = false
        log.error("We'll get here.")
        return forthOutput
    }

}
