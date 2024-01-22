package com.rajames.forth.dictionary

import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class DictionaryService {

    private final DictionaryRepository dictionaryRepository

    DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository
    }

    @EventListener(ContextRefreshedEvent)
    void createCoreDictionary() {
        Dictionary dictionary = new Dictionary()
        dictionary.setName("Core")

        Set<Word> words = new HashSet<>()
        // Add words to the set

        dictionary.setWords(words)
        dictionaryRepository.save(dictionary)
    }
}
