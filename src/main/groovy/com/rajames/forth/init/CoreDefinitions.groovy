package com.rajames.forth.init

import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService

class CoreDefinitions {
    private final WordService wordService
    private String coreName

    CoreDefinitions(WordService wordService, String dictionaryName) {
        this.wordService = wordService
        this.coreName = dictionaryName
    }

    Word createPrimitiveWord(String wordName, String behaviorScript, Integer argumentCount = 0) {
        return wordService.addWordToDictionary(wordName, null, behaviorScript, this.coreName, argumentCount)
    }

    Word createComplexWord(String wordName, List<Word> words, Integer argumentCount = 0) {
        return wordService.addWordToDictionary(wordName, words, null, this.coreName, argumentCount) // TODO
    }


    void createCoreDictionary() {

        // Primitive words with their behavior described in a Groovy Script
        Word plus = createPrimitiveWord("+", "arg1 + arg2", 2)
        Word dot = createPrimitiveWord(".", "print arg1", 1)
        Word cr = createPrimitiveWord("cr", "println()")
        Word lessThanZero = createPrimitiveWord("0<", "arg1 < 0", 1)
        Word dotQuote = createPrimitiveWord(".\"", """
    StringBuilder sb = new StringBuilder()
    while (!tokens.isEmpty()) {
        String token = tokens.remove()
        if(token == '\"') {
            break
        } else if(token.endsWith('\"')) {
            token = token - '\"'
            sb.append(token).append(' ')
            break
        } else {
            sb.append(token).append(' ')
        }
   }
    print(sb.toString())
""")

        // Complex words that are made up of a List<Word> that describes their behavior go here.
        Word add = createComplexWord("add", [plus, dot, cr], 2) // TODO This is a test word REMOVE
    }
}
