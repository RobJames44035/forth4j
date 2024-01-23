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

    @Transactional
    void addWordToDictionary(String wordName, String behaviorScript, String dictionaryName) {
        log.info("Adding ${wordName} to ${dictionaryName} dictionary.")
        try {
            Optional<Dictionary> dictionaryOptional = dictionaryRepository.findByName(dictionaryName) as Optional<Dictionary>;

            if (dictionaryOptional.isPresent()) {
                Dictionary dictionary = dictionaryOptional.get();

                // Create a new Word object
                Word word = new Word()
                word.name = wordName
                word.dictionary = dictionary
                word.behaviorScript = behaviorScript

                // Save the new Word to the database
                wordRepository.save(word)
            } else {
                log.error("Dictionary with name ${dictionaryName} does not exist.")
            }
        } catch(Exception e) {
            log.error("${wordName} was NOT added to ${dictionaryName} dictionary.", e)
        }
        log.info("${wordName} added to ${dictionaryName} dictionary.")
    }
}