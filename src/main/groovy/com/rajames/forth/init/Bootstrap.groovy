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

package com.rajames.forth.init

import com.rajames.forth.dictionary.DictionaryService
import com.rajames.forth.dictionary.WordService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Bootstrap implements InitializingBean {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private DictionaryService dictionaryService
    private WordService wordService

    private String coreName

    private DatabaseBackupService databaseBackupService

    @Autowired
    Bootstrap(DictionaryService dictionaryService, WordService wordService, DatabaseBackupService databaseBackupService) {
        this.dictionaryService = dictionaryService
        this.wordService = wordService
        this.databaseBackupService = databaseBackupService
    }

    @Override
    void afterPropertiesSet() {
        log.info("Bootstrap started...")
        println("Bootstrap started...")

        coreName = dictionaryService.createDictionary("Core")

        CoreDefinitions coreDefinitions = new CoreDefinitions(wordService, coreName, databaseBackupService)
        coreDefinitions.createCoreDictionary()

        // more initialization code here...
        log.info("\t...Bootstrap finished.")
        println("\t...Bootstrap finished.")
    }

    String getCoreName() {
        return coreName
    }
}
