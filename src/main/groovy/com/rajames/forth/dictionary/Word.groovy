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

    @Column(nullable = true, name = "complex_word_order")
    private Integer complexWordOrder

    @ManyToOne
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary

    @OneToMany(mappedBy = "parentWord", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "complex_word_order")
    private List<Word> forthWords = new ArrayList<>()

    @ManyToOne
    @JoinColumn(name = "parent_word_id")
    private Word parentWord

    @PrePersist
    void onCreate() {
        this.setCreateDateTime(new Date())
    }

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

    void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary
    }

    List<Word> getForthWords() {
        return forthWords
    }

    void setForthWords(List<Word> forthWords) {
        this.forthWords = forthWords
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

    Integer getComplexWordOrder() {
        return complexWordOrder
    }

    void setComplexWordOrder(Integer complexWordOrder) {
        this.complexWordOrder = complexWordOrder
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
        return this.name
    }
}