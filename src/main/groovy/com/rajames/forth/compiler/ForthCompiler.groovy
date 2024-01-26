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


import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService
import com.rajames.forth.init.Bootstrap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ForthCompiler {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    @Autowired
    WordService wordService

    @Autowired
    Bootstrap bootstrap


    Word compileWord(LinkedList<String> tokens) {
        log.debug("Entering compiler")
        String dictionaryName = bootstrap.coreName
        Integer argumentCount = 0
        Word newWord = null
        String wordName = tokens.remove()
        ArrayList<Word> words = []
        if (!tokens.contains(";")) {
            tokens.clear()
            throw new ForthCompilerException("No ending semicolon")
        }
        while (!tokens.isEmpty()) {
            String token = tokens.remove()
            if (token != ";") {
                Optional<Word> optionalWord = wordService.findByName(token)
                Word word
                if (optionalWord.isPresent()) {
                    word = optionalWord.get()
                }
                if (word != null) {
                    words.add(word)
                } else {
                    try {
                        Integer num = Integer.parseInt(token)
                        // handle the fact this is a number, not a word
                        // this could involve creating a new Word that represents
                        // pushing this number onto the stack and adding it to the words list
                    } catch (NumberFormatException e) {
                        throw new ForthCompilerException("", e)
                    }
                }
            } else {
                // we have reached the end.
                break
            }
            // now we have all the information we need to build the word
        }
        return newWord
    }
}