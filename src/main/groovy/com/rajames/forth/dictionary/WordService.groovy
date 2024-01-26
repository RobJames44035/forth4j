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

package com.rajames.forth.dictionary

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordService {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private final WordRepository wordRepository
    private final DictionaryRepository dictionaryRepository

    WordService(WordRepository wordRepository, DictionaryRepository dictionaryRepository) {
        this.wordRepository = wordRepository
        this.dictionaryRepository = dictionaryRepository
    }

    Optional<Word> findByName(String name) {
        return wordRepository.findByName(name)
    }

    @Transactional
    Word addWordToDictionary(String wordName, List<Word> words = null, String behaviorScript, String dictionaryName, Integer argumentCount = 0, Boolean compileOnly = false) {
        Word word = null
        try {
            Optional<Dictionary> dictionaryOptional = dictionaryRepository.findByName(dictionaryName) as Optional<Dictionary>

            if (dictionaryOptional.isPresent()) {
                Dictionary dictionary = dictionaryOptional.get()

                word = new Word()
                word.name = wordName
                word.dictionary = dictionary
                word.behaviorScript = behaviorScript
                word.argumentCount = argumentCount
                word.compileOnly = compileOnly
                wordRepository.save(word)

                if (words) {
                    words.each { Word childWord ->
                        Optional<Word> childWordOptional = wordRepository.findByName(childWord.name)

                        if (childWordOptional.isPresent()) {
                            Word managedChildWord = childWordOptional.get()
                            managedChildWord.parentWord = word
                            word.childWords.add(managedChildWord)
                            wordRepository.save(managedChildWord)
                        } else {
                            log.error("Child word with name ${childWord} not found.")
                        }
                    }
                }

                wordRepository.save(word)
            } else {
                log.error("Dictionary with name ${dictionaryName} does not exist.")
            }
        } catch (Exception e) {
            log.error("${wordName} was NOT added to ${dictionaryName} dictionary.", e)
        }
        log.info("${wordName} added to ${dictionaryName} dictionary.")
        return word
    }
}