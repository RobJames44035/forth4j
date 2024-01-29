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
//file:noinspection GroovyUnusedAssignment

package com.rajames.forth.compiler

import com.rajames.forth.dictionary.*
import com.rajames.forth.init.Bootstrap
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ForthCompiler {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    @Autowired
    DictionaryService dictionaryService

    @Autowired
    DictionaryRepository dictionaryRepository

    @Autowired
    WordRepository wordRepository

    @Autowired
    WordService wordService

    @Autowired
    Bootstrap bootstrap

    @Autowired
    ForthInterpreter interpreter


    Word word

    @Transactional
    Word compileWord(LinkedList<String> tokens) {
        log.trace("Entering compiler")
        // Fail Fast
        if (!tokens.contains(";")) {
            tokens.clear()
            throw new ForthCompilerException("No ending semicolon")
        }

        Integer argumentCount = 0
        Word newWord = new Word()
        newWord.name = tokens.remove()
        newWord.dictionary = dictionaryService.findByName(bootstrap.coreName)
        wordRepository.save(newWord)

        int ct = 0
        while (!tokens.isEmpty()) {
            String token = tokens.remove()
            if (token != ";") {
                word = wordService.findByName(token)
                String runtimeClass = word?.runtimeClass?.trim()
                String compileClass = word?.compileClass?.trim()
                if (word != null) {
                    if (word.compileClass != null && !word.compileClass.isEmpty()) {
                        def classLoader = new GroovyClassLoader()
                        Class groovyClass = classLoader.parseClass(compileClass)
                        CompileTime compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompileTime
                        def x = compileTime.execute(this, interpreter)
                    }
                    newWord.forthWords.add(word)
                    word.parentWord = newWord
                    newWord.complexWordOrder = ct
                    wordRepository.save(newWord)
                } else {
                    try {
                        // handle the fact this is a number, not a dictionary word.
                        Integer num = Integer.parseInt(token)
                        Word literal = wordService.findByName("literal")
                        Word wordLiteral = new Word()
                        wordLiteral.complexWordOrder = ct
                        wordLiteral.name = "int_${literal.name}_${ct}"
                        wordLiteral.runtimeClass = literal.runtimeClass
                        wordLiteral.stackValue = num
                        wordLiteral.compileOnly = true
                        wordLiteral.dictionary = dictionaryService.findByName(bootstrap.coreName)
                        wordLiteral.parentWord = newWord
                        newWord.forthWords.add(wordLiteral)
                        wordRepository.save(wordLiteral)
                    } catch (NumberFormatException ignored) {
                        // String literal
                        Word literal = wordService.findByName("literal")
                        Word wordLiteral = new Word()
                        wordLiteral.complexWordOrder = ct
                        wordLiteral.name = "str_${literal.name}_${ct}"
                        wordLiteral.runtimeClass = literal.runtimeClass
                        wordLiteral.stringLiteral = token
                        wordLiteral.compileOnly = true
                        wordLiteral.dictionary = dictionaryService.findByName(bootstrap.coreName)
                        wordLiteral.parentWord = newWord
                        newWord.forthWords.add(wordLiteral)
                        wordRepository.save(wordLiteral)
                    }
                }
            } else {
                break
            }
            ct++
        }
        return newWord
    }
}
