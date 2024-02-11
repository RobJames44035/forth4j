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

import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.memory.DataStack
import com.rajames.forth.memory.Memory
import com.rajames.forth.memory.ReturnStack
import com.rajames.forth.runtime.ForthInterpreter
import com.rajames.forth.runtime.ForthInterpreterException
import com.rajames.forth.util.DatabaseBackupService
import org.apache.groovy.groovysh.Groovysh
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.codehaus.groovy.tools.shell.IO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component

/**
 * forth4j REPL
 */
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

    @Autowired
    DatabaseBackupService databaseBackupService

    ForthRepl() {
        this.scanner = new Scanner(System.in)
    }

    /**
     * Execute the REPL
     */
    void run() {
        Boolean forthOutput
        printPreamble()

        while (true) {
            String line = this.scanner.nextLine().trim()

            if (line == "bye") {
                databaseBackupService.backupDatabase(null, null)
                println("Goodbye!")
                break
            } else if (line == "gsh") {
                Binding binding = new Binding()
                AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.rajames.forth")
                String[] allBeanNames = context.getBeanDefinitionNames()

                allBeanNames.each { beanName ->
                    Object bean = context.getBean(beanName)
                    binding.setProperty(beanName, bean)
                }

                Groovysh groovysh = new Groovysh(binding, new IO())

                groovysh.run(null, [])
                continue
            }

            try {
                forthOutput = interpreter.interpretAndExecute(line)
                resetInterpreter()
                resetCompiler()
            } catch (ForthInterpreterException ex) {
                log.error("Error: ${ex?.message}")
            }

            if (forthOutput) {
                println()
            }

            print("\u001B[1A")  // Move cursor up one line
            print("\u001B[" + (line.length()) + "C")  // Move cursor to the end of existing user input + 2 spaces
            print("\033[94m ok\033[0m\n")
        }

        this.scanner.close()
        System.exit(0)
    }

    /**
     * Reset the interpreter to it's known starting state.
     */
    private void resetInterpreter() {
        interpreter.tokens.clear()
        interpreter.words.clear()
        interpreter.token = null
        interpreter.instructionPointer = 0
    }

    /**
     * Reset the compiler to it's known starting state.
     */
    private void resetCompiler() {
        ForthCompiler compiler = interpreter.forthCompiler
        compiler.newWord = null
        compiler.literal = null
        compiler.nextTokenToCompile = null
        compiler.forthWordsBuffer = new ArrayList<String>()
    }

    /**
     * Print the starting Banner
     */
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
}
