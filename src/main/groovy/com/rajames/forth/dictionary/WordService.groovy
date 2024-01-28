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

import com.rajames.forth.compiler.ForthCompilerException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WordService {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private final WordRepository wordRepository
    private final DictionaryRepository dictionaryRepository

    WordService(WordRepository wordRepository, DictionaryRepository dictionaryRepository) {
        this.wordRepository = wordRepository
        this.dictionaryRepository = dictionaryRepository
    }

    Word findByName(String name) {
        Optional<Word> optional = wordRepository.findFirstByNameOrderByCreateDateTimeDesc(name)
        if (optional.isPresent()) {
            return optional.get()
        } else {
            return null
        }
    }

    Word save(Word word) {
        return wordRepository.save(word)
    }

    void deleteWordFromDictionary(Word deleteMe) {
        Word word = findByName(deleteMe.name)
        if (word != null) {
            wordRepository.delete(word)
        }
    }

    Word addWordToDictionary(String wordName, String dictionaryName,
                             List<Word> complexWords = null,
                             String runtimeClass = null, String compileClass = null,
                             Integer argumentCount = 0, Boolean compileOnly = false) {
        Word word = null
        try {
            Optional<Dictionary> dictionaryOptional = dictionaryRepository.findByName(dictionaryName) as Optional<Dictionary>

            if (dictionaryOptional.isPresent()) {
                Dictionary dictionary = dictionaryOptional.get()

                word = new Word()
                word.name = wordName
                word.dictionary = dictionary
                word.runtimeClass = runtimeClass
                word.compileClass = compileClass
                word.argumentCount = argumentCount
                word.compileOnly = compileOnly
                save(word)

                if (complexWords) {
                    complexWords.each { Word childWord ->
                        Word forthWord = findByName(childWord.name)
                        if (word != null) {
                            forthWord.parentWord = word
                            word.forthWords.add(forthWord)
                            save(forthWord)
                        } else {
                            throw new ForthCompilerException("Word ${childWord} not found.")
                        }
                    }
                }

                save(word)
            } else {
                throw new ForthCompilerException("Dictionary with name ${dictionaryName} does not exist.")
            }
        } catch (Exception e) {
            throw new ForthCompilerException("${wordName} was NOT added to ${dictionaryName} dictionary.", e)
        }
        log.debug("${wordName} added to ${dictionaryName} dictionary.")
        return word
    }
}
