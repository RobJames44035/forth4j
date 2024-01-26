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

import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService

class CoreDefinitions {
    public static final String SCRIPTS = "primitives_groovy"
    private final WordService wordService
    private String coreName

    CoreDefinitions(WordService wordService, String dictionaryName) {
        this.wordService = wordService
        this.coreName = dictionaryName
    }

    Word createPrimitiveWord(String wordName, String behaviorScript, Integer argumentCount = 0, Boolean compileOnly = false) {
        return wordService.addWordToDictionary(wordName, null, behaviorScript, this.coreName, argumentCount, compileOnly)
    }

    Word createPrimitiveWord(String wordName, File behaviorScriptFile, Integer argumentCount = 0, Boolean compileOnly = false) {
        String behaviorScript = behaviorScriptFile.text
        return wordService.addWordToDictionary(wordName, null, behaviorScript, this.coreName, argumentCount, compileOnly)
    }

    Word createComplexWord(String wordName, List<Word> words, Integer argumentCount = 0, Boolean compileOnly = false) {
        return wordService.addWordToDictionary(wordName, words, null, this.coreName, argumentCount, compileOnly)
    }


    void createCoreDictionary() {

        // Primitive words with their behavior described in a Groovy Script
        Word plus = createPrimitiveWord("+", "arg1 + arg2", 2)              // Good
        Word minus = createPrimitiveWord("-", "arg1 - arg2", 2)             // Good
        Word dot = createPrimitiveWord(".", "print arg1", 1)                // Good
        Word cr = createPrimitiveWord("cr", "println()")                                  // Good
        Word emit = createPrimitiveWord("emit", "print((char) arg1)", 1)    // Good
        Word dotQuote = createPrimitiveWord(".\"", new File("${SCRIPTS}/dotQuote.groovy"))   // Good
        Word lessThanZero = createPrimitiveWord("0<", "arg1 < 0", 1)        // Good
        Word equalsZero = createPrimitiveWord("0=", "arg1 == 0", 1)          //Good
        Word greaterThanZero = createPrimitiveWord("0>", "arg1 > 0", 1)     // Good
        Word onePlus = createPrimitiveWord("1+", "arg1++", 1)               // Good
        Word oneMinus = createPrimitiveWord("1-", "arg1 - 1", 1)            // Good
        Word twoPlus = createPrimitiveWord("2+", "arg1 + 2", 1)             // Good
        Word twoMinus = createPrimitiveWord("2-", "arg1 - 2", 1)            // Good
        Word twoTimes = createPrimitiveWord("2*", "arg1 * 2", 1)            // good
        Word twoDivide = createPrimitiveWord("2/", "arg1 / 2", 1)           // Good
        Word lessThan = createPrimitiveWord("<", "arg1 < arg2", 2)          // Good
        Word greaterThan = createPrimitiveWord(">", "arg1 > arg2", 2)       // Good
        Word equal = createPrimitiveWord("=", "arg1 == arg2", 2)// Good
        Word colon = createPrimitiveWord(":", new File("${SCRIPTS}/colon.groovy"))
        Word semicolon = createPrimitiveWord(";", new File("${SCRIPTS}/semicolon.groovy"))

        //Word If = createPrimitiveWord("if", new File("${SCRIPTS}/If.groovy"), 1, true)
        //Word Else = createPrimitiveWord("else", new File("${SCRIPTS}/Else.groovy"), 1, true)
        //Word Then = createPrimitiveWord("then", new File("${SCRIPTS}/Else.groovy"), 1, true)

        // Complex words that are made up of a List<Word> that describes their behavior go here.
        Word add = createComplexWord("add", [plus, dot, cr], 2) // TODO This is a test word REMOVE
        Word sub = createComplexWord("sub", [minus, dot, cr], 2) // TODO This is a test word REMOVE
    }
}
