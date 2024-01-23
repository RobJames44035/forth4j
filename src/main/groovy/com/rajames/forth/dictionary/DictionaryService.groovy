package com.rajames.forth.dictionary

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
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
