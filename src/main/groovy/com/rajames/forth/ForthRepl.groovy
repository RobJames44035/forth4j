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

//file:noinspection GroovyAssignabilityCheck
//file:noinspection GroovyUnusedAssignment
package com.rajames.forth

import com.rajames.forth.memory.DataStack
import com.rajames.forth.memory.Memory
import com.rajames.forth.memory.ReturnStack
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.groovy.groovysh.Groovysh
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
    ForthInterpreter interpreter

    ForthRepl() {
        this.scanner = new Scanner(System.in)
    }

    void run() {

        Boolean forthOutput

        printPreamble()

        while (true) {
            String line = this.scanner.nextLine().trim()

            if (line == "bye" || line == "exit") {
                println("Goodbye!")
                break
            } else if (line == "gsh") {
                Groovysh groovysh = new Groovysh()
                groovysh.run(null, [])
                continue
            }

            try {
                forthOutput = interpreter.interpretAndExecute(line)
            } catch (Exception ex) {
                log.error(ex.message)
            }

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
        println("=================================================================")
        println("|      forth4j 1.0.0, Copyright (C) 2024 Robert A. James.       |")
        println("|          forth4j comes with NO ABSOLUTELY WARRANTY.           |")
        println("| For details see `https://www.apache.org/licenses/LICENSE-2.0' |")
        println("=================================================================")
        println("Type `bye' to exit.")
    }

    static void printPreamble2() {
        println(ForthRepl.class.getClassLoader().getResource("preamble.txt").text)
    }
}
