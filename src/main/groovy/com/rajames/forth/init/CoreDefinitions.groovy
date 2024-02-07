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
//file:noinspection GroovyUnusedAssignment

package com.rajames.forth.init

import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService
import com.rajames.forth.util.DatabaseBackupService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CoreDefinitions {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    public static final String RUNTIME = "primitives_classes/runtime"
    public static final String COMPILE = "primitives_classes/compiler"

    private final WordService wordService
    private String coreName

    private DatabaseBackupService databaseBackupService

    CoreDefinitions(WordService wordService, String dictionaryName, DatabaseBackupService databaseBackupService) {
        this.wordService = wordService
        this.coreName = dictionaryName
        this.databaseBackupService = databaseBackupService
    }

    static String getRuntimeContent(String runtimeClass) {
        String runtimeBasePath = "primitives_classes/runtime"
        getGroovyFileContent(runtimeBasePath, runtimeClass)
    }

    static String getCompileContent(String compileClass) {
        String compileBasePath = "primitives_classes/compiler"
        getGroovyFileContent(compileBasePath, compileClass)
    }

    // helper method to retrieve content of Groovy files
    private static String getGroovyFileContent(String basePath, String className) {
        String retVal = null
        if (className != null) {
            String myResourceName = basePath + "/" + className + ".groovy"
            retVal = CoreDefinitions.class.getClassLoader().getResource(myResourceName).text
        }
        return retVal
    }

    Word createPrimitiveWord(String wordName, String runtimeClass = null, String compileClass = null, Integer argumentCount = 0, Boolean compileOnly = false, Boolean controlWord = false) {
        try {
            String runtimeContent = getRuntimeContent(runtimeClass)
            String compileContent = getCompileContent(compileClass)
            return wordService.addWordToDictionary(wordName, coreName, null, runtimeContent, compileContent, argumentCount, compileOnly, controlWord)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create primitive word ${wordName}.", e)
        }
    }

    Word createComplexWord(String wordName, List<String> words, Integer argumentCount = 0, Boolean compileOnly = false) {
        try {
            return wordService.addWordToDictionary(wordName, coreName, words as List<String>, null, null, argumentCount, compileOnly)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create complex word ${wordName}.", e)
        }
    }


    void createCoreDictionary() {

        // Non-Standard words
        Word noop = createPrimitiveWord("noop")
        Word nop = createPrimitiveWord("nop")
        Word dump = createPrimitiveWord("defdump", "Dump")

        // Primitive words with their behavior described in a Groovy Script
        Word plus = createPrimitiveWord("+", "Plus", null, 2)
        Word minus = createPrimitiveWord("-", "Minus", null, 2)
        Word dot = createPrimitiveWord(".", "Dot", null, 2)
        Word cr = createPrimitiveWord("cr", "Cr")
        Word emit = createPrimitiveWord("emit", "Emit", null, 1)
        Word dotQuote = createPrimitiveWord(".\"", null, "DotQuoteC")
        Word lessThanZero = createPrimitiveWord("0<", "LessThanZero", null, 1)
        Word equalZero = createPrimitiveWord("0=", "EqualZero", null, 1)
        Word greaterThanZero = createPrimitiveWord("0=", "GreaterThanZero", null, 1)
        Word onePlus = createPrimitiveWord("1+", "OnePlus", null, 1)
        Word oneMinus = createPrimitiveWord("1-", "OneMinus", null, 1)
        Word twoPlus = createPrimitiveWord("2+", "TwoPlus", null, 1)
        Word twoMinus = createPrimitiveWord("2-", "TwoMinus", null, 1)
        Word twoTimes = createPrimitiveWord("2*", "TwoTimes", null, 1)
        Word twoDivide = createPrimitiveWord("2/", "TwoDivide", null, 1)
        Word lessThan = createPrimitiveWord("<", "LessThan", null, 2)
        Word greaterThan = createPrimitiveWord(">", "GreaterThan", null, 2)
        Word equal = createPrimitiveWord("=", "Equal", null, 2)
        Word spaces = createPrimitiveWord("spaces", "Spaces", null, 1)

        Word colon = createPrimitiveWord(":", "Colon")
        Word semicolon = createPrimitiveWord(";")
        Word literal = createPrimitiveWord("literal", "Literal", null, 0, true)
//        Word ifWord = createPrimitiveWord("if", "If", "IfC", 1, true, true)
//        Word elseWord = createPrimitiveWord("else", "Else", "ElseC", 0, true, true)
//        Word thenWord = createPrimitiveWord("then", null, null, 0, true, true)

//        Word doWord = createPrimitiveWord("do")
//        Word loopWord = createPrimitiveWord("loop")
//        Word plusLoopWord = createPrimitiveWord("+loop)

//        Word beginWord = createPrimitiveWord("begin")
//        Word againWord = createPrimitiveWord("again")
//        Word whileWord = createPrimitiveWord("while")
//        Word repeatWord = createPrimitiveWord("repeat")

        // Complex words that are made up of a List<Word> that describes their behavior go here.

        databaseBackupService.backupDatabase("/home/rajames/PROJECTS/forth4j/src/main/resources", "CoreForth4j.sql")
    }
}
