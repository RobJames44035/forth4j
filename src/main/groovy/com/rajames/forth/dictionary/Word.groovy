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
//file:noinspection JpaDataSourceORMInspection

package com.rajames.forth.dictionary

import javax.persistence.*

@Entity
@Table(name = "word")
class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    @Column(nullable = false)
    private String name

    @Column(columnDefinition = "TEXT")
    private String behaviorScript

    @Column
    private Integer argumentCount = 0

    @Column
    private Boolean compileOnly = false

    @ManyToOne
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary

    @OneToMany(mappedBy = "parentWord", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "child_order")
    private List<Word> childWords = new ArrayList<>()

    @ManyToOne
    @JoinColumn(name = "parent_word_id")
    private Word parentWord

    Integer getArgumentCount() {
        return argumentCount
    }

    void setArgumentCount(Integer argumentCount) {
        this.argumentCount = argumentCount
    }

    Long getId() {
        return id
    }


    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getBehaviorScript() {
        return behaviorScript
    }

    void setBehaviorScript(String behaviorScript) {
        this.behaviorScript = behaviorScript
    }


    void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary
    }

    List<Word> getChildWords() {
        return childWords
    }

    void setChildWords(List<Word> childWords) {
        this.childWords = childWords
    }

    Word getParentWord() {
        return parentWord
    }

    void setParentWord(Word parentWord) {
        this.parentWord = parentWord
    }

    Boolean getCompileOnly() {
        return compileOnly
    }

    void setCompileOnly(Boolean compileOnly) {
        this.compileOnly = compileOnly
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Word word = (Word) o

        if (id != word.id) return false
        if (name != word.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }
}