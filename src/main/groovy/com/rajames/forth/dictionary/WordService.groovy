package com.rajames.forth.dictionary

import org.springframework.stereotype.Service

@Service
class WordService {

    private final WordRepository wordRepository
    private final DictionaryRepository dictionaryRepository

    WordService(WordRepository wordRepository, DictionaryRepository dictionaryRepository) {
        this.wordRepository = wordRepository
        this.dictionaryRepository = dictionaryRepository
    }

    void addWordToDictionary(String wordName, Integer dictionaryId) {
        Dictionary dictionary = dictionaryRepository.findById(dictionaryId).orElseThrow() // insert your exception here

        Word word = new Word()
        word.name = wordName
        word.dictionary = dictionary

        wordRepository.save(word)
    }
}
