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

package compiler

import com.rajames.forth.compiler.AbstractCompile
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ConcurrentLinkedQueue

class DotQuoteC extends AbstractCompile {
// : plus 1 1 + ." Hello World " . cr ;
    @Override
    @Transactional
    Boolean execute(Word word, ForthCompiler compiler, ForthInterpreter interpreter) {
        ConcurrentLinkedQueue<Word> words = interpreter.words
        ConcurrentLinkedQueue<String> nonWords = interpreter.nonWords
        Word nextWord = words.remove()
        while (!nonWords.isEmpty()) {
            String stringLiteral = nonWords.remove()
            if (stringLiteral == "\"") {
                break
            }
            Word literal = compiler.literal
            Word wordLiteral = new Word()
            wordLiteral.name = "str_${literal.name}_${UUID.randomUUID().toString()}"
            wordLiteral.runtimeClass = literal.runtimeClass
            wordLiteral.stringLiteral = stringLiteral - "\""
            wordLiteral.compileOnly = true
            wordLiteral.dictionary = compiler.dictionary
            wordLiteral.parentWord = compiler.newWord
            compiler.wordService.save(wordLiteral)

            compiler.forthWordsBuffer.add(wordLiteral)
//                word.forthWords.add(wordLiteral)
//                compiler.wordService.save(word)
            if (stringLiteral.endsWith("\"")) {
                break
            }
        }
//        compiler.newWord.forthWords.add(nextWord)
        compiler.forthWordsBuffer.add(nextWord)
        return false
    }
}
