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
        wordService.addWordToDictionary("license", "println('See: http://www.apache.org/licenses/LICENSE-2.0)", this.coreName)

        wordService.addWordToDictionary("+", "arg1 + arg2", this.coreName, 2)
        wordService.addWordToDictionary(".", "print arg1", this.coreName, 1)
        wordService.addWordToDictionary("cr", "println()", this.coreName)

        // more initialization code here...
        log.info("\t...Bootstrap finishes")
    }
}
