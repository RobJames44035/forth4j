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

-- H2 2.2.220;
SET DB_CLOSE_DELAY -1;
;
CREATE USER IF NOT EXISTS "SA" SALT '6935f13013f9f3b4' HASH 'd584ceea59cc2343fad2c94484e88ab3a43df0632ee576ff8a197c262645e54e' ADMIN;
CREATE SEQUENCE "PUBLIC"."HIBERNATE_SEQUENCE" START WITH 1 RESTART WITH 31;
CREATE MEMORY TABLE "PUBLIC"."DICTIONARY"
(
    "ID" INTEGER NOT NULL,
    "NAME" CHARACTER VARYING(255)
);
ALTER TABLE "PUBLIC"."DICTIONARY"
    ADD CONSTRAINT "PUBLIC"."CONSTRAINT_3" PRIMARY KEY ("ID");
-- 1 +/- SELECT COUNT(*) FROM PUBLIC.DICTIONARY;               
INSERT INTO "PUBLIC"."DICTIONARY"
VALUES (1, 'Core');
CREATE MEMORY TABLE "PUBLIC"."FORTHWORDS"
(
    "WORD_ID" BIGINT NOT NULL,
    "FORTH_WORD_NAME" CHARACTER VARYING(255)
);      
-- 8 +/- SELECT COUNT(*) FROM PUBLIC.FORTHWORDS;               
INSERT INTO "PUBLIC"."FORTHWORDS"
VALUES (29, '+'),
       (29, 'cr'),
       (29, '.'),
       (29, 'cr'),
       (30, '-'),
       (30, 'cr'),
       (30, '.'),
       (30, 'cr');
CREATE MEMORY TABLE "PUBLIC"."WORD"
(
    "ID"             BIGINT                 NOT NULL,
    "ARGUMENTCOUNT"  INTEGER,
    "COMPILECLASS"   CHARACTER VARYING,
    "COMPILEONLY"    BOOLEAN,
    "CREATEDATETIME" TIMESTAMP,
    "NAME"           CHARACTER VARYING(255) NOT NULL,
    "RUNTIMECLASS"   CHARACTER VARYING,
    "STACKVALUE"     INTEGER,
    "STRINGLITERAL"  CHARACTER VARYING(255),
    "VERSION"        INTEGER                NOT NULL,
    "DICTIONARY_ID"  INTEGER                NOT NULL,
    "PARENT_WORD_NAME" BIGINT
);
ALTER TABLE "PUBLIC"."WORD"
    ADD CONSTRAINT "PUBLIC"."CONSTRAINT_2" PRIMARY KEY ("ID");
