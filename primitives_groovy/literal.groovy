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
log.info("literal.groovy")
log.debug("TOKENS: " + tokens)
log.debug("LINE: " + line)
if(word.name.startsWith("int_")) {
    return word.stackValue
} else if(word.name.startsWith("str_")) {
    print(word.stringLiteral)
}