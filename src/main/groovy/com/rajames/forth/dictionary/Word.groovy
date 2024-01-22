package com.rajames.forth.dictionary

import javax.persistence.*
import org.springframework.lang.NonNull
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode
@TupleConstructor(includeSuperProperties = true, includeFields = true, includeProperties = true)
@Entity
@Table(name = "word")
class Word {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NonNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;
}

