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
class DictionaryService {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private final DictionaryRepository dictionaryRepository

    DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository
    }

    @Transactional
    String createDictionary(String name) {
        log.info("Creating ${name} dictionary.")
        Dictionary dictionary = null
        try {
            dictionary = new Dictionary()
            dictionary.name = name
            dictionaryRepository.save(dictionary)

        } catch(Exception e) {
            log.error("Failed to save ${} dictionary", e)
        }
        log.info("Saved ${name} dictionary.")
        return dictionary?.name
    }
}
