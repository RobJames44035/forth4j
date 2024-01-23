package com.rajames.forth.init

import com.rajames.forth.dictionary.DictionaryService
import com.rajames.forth.dictionary.WordService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class Bootstrap implements InitializingBean {

    private static final Logger log = LogManager.getLogger(this.class.getName())


    private final DictionaryService dictionaryService
    private final WordService wordService
    private String coreName

    Bootstrap(DictionaryService dictionaryService, WordService wordService) {
        this.dictionaryService = dictionaryService
        this.wordService = wordService
    }

    @Override
    void afterPropertiesSet() {
        log.info("Bootstrap started...")
        coreName = dictionaryService.createDictionary("Core")
        wordService.addWordToDictionary("testWord", "println 'This is a test word.'", this.coreName)
        // more initialization code here...
        log.info("\t...Bootstrap finishes")
    }
}
