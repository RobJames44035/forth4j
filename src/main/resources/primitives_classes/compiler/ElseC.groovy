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


import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.CompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ElseC extends AbstractCompilerDirective {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    ForthCompiler compiler
    ForthInterpreter interpreter

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
            String token = this.interpreter.tokensCopy.remove()
            Word thenWord = this.compiler.wordService.findByName("then")
            if (token == thenWord.name) {
                def classLoader = new GroovyClassLoader()
                Class groovyClass = classLoader.parseClass(thenWord.compileClass)
                CompilerDirective compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                def output = compileTime.execute(this.compiler.newWord, this.compiler, this.interpreter)
                break
            } else {
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
        }
        return false
    }

    // ... rest of your methods ...

}