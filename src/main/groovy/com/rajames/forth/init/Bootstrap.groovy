package com.rajames.forth.init

import com.rajames.forth.dictionary.DictionaryService
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class Bootstrap implements InitializingBean {

    private final DictionaryService dictionaryService

    Bootstrap(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService
    }

    @Override
    void afterPropertiesSet() {
        dictionaryService.createCoreDictionary()
        // more initialization code here...
    }
}
