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

package primitives_classes.compiler
//    : ?full 12 = if ." It's full " else ." NOT full " then ." Finished! " ;
//    : ?full 12 = if ." It's full " then ;

import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.CompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class IfC extends AbstractCompilerDirective {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    ForthCompiler compiler
    ForthInterpreter interpreter
    @Override
    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {
        this.compiler = compiler
        this.interpreter = interpreter

        // Fast Fail
        if (interpreter.words.stream().noneMatch(w -> w.getName().equals("then"))) {
            interpreter.words.clear()
            interpreter.nonWords.clear()
            interpreter
            throw new ForthCompilerException("No matching 'THEN for 'IF")
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
