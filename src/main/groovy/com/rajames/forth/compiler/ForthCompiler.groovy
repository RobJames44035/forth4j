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
    Word nextWordToCompile
    Dictionary dictionary
    List<String> forthWordsBuffer = new ArrayList<>()
    ConcurrentLinkedQueue<Word> words
    ConcurrentLinkedQueue<Integer> arguments
    ConcurrentLinkedQueue<String> nonWords

    @Transactional
    Word compileWord(
            ConcurrentLinkedQueue<Word> words,
            ConcurrentLinkedQueue<Integer> arguments,
            ConcurrentLinkedQueue<String> nonWords
    ) {

        this.words = words
        this.arguments = arguments
        this.nonWords = nonWords

        Word newForthWord = null

        try {
            setupForCompilerRun()
            compileArgumentLiterals()
            doCompile()

            this.newWord.forthWords = forthWordsBuffer
            newForthWord = wordService.save(this.newWord)

        } catch (Exception e) {
            log.error(e.message)
        }
        return newForthWord
    }

    private Boolean doCompile() {
        Boolean output = false
        try {
            while (!words.isEmpty()) {
                nextWordToCompile = words.remove()
                if (nextWordToCompile) {
                    output = true
                    String compileClass = nextWordToCompile?.compileClass?.trim()
                    if (compileClass != null && !compileClass.isEmpty()) {
                        def classLoader = new GroovyClassLoader()
                        Class groovyClass = classLoader.parseClass(compileClass)
                        CompilerDirective compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                        output = compileTime.execute(this.newWord, this, this.interpreter)
                        if (output) {
                            forthWordsBuffer.add(nextWordToCompile.name)
                        } // Edge cases can fo here. To be avoided though.
                    } else {
                        forthWordsBuffer.add(nextWordToCompile.name)
                    }
                }
            }
        } catch (Exception e) {
            log.error("foo", e)
        }
        return output
    }

    private void setupForCompilerRun() {
        this.dictionary = dictionaryService.findByName(bootstrap.coreName)
        this.literal = wordService.findByName("literal")
        this.newWord = new Word()
        this.newWord.name = nonWords.remove()
        this.newWord.dictionary = dictionary
        this.wordService.save(this.newWord)
    }

    private void compileArgumentLiterals() {
        while (!arguments.isEmpty()) {
            Integer argument = arguments.remove()
            String uniqueId = UUID.randomUUID().toString().replaceAll("-", "")

            Word wordLiteral = new Word()
            wordLiteral.name = "int_${literal.name}_${uniqueId}"
            wordLiteral.runtimeClass = literal.runtimeClass
            wordLiteral.stackValue = argument
            wordLiteral.compileOnly = true
            wordLiteral.dictionary = this.dictionary
            wordLiteral.parentWord = newWord
            wordService.save(wordLiteral)
            forthWordsBuffer.add(wordLiteral.name)
        }
    }
}
