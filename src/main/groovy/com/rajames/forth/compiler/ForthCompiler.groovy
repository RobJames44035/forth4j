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
import com.rajames.forth.init.DatabaseBackupService
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ConcurrentLinkedQueue

@Component
@Transactional
class ForthCompiler {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    List<Word> forthWordsBuffer = new ArrayList<>()

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
    Session session

    @Autowired
    DatabaseBackupService databaseBackupService

    Word newWord
    Word literal
    Dictionary dictionary

    Stack<String> ctrlFlowStack = new Stack<>()

    Integer loopCounter = 0

    @Transactional
    void compileWord(
            ConcurrentLinkedQueue<Word> words,
            ConcurrentLinkedQueue<Integer> arguments,
            ConcurrentLinkedQueue<String> nonWords
    ) {
        log.trace("Entering compiler")

        try {
            this.dictionary = dictionaryService.findByName(bootstrap.coreName)
            this.literal = wordService.findByName("literal")
            this.newWord = new Word()
            this.newWord.name = nonWords.remove()
            this.newWord.dictionary = dictionary
            this.wordService.save(this.newWord)

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

                forthWordsBuffer.add(wordLiteral)
            }

            Boolean output
            while (!words.isEmpty()) {
                Word nextWordToCompile = words.remove()
                if (nextWordToCompile) {
                    output = true
                    String compileClass = nextWordToCompile?.compileClass?.trim()
                    if (compileClass != null && !compileClass.isEmpty()) {
                        def classLoader = new GroovyClassLoader()
                        Class groovyClass = classLoader.parseClass(compileClass)
                        CompileTime compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompileTime
                        output = compileTime.execute(this.newWord, this, this.interpreter)
                        if (output) {
                            if (!wordService.isSaved(nextWordToCompile)) {
                                wordService.save(nextWordToCompile)
                            }
                            forthWordsBuffer.add(nextWordToCompile)
//                            this.newWord.forthWords.add(nextWordToCompile)
//                            wordRepository.save(this.newWord)
                        }
                    } else {
                        forthWordsBuffer.add(nextWordToCompile)
                    }
                }
            }
            this.newWord.forthWords = forthWordsBuffer

            log.debug("Compiler: Word before 'wordService.save(this.newWord)' this?.newWord?.name = ${this?.newWord?.name} this?.newWord?.forthWords = ${this?.newWord?.forthWords}")
            Word afterSave = wordService.save(this.newWord)
            log.debug("Compiler: Word after 'wordService.save(this.newWord)' afterSave?.name = ${afterSave?.name} afterSave?.forthWords = ${afterSave?.forthWords}")

        } catch (Exception e) {
            log.error(e.message, e)
        }
    }
}
