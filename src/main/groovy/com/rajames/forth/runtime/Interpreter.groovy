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
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class Interpreter {

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

    Interpreter() {
    }

    boolean interpretAndExecute(String line) {
        this.line = line.toLowerCase() as String
        this.tokens = new LinkedList<>(line.tokenize())
        boolean forthOutput = false
        try {
            log.debug("Interpreting line ${line}.")
            // New
            while (!tokens.isEmpty()) {
                Optional<Word> wordOptional = null
                Word word = null
                String token = tokens.remove()

                wordOptional = wordService.findByName(token)
                if (wordOptional.isPresent()) {
                    word = wordOptional.get()
                }

                if (word != null) {
                    forthOutput = executeWord(word)
                } else {
                    try {
                        dataStack.push(Integer.parseInt(token) as Integer)
                    } catch (NumberFormatException ignored) {
                        break
                    }
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
            String tryS = "try {\n"
            String catchS = "\n} catch(Exception e) {\ne.printStackTrace()\n}"
            behaviorScript = tryS + behaviorScript + catchS
//            println(behaviorScript) // TODO Remove later. IMPORTANT!
            GroovyShell shell = new GroovyShell()
            Bindings bindings = new SimpleBindings()
            bindings.put("line", line)
            bindings.put("tokens", tokens)
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
            if (result) {
                if (result instanceof Boolean) {
                    dataStack.push(result ? -1 : 0)
                } else {
                    dataStack.push(result)
                }
            } else {
                forthOutput = true
            }
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
