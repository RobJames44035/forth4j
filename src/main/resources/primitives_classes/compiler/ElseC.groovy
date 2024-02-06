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


/*
 * The class "ElseC" extends from "AbstractCompilerDirective", and is part of the compile-time mechanism for handling the 'ELSE'
 * control structure in the Forth-like language interpreter.
 *
 * During the compilation process, the compiler directive associated with 'ELSE' is activated upon encountering 'ELSE'.
 * The 'ElseC' class's 'execute' method processes all tokens following 'ELSE' until it encounters 'THEN'.
 * These tokens are sequentially translated into an executable form, to be interpreted by the runtime mechanism.
 *
 * Here is an example of how it's used:
 * If the current line being interpreted contains "if 10 = else", the execute() method would process everything after "else" until a 'THEN' is found.
 *
 * A Note to developers: It helps to set a line breakpoint at the return statement and inspecting
 * the compiler.forthWordsBuffer for correctness. This is what the 'newWord' will `look like` once saved
 * to the dictionary.
 *
 */
package primitives_classes.compiler

import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.CompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * The 'ElseC' class extends the 'AbstractCompilerDirective' super class.
 * This compiler directive class handles the logic of the 'ELSE' keyword at compile time in the interpreter.
 */
class ElseC extends AbstractCompilerDirective {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    ForthCompiler compiler
    ForthInterpreter interpreter

    /**
     * The `execute` method is responsible for performing the compile-time operations for 'ELSE' in the
     * Forth compiler.
     *
     * @param newWord The word that is being compiled.
     * @param compiler The ForthCompiler instance.
     * @param interpreter The ForthInterpreter instance.
     * @return Boolean indicating if a new line needs to be printed or not in Forth REPL.
     * @exception ForthCompilerException If there's no matching 'IF' or 'THEN' for 'ELSE'.
     */
    @Override
    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {
        this.compiler = compiler
        this.interpreter = interpreter

        if (!interpreter.line.contains("if")) {
            interpreter.words.clear()
            interpreter.nonWords.clear()
            throw new ForthCompilerException("No matching 'IF for 'ELSE")
        }

        if (!interpreter.line.contains("then")) {
            interpreter.words.clear()
            interpreter.nonWords.clear()
            throw new ForthCompilerException("No matching 'THEN for 'ELSE")
        }

        while (!this.interpreter.tokensCopy.isEmpty()) {
            String token = this.interpreter.tokensCopy.poll()
            Word thenWord = this.compiler.wordService.findByName("then")

            Word word = this.compiler.wordService.findByName(token)

            if (word) {
                this.compiler.forthWordsBuffer.add(word.name)
                if (word.compileClass) {
                    def classLoader = new GroovyClassLoader()
                    Class groovyClass = classLoader.parseClass(word.compileClass)
                    CompilerDirective compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                    def output = compileTime.execute(this.compiler.newWord, this.compiler, this.interpreter)
                }
            }

        }

        // Fix things up a bit.
        while (compiler.forthWordsBuffer.contains(";")) {
            compiler.forthWordsBuffer.remove(";")
        }
        return false
    }
}