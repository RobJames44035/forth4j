package com.rajames.forth.dictionary

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DictionaryRepository extends JpaRepository<Dictionary, Integer> {
}