package com.rajames.forth.dictionary

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@EqualsAndHashCode
@TupleConstructor(includeSuperProperties = true, includeFields = true, includeProperties = true)
@Entity
@Table(name = "dictionary")
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    Integer getId() {
        return id
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Set<Word> getWords() {
        return words
    }

    void setWords(Set<Word> words) {
        this.words = words
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Word> words;

    // constructors, getters and setters ...
}

