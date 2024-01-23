package com.rajames.forth.dictionary

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import javax.persistence.*

@EqualsAndHashCode
@TupleConstructor(includeSuperProperties = true, includeFields = true, includeProperties = true)
@Entity
@Table(name = "dictionary")
public class Dictionary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique=true)
    private String name;

    @OneToMany(mappedBy = "dictionary", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Word> words;

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

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        java.util.Dictionary that = (java.util.Dictionary) o

        if (id != that.id) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }
}

