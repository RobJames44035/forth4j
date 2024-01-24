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
        printPreamble()

        while (true) {
            String line = this.scanner.nextLine().trim()

            if (line == "bye") {
                println("Goodbye!")
                break
            }

            boolean forthOutput = interpreter.interpretAndExecute(line)

            // TODO
            if (forthOutput) {
                println()
            }

            print("\u001B[1A")  // Move cursor up one line
            print("\u001B[" + (line.length()) + "C")  // Move cursor to the end of existing user input + 2 spaces
            print(" ok\n")

        }

        this.scanner.close()
        System.exit(0)
    }

    static void printPreamble() {
        print("\u001B[2J")
        print("\u001B[H")
        println("================================================================")
        println("|      forth4j 1.0.0, Copyright (C) 2024 Robert A. James.      |")
        println("|          forth4j comes with NO ABSOLUTELY WARRANTY.          |")
        println("| For details see `http://www.apache.org/licenses/LICENSE-2.0' |")
        println("================================================================")
        println("Type `bye' to exit.")
    }
}
