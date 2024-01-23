package com.rajames.forth.dictionary

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository extends CrudRepository<Word, String> {
    Optional<Word> findByName(String name);
}