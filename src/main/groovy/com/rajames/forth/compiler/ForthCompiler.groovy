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
import com.rajames.forth.util.DatabaseBackupService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ConcurrentLinkedQueue

//import org.hibernate.Session

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

    @Autowired
    DatabaseBackupService databaseBackupService

    Word newWord
    Word literal
    String nextTokenToCompile
    Dictionary dictionary
    List<String> forthWordsBuffer = new ArrayList<>()

    Queue<String> tokens = new ConcurrentLinkedQueue<>()

    private boolean isStringContext = false

    @Transactional
    Word compile(String line) {
        // Setup
        Word newForthWord = null
        line = line.toLowerCase().trim() - ":" - ";" as String
        this.tokens = new LinkedList<>(line.tokenize())
        defineNewWord()

        while (!tokens.isEmpty()) {
            Object compilerOutput = null
            List<String> list = new ArrayList<>(tokens)
            String token = tokens.poll()
            Word word = wordService.findByName(token)

            if (word != null) {
                String compilerDirectiveClass = word?.compileClass?.trim()
                if (compilerDirectiveClass != null && !compilerDirectiveClass.isEmpty() && !compilerDirectiveClass.isBlank()) {
                    GroovyClassLoader classLoader = new GroovyClassLoader()
                    Class groovyClass = classLoader.parseClass(compilerDirectiveClass)
                    CompilerDirective compilerDirective = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                    compilerOutput = compilerDirective.execute(word, this, this.interpreter)
                    if (compilerOutput == null) {
                        compilerOutput = false
                    } else {
                        // We CAN return anything we want in reality if there is anything AFTER
                        // the compiler directive is finished and if we return something.
                        // This was done to handle edge cases should they arise.
                        continue
                    }
                }
                this.forthWordsBuffer.add(word.name)
            } else if (canParseToInt(token)) {
                compileIntegerLiteral(token)
            }
        }

        try {
            this.newWord.forthWords = forthWordsBuffer
            newForthWord = wordService.save(this.newWord)

        } catch (Exception e) {
            log.error(e.message)
        }
        return newForthWord
    }

    static boolean canParseToInt(String str) {
        try {
            Integer.parseInt(str)
            return true
        } catch (NumberFormatException ignored) {
            return false
        }
    }

    void defineNewWord() {
        this.dictionary = dictionaryService.findByName(bootstrap.coreName)
        this.newWord = new Word()
        this.newWord.name = this.tokens.poll()
        this.newWord.dictionary = dictionary
        this.wordService.save(this.newWord)
    }

    void compileIntegerLiteral(String token) {
        try {
            Word literal = wordService.findByName("literal")
            Word integerLiteral = new Word()
            integerLiteral.name = "int_${literal.name}_${UUID.randomUUID().toString() - "-"}"
            integerLiteral.runtimeClass = literal.runtimeClass
            integerLiteral.stackValue = Integer.parseInt(token)
            integerLiteral.dictionary = this.dictionary
            integerLiteral.parentWord = this.newWord
            wordService.save(integerLiteral)
            this.forthWordsBuffer.add(integerLiteral.name)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not compile ${token} into the dictionary.", e)
        }
    }

    void compileLiteral(String compileLiteral) {
        try {
            Word literal = wordService.findByName("literal")
            Word stringLiteral = new Word()
            stringLiteral.name = "str_${literal.name}_${UUID.randomUUID().toString() - "-"}"
            stringLiteral.runtimeClass = literal.runtimeClass
            stringLiteral.stringLiteral = compileLiteral
            stringLiteral.compileOnly = true
            stringLiteral.dictionary = this.dictionary
            stringLiteral.parentWord = this.newWord
            wordService.save(stringLiteral)
            forthWordsBuffer.add(stringLiteral.name)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not compile ${compileLiteral} into the dictionary.", e)
        }
    }


}
