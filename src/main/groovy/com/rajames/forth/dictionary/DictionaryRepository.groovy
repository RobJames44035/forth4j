package com.rajames.forth.dictionary

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DictionaryRepository extends CrudRepository<Dictionary, Integer> {
    Optional<Dictionary> findByName(String name);
}