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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
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
                Word word = wordService.findByName(token)

                if (word != null) {
                    newWord.childWords.add(word)
                    word.parentWord = newWord
                    newWord.complexWordOrder = ct
                } else {
                    try {
                        // handle the fact this is a number, not a word
                        Integer num = Integer.parseInt(token)
                        Word literal = wordService.findByName("literal")

                        Word wordLiteral = new Word()
                        wordLiteral.complexWordOrder = ct
                        wordLiteral.name = literal.name + "_" + newWord.name + "_" + num + "_" + "_" + ct
                        wordLiteral.behaviorScript = literal.behaviorScript
                        wordLiteral.stackValue = num
                        wordLiteral.compileOnly = true
                        wordLiteral.dictionary = dictionaryService.findByName(bootstrap.coreName)
                        wordLiteral.parentWord = newWord
                        newWord.childWords.add(wordLiteral)
                        wordRepository.save(wordLiteral)

                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                break
            }
            ct++
        }
        return wordRepository.save(newWord)
    }
}
