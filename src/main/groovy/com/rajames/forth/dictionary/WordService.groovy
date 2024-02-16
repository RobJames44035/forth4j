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
import com.rajames.forth.runtime.ForthInterpreterException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
@Transactional
class WordService {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private final WordRepository wordRepository
    private final DictionaryRepository dictionaryRepository

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    WordService(WordRepository wordRepository, DictionaryRepository dictionaryRepository) {
        this.wordRepository = wordRepository
        this.dictionaryRepository = dictionaryRepository
    }

    Word getById(Long id) {
        return wordRepository.findById(id).orElseThrow(() -> new ForthInterpreterException("Word not found for id: " + id))
    }

    List<Word> list() {
        return wordRepository.findAll()
    }

    @Transactional
    void deleteAllWithCreationDateOnOrAfter(Date date) {
        wordRepository.deleteAllWithCreationDateOnOrAfter(date)
    }

    @Transactional
    Word findByName(String name) {
        log.trace("WordService.findByName(String '${name}')")
        Optional<Word> optional = wordRepository.findFirstByNameOrderByCreateDateTimeDesc(name)
        log.trace("WordService: Optional<Word> optional = ${optional}")
        if (optional.isPresent()) {
            Word word = optional.get()
            log.trace("WordService: Word word = optional.get() name = '${word.name}' forthWords = ${word.forthWords}")
            return word
        } else {
            log.trace("NOT FOUND!")
            return null
        }
    }

    @Transactional
    Word save(Word word) {
        Word retrievedWord = wordRepository.save(word)
        entityManager.flush()
        return retrievedWord
    }

    @Transactional
    Boolean isSaved(Word word) {
        return entityManager.contains(word)
    }

    @Transactional
    void deleteWordFromDictionary(Word deleteMe) {
        Word word = findByName(deleteMe.name)
        if (word != null) {
            wordRepository.delete(word)
        }
    }

    @Transactional
    Word addWordToDictionary(String wordName, String dictionaryName,
                             List<String> complexWords = null,
                             String runtimeClass = null, String compileClass = null,
                             Integer argumentCount = 0, Boolean compileOnly = false, Boolean controlWord = false) {
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
                word.controlWord = controlWord
                save(word)

                if (complexWords) {
                    complexWords.each { String childWord ->
                        Word forthWord = findByName(childWord)
                        if (word != null && forthWord != null) {
                            forthWord.parentWord = word
                            word.forthWords.add(forthWord.name)
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
        log.trace("${wordName} added to ${dictionaryName} dictionary.")
        return word
    }
}
