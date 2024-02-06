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
 * The "IfC" class is an integral part of the compilation process of the Forth-like language interpreter.
 * This class extends from "AbstractCompilerDirective" which forms the foundation for all the compiler directive classes.
 *
 * The compile-time mechanism analyzes and translates the defined Forth terms during the parsing process.
 * For the 'IF', 'THEN', 'ELSE' words, they are registered into the dictionary associated with their respective compiler directive
 * classes (in this case, the 'IfC' class for 'IF').
 *
 * The 'IfC' class, through its 'execute' method, modifies the source code under interpretation to a more executable form.
 * The 'execute' method extracts all the tokens corresponding to a conditional control structure starting from 'IF' and ending
 * with 'THEN', while properly handling the optional 'ELSE' case as well.
 * Thus the method assembles these tokens into a manageable structure, ready for the runtime execution.
 *
 * This facilitates the use and implementation of 'IF', 'ELSE' and 'THEN' control structures in the language, enhancing
 * the versatility of the interpreted language.
 *
 * Our test cases included:
 * : test1 5 = if ." Five " then ;
 * : test2 5 = if ." Five " else ." Not Five " then ;
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
 * The 'IfC' class extends the 'AbstractCompilerDirective' super class.
 * This compiler directive class handles the logic of the 'IF' keyword at compile time in the interpreter.
 */
class IfC extends AbstractCompilerDirective {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    ForthCompiler compiler
    ForthInterpreter interpreter

/**
 * The `execute` method is responsible for performing the compile-time operations for 'IF' in the Forth compiler.
 *
 * @param newWord The word that is being compiled.
 * @param compiler The ForthCompiler instance.
 * @param interpreter The ForthInterpreter instance.
 * @return Boolean indicating if a new line needs to be printed or not in the Forth REPL.
 * @exception ForthCompilerException if there's no matching 'IF' or 'THEN' for 'ELSE'.
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

        runup()

        while (!this.interpreter.tokensCopy.isEmpty()) {
            String token = this.interpreter.tokensCopy.remove()
            Word thenWord = this.compiler.wordService.findByName("then")
            Word elseWord = this.compiler.wordService.findByName("else")
            if (token == thenWord.name || token == elseWord.name) {
                if (token == thenWord.name) {
                    this.compiler.forthWordsBuffer.add(thenWord.name)
                    this.interpreter.words.remove()
                    break
                }
                if (token == elseWord.name) {
                    def classLoader = new GroovyClassLoader()
                    Class groovyClass = classLoader.parseClass(elseWord.compileClass)
                    CompilerDirective compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                    def output = compileTime.execute(this.compiler.newWord, this.compiler, this.interpreter)
                    break
                }
            } else {
                Word word = this.compiler.wordService.findByName(token)
                try {
                    this.interpreter.words.remove()
                } catch (Exception ignored) {
                    break
                }

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
        }
        return false
    }

    /**
     * The 'runup' method removes all tokens from the tokensCopy up to "if".
     * It adds "if" to the list of forthWordsBuffer.
     */
    private void runup() {
        // remove all tokens from the tokensCopy up to "if"
        while (!this.interpreter.tokensCopy.isEmpty()) {
            String token = this.interpreter.tokensCopy.remove()
            Word ifWord = this.compiler.wordService.findByName("if")
            if (ifWord.name == token) {
                // add "if" to the list of forthWordsBuffer
                this.compiler.forthWordsBuffer.add(ifWord.name)
                break
            }
        }
    }

    /**
     * The 'compileLiteral' method takes in a token (either integer or string literal) and
     * generates unique identifiers for each.
     * It then saves the literals as Words into the dictionary.
     *
     * @param token An Integer or String literal token
     * @return A Word instance representing the literal token.
     */
    private Word compileLiteral(String token) {
        Word wordLiteral = new Word()
        String uniqueId = UUID.randomUUID().toString().replaceAll("-", "")
        try {
            // Integer literals
            Integer i = Integer.parseInt(token)
            wordLiteral.name = "int_${this.compiler.literal.name}_${uniqueId}"
            wordLiteral.stackValue = i
        } catch (NumberFormatException ignored) {
            // string literal
            wordLiteral.name = "str_${this.compiler.literal.name}_${uniqueId}"
            wordLiteral.stringLiteral = token
        }
        wordLiteral.runtimeClass = this.compiler.literal.runtimeClass
        wordLiteral.compileOnly = true
        wordLiteral.dictionary = this.compiler.dictionary
        wordLiteral.parentWord = this.compiler.newWord
        this.compiler.wordService.save(wordLiteral)
        return wordLiteral
    }
}
