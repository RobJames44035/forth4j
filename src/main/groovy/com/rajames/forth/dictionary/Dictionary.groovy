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
//file:noinspection unused

package com.rajames.forth.dictionary

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import javax.persistence.*

@EqualsAndHashCode
@TupleConstructor(includeSuperProperties = true, includeFields = true, includeProperties = true)
@Entity
@Table(name = "dictionary")
class Dictionary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id

    @Column(unique=true)
    private String name

    @OneToMany(mappedBy = "dictionary", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Word> words


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

