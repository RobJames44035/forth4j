//file:noinspection GroovyAssignabilityCheck
package com.rajames.forth

import com.rajames.forth.memory.DataStack
import com.rajames.forth.memory.Memory
import com.rajames.forth.memory.ReturnStack
import com.rajames.forth.runtime.Interpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ForthRepl {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private Scanner scanner

    @Autowired
    private Memory memory

    @Autowired
    private DataStack dataStack

    @Autowired
    private ReturnStack returnStack

    @Autowired
    Interpreter interpreter

    ForthRepl() {
        this.scanner = new Scanner(System.in)
    }

    void run() {
        while (true) {
            print("forth> ")
            String line = this.scanner.nextLine().trim()

            if (line == "exit") {
                System.out.println("Goodbye!")
                break
            }

            interpreter.interpretAndExecute(line)
        }
        this.scanner.close()
    }
}