-- 29 +/- SELECT COUNT(*) FROM PUBLIC.WORD;    
INSERT INTO "PUBLIC"."WORD"
VALUES (2, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.564', 'noop', NULL, NULL, NULL, 0, 1, NULL),
       (3, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.617', 'nop', NULL, NULL, NULL, 0, 1, NULL),
       (4, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.666', 'defdump',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Dump extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        Word dumpWord = interpreter?.words?.remove()\000a        println(dumpWord.toString())\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (5, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.696', '+',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Plus extends AbstractRuntime {\000a\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() + interpreter.dataStack.pop())\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 1, 1, 29),
       (6, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.717', '-',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Minus extends AbstractRuntime {\000a\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() - interpreter.dataStack.pop())\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 1, 1, 30),
       (7, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.741', '.',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Dot extends AbstractRuntime {\000a\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        print(interpreter.dataStack.pop())\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 2, 1, 30);
INSERT INTO "PUBLIC"."WORD"
VALUES (8, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.763', 'cr',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Cr extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        println()\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 2, 1, 30),
       (9, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.786', 'emit',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Emit extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        print((char) interpreter.dataStack.pop())\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (10, 0,
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage compiler\000a\000aimport com.rajames.forth.compiler.AbstractCompile\000aimport com.rajames.forth.compiler.ForthCompiler\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.ForthInterpreter\000aimport org.springframework.transaction.annotation.Transactional\000a\000aimport java.util.concurrent.ConcurrentLinkedQueue\000a\000aclass DotQuoteC extends AbstractCompile {\000a\000a    @Override\000a    @Transactional\000a    Boolean execute(Word word, ForthCompiler compiler, ForthInterpreter interpreter) {\000a        ConcurrentLinkedQueue<Word> words = interpreter.words\000a        ConcurrentLinkedQueue<String> nonWords = interpreter.nonWords\000a        Word nextWord = words.remove()\000a        while (!nonWords.isEmpty()) {\000a            String stringLiteral = nonWords.remove()\000a            if (stringLiteral == "\\"") {\000a                break\000a            }\000a            Word literal = compiler.literal\000a            Word wordLiteral = new Word()\000a            wordLiteral.name = "str_${literal.name}_${UUID.randomUUID().toString()}"\000a            wordLiteral.runtimeClass = literal.runtimeClass\000a            wordLiteral.stringLiteral = stringLiteral - "\\""\000a            wordLiteral.compileOnly = true\000a            wordLiteral.dictionary = compiler.dictionary\000a            wordLiteral.parentWord = compiler.newWord\000a            compiler.wordService.save(wordLiteral)\000a\000a            compiler.forthWordsBuffer.add(wordLiteral.name)\000a            if (stringLiteral.endsWith("\\"")) {\000a                break\000a            }\000a        }\000a        compiler.forthWordsBuffer.add(nextWord.name)\000a        return false\000a    }\000a}\000a',
        FALSE, TIMESTAMP '2024-02-02 12:33:07.818', '."',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aimport java.util.concurrent.ConcurrentLinkedDeque\000a\000aclass DotQuote extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        ConcurrentLinkedDeque<String> tokens = interpreter.tokens\000a        while (!tokens.isEmpty()) {\000a            String token = tokens.remove()\000a            print(token.replaceAll("\\"", "") + " ")\000a        }\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (11, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.843', '0<',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass LessThanZero extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() < 0 ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (12, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.864', '0=',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass EqualZero extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() == 0 ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (13, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.886', '0=',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass GreaterThanZero extends AbstractRuntime {\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() > 0 ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (14, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.912', '1+',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass OnePlus extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() + 1)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (15, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.939', '1-',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass OneMinus extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() - 1)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (16, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.964', '2+',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass TwoPlus extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() + 2)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (17, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:07.991', '2-',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass TwoMinus extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() - 2)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (18, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.02', '2*',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass TwoTimes extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() * 2)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (19, 1, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.05', '2/',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass TwoDivide extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push(interpreter.dataStack.pop() / 2)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (20, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.077', '<',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass LessThan extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push((interpreter.dataStack.pop() < interpreter.dataStack.pop()) ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (21, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.109', '>',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass GreaterThan extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push((interpreter.dataStack.pop() > interpreter.dataStack.pop()) ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL),
       (22, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.14', '=',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Equal extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        interpreter.dataStack.push((interpreter.dataStack.pop() == interpreter.dataStack.pop()) ? -1 : 0)\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (23, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.166', ':',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.compiler.ForthCompilerException\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000aimport org.apache.logging.log4j.LogManager\000aimport org.apache.logging.log4j.Logger\000a\000aimport java.util.concurrent.ConcurrentLinkedQueue\000a\000aclass Colon extends AbstractRuntime {\000a\000a    private static final Logger log = LogManager.getLogger(this.class.getName())\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a        // Fail Fast\000a        if (interpreter.words.stream().noneMatch(w -> w.getName().equals(";"))) {\000a            interpreter.words.clear()\000a            throw new ForthCompilerException("No ending semicolon")\000a        }\000a\000a        ConcurrentLinkedQueue<String> nonWords = interpreter.nonWords\000a        ConcurrentLinkedQueue<Word> words = interpreter.words\000a        ConcurrentLinkedQueue<Integer> arguments = new ConcurrentLinkedQueue<>()\000a\000a        while (interpreter.dataStack.size() > 0) {\000a            Integer argument = interpreter.dataStack.pop() as Integer\000a            arguments.add(argument)\000a        }\000a\000a        Word w = interpreter.forthCompiler.compileWord(words, arguments, nonWords)\000a        w = interpreter.wordService.findByName(w.name)\000a        log.trace("Colon: Word after ''interpreter.wordService.findByName(w.name)'' this?.word?.name = ${w?.name} this?.word?.forthWords = ${w?.forthWords}")\000a        return null\000a    }\000a}',
        NULL, NULL, 0, 1, NULL),
       (24, 0,
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage compiler\000a\000aimport com.rajames.forth.compiler.AbstractCompile\000aimport com.rajames.forth.compiler.ForthCompiler\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass SemiColonC extends AbstractCompile {\000a\000a    @Override\000a    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {\000a        return false\000a    }\000a}\000a',
        FALSE, TIMESTAMP '2024-02-02 12:33:08.197', ';', NULL, NULL, NULL, 0, 1, NULL),
       (25, 0, NULL, TRUE, TIMESTAMP '2024-02-02 12:33:08.243', 'literal',
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage runtime\000a\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.AbstractRuntime\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass Literal extends AbstractRuntime {\000a\000a    @Override\000a    Object execute(ForthInterpreter interpreter, Word word) {\000a\000a        // Code goes here\000a        if (word.name.startsWith("int_")) {\000a            Word lit = word\000a            Integer arg = lit.stackValue\000a            interpreter.dataStack.push(arg)\000a        } else if (word.name.startsWith("str_")) {\000a            Word lit = word\000a            String string = lit.stringLiteral\000a            if (string != "\\"") {\000a                print(word.stringLiteral.replaceAll("\\"", "") + " ")\000a            }\000a        }\000a        return null\000a    }\000a}\000a',
        NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (26, 1,
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage compiler\000a//    : ?full 12 = if ." It''s full " then ." Hello World" ;\000a\000aimport com.rajames.forth.compiler.AbstractCompile\000aimport com.rajames.forth.compiler.ForthCompiler\000aimport com.rajames.forth.compiler.ForthCompilerException\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass IfC extends AbstractCompile {\000a    @Override\000a    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {\000a        List<String> tokens = newWord.getForthWords()\000a        Stack<Integer> ctrlFlowStack = new Stack<>()\000a        int currentIndex = 0\000a\000a        for (token in compiler.forthWordsBuffer) {\000a            Word word = interpreter.wordService.findByName(token)\000a            if (word != null) {\000a                if (word.name == "if") {\000a                    // When we see an IF, we add a placeholder (use a unique string)\000a                    // where we''ll later put a jump instruction\000a                    tokens[currentIndex] = "<placeholder for jump if false>"\000a                    // push the current index onto the control flow stack\000a                    ctrlFlowStack.push(currentIndex)\000a                }\000a                else if (word.name == "then") {\000a                    if (ctrlFlowStack.size() == 0) {\000a                        throw new ForthCompilerException("Mismatched THEN")\000a                    }\000a                    // We''ve encountered a THEN. Replace the jump placeholder with a real jump location\000a                    int ifIndex = ctrlFlowStack.pop()\000a                    String jumpTo = Integer.toString(currentIndex)\000a                    tokens[ifIndex] = jumpTo\000a                }\000a            }\000a            currentIndex++;\000a        }\000a\000a        // Check if we have any unmatched IFs\000a        if (!ctrlFlowStack.isEmpty()) {\000a            throw new ForthCompilerException("Unmatched IF")\000a        }\000a\000a        newWord.setForthWords(tokens)\000a        return false\000a    }\000a}\000a',
        TRUE, TIMESTAMP '2024-02-02 12:33:08.273', 'if', NULL, NULL, NULL, 0, 1, NULL),
       (27, 0, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.298', 'else', NULL, NULL, NULL, 0, 1, NULL),
       (28, 0,
        U&'/*\000a * Copyright 2024 Robert A. James\000a *\000a * Licensed under the Apache License, Version 2.0 (the "License");\000a * you may not use this file except in compliance with the License.\000a * You may obtain a copy of the License at\000a *\000a *   http://www.apache.org/licenses/LICENSE-2.0\000a *\000a * Unless required by applicable law or agreed to in writing, software\000a * distributed under the License is distributed on an "AS IS" BASIS,\000a * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\000a * See the License for the specific language governing permissions and\000a * limitations under the License.\000a */\000a\000apackage compiler\000a\000aimport com.rajames.forth.compiler.AbstractCompile\000aimport com.rajames.forth.compiler.ForthCompiler\000aimport com.rajames.forth.dictionary.Word\000aimport com.rajames.forth.runtime.ForthInterpreter\000a\000aclass ThenC extends AbstractCompile {\000a    @Override\000a    Boolean execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {\000a        return false\000a    }\000a}\000a',
        FALSE, TIMESTAMP '2024-02-02 12:33:08.322', 'then', NULL, NULL, NULL, 0, 1, NULL);
INSERT INTO "PUBLIC"."WORD"
VALUES (29, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.361', 'add', NULL, NULL, NULL, 4, 1, NULL),
       (30, 2, NULL, FALSE, TIMESTAMP '2024-02-02 12:33:08.464', 'sub', NULL, NULL, NULL, 4, 1, NULL);
CREATE INDEX "PUBLIC"."WORD_NAME_INDEX" ON "PUBLIC"."WORD" ("NAME" NULLS FIRST);
ALTER TABLE "PUBLIC"."DICTIONARY"
    ADD CONSTRAINT "PUBLIC"."UK_BFLAUXN62LF5A1PEXOI1Q9UTX" UNIQUE ("NAME");
ALTER TABLE "PUBLIC"."WORD"
    ADD CONSTRAINT "PUBLIC"."FKGOXHLJHA8FXBLY06SVO210LJ" FOREIGN KEY ("PARENT_WORD_NAME") REFERENCES "PUBLIC"."WORD" ("ID") NOCHECK;
ALTER TABLE "PUBLIC"."FORTHWORDS"
    ADD CONSTRAINT "PUBLIC"."FK6R5Y1XEMN44YELFD5VUUTHOPF" FOREIGN KEY ("WORD_ID") REFERENCES "PUBLIC"."WORD" ("ID") NOCHECK;
ALTER TABLE "PUBLIC"."WORD"
    ADD CONSTRAINT "PUBLIC"."FKPEVQ98N5Q80GKUHDF3YIK5SBA" FOREIGN KEY ("DICTIONARY_ID") REFERENCES "PUBLIC"."DICTIONARY" ("ID") NOCHECK;
