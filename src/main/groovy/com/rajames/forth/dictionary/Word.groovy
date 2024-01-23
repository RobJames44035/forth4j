package com.rajames.forth.dictionary

import javax.persistence.*

@Entity
@Table(name = "word")
class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String behaviorScript

    @ManyToOne
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
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

    Dictionary getDictionary() {
        return dictionary
    }

    void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary
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