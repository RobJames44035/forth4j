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

class CoreDefinitions {
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

    Word createPrimitiveWord(String wordName, String runtimeClass = null, String compileClass = null, Integer argumentCount = 0, Boolean compileOnly = false) {
        try {
            File runtime = null
            File compile = null
            if (null != runtimeClass) {
                runtime = new File("${RUNTIME}/${runtimeClass}.groovy")
            }
            if (null != compileClass) {
                compile = new File("${COMPILE}/${compileClass}.groovy")
            }
            return wordService.addWordToDictionary(wordName, coreName, null, runtime?.text, compile?.text, argumentCount, compileOnly)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create primitive word ${wordName}.", e)
        }
    }

    Word createComplexWord(String wordName, List<Word> words, Integer argumentCount = 0, Boolean compileOnly = false) {
        try {
            return wordService.addWordToDictionary(wordName, coreName, words, null, null, argumentCount, compileOnly)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create complex word ${wordName}.", e)
        }
    }


    void createCoreDictionary() {

        // Primitive words with their behavior described in a Groovy Script
        Word noop = createPrimitiveWord("noop")
        Word nop = createPrimitiveWord("nop")
        Word plus = createPrimitiveWord("+", "Plus", null, 2)
        Word minus = createPrimitiveWord("-", "Minus", null, 2)
        Word dot = createPrimitiveWord(".", "Dot", null, 2)
        Word cr = createPrimitiveWord("cr", "Cr")
        Word emit = createPrimitiveWord("emit", "Emit", null, 1)
        Word dotQuote = createPrimitiveWord(".\"", "DotQuote", "Noop")
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
        Word colon = createPrimitiveWord(":", "Colon")
        Word semicolon = createPrimitiveWord(";", null, "Noop")
        Word literal = createPrimitiveWord("literal", "Literal", null, 0, true)


//        Word ifWord = createPrimitiveWord("if", new File("${RUNTIME}/if.groovy"), 1, true)
//        Word elseWord = createPrimitiveWord("else", new File("${RUNTIME}/else.groovy"), 1, true)
//        Word thenWord = createPrimitiveWord("then", new File("${RUNTIME}/then.groovy"), 1, true)

        // Complex words that are made up of a List<Word> that describes their behavior go here.
        Word add = createComplexWord("add", [plus, dot, cr], 2) // TODO This is a test word REMOVE
        Word sub = createComplexWord("sub", [minus, dot, cr], 2) // TODO This is a test word REMOVE

        databaseBackupService.backupDatabase("/home/rajames/PROJECTS/forth4j/src/main/resources", "CoreForth4j.sql")
    }
}
