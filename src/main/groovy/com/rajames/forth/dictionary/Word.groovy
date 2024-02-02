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
@Table(name = "word", indexes = [@Index(name = "word_name_index", columnList = "name")])
class Word implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    @Version
    private int version

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDateTime

    @Column(nullable = false)
    private String name

    @Column(columnDefinition = "TEXT", nullable = true)
    private String runtimeClass

    @Column(columnDefinition = "TEXT", nullable = true)
    private String compileClass

    @Column
    private Integer argumentCount = 0

    @Column
    private Boolean compileOnly = false

    @Column(nullable = true)
    private Integer stackValue

    @Column(nullable = true)
    private String stringLiteral

    @ManyToOne
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary

    @OneToMany(mappedBy = "parentWord", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "complex_word_order")
    private List<Word> forthWords = new ArrayList<>()

    @ManyToOne
    @JoinColumn(name = "parent_word_name")
    private Word parentWord

    @PrePersist
    void onCreate() {
        this.setCreateDateTime(new Date())
    }

    void setArgumentCount(Integer argumentCount) {
        this.argumentCount = argumentCount
    }

    Long getId() {
        return id
    }

    List<Word> getForthWords() {
        return forthWords
    }

    void setForthWords(List<Word> forthWords) {
        this.forthWords.clear()
        if (forthWords != null) {
            this.forthWords.addAll(forthWords)
        }
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary
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

    Integer getStackValue() {
        return stackValue
    }

    void setStackValue(Integer stackValue) {
        this.stackValue = stackValue
    }

    String getStringLiteral() {
        return stringLiteral
    }

    void setStringLiteral(String stringLiteral) {
        this.stringLiteral = stringLiteral
    }

    String getRuntimeClass() {
        return runtimeClass
    }

    void setRuntimeClass(String runtimeClass) {
        this.runtimeClass = runtimeClass
    }

    String getCompileClass() {
        return compileClass
    }

    void setCompileClass(String compileClass) {
        this.compileClass = compileClass
    }

    Date getCreateDateTime() {
        return createDateTime
    }

    void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime
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

    String toString() {
        StringBuilder sb = new StringBuilder("\n${this.name}:\n")
        if (this.forthWords.size() > 0) {
            sb.append("\n\tCompiled Definition: ").append(this.forthWords)
        } else {
            if (this.runtimeClass) {
                sb.append("\n\tRuntime Behavior: Scripted & available.")
            }
            if (this.compileClass) {
                sb.append("\n\tCompiler Behavior: Scripted & available.")
            }
            sb.append("\n\tStack expectancy: ").append(this.argumentCount)
            sb.append("\n\tCompiler word: ").append((this.compileOnly))
        }
        return sb.toString()
    }
}