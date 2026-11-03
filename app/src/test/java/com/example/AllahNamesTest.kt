package com.example

import com.example.data.AllahNamesData
import org.junit.Assert.*
import org.junit.Test

class AllahNamesTest {

    @Test
    fun testAll99NamesExist() {
        val names = AllahNamesData.names
        assertEquals("There must be exactly 99 Names of Allah", 99, names.size)
    }

    @Test
    fun testSequentialIdsFrom1To99() {
        val names = AllahNamesData.names
        names.forEachIndexed { index, name ->
            val expectedId = index + 1
            assertEquals("Name ID must be sequential from 1 to 99", expectedId, name.id)
        }
    }

    @Test
    fun testNoEmptyFieldsInAnyName() {
        val names = AllahNamesData.names
        for (item in names) {
            assertTrue("Arabic must not be blank for ID ${item.id}", item.arabic.isNotBlank())
            assertTrue("Bengali transliteration must not be blank for ID ${item.id}", item.transliterationBn.isNotBlank())
            assertTrue("English transliteration must not be blank for ID ${item.id}", item.transliterationEn.isNotBlank())
            assertTrue("Bengali meaning must not be blank for ID ${item.id}", item.meaningBn.isNotBlank())
            assertTrue("English meaning must not be blank for ID ${item.id}", item.meaningEn.isNotBlank())
            assertTrue("Bengali fazilat must not be blank for ID ${item.id}", item.fazilatBn.isNotBlank())
        }
    }

    @Test
    fun testFirstAndLastNames() {
        val first = AllahNamesData.names.first()
        assertEquals(1, first.id)
        assertEquals("الرَّحْمَنُ", first.arabic)
        assertEquals("আর-রহমান", first.transliterationBn)
        assertEquals("Ar-Rahman", first.transliterationEn)

        val last = AllahNamesData.names.last()
        assertEquals(99, last.id)
        assertEquals("الصَّبُورُ", last.arabic)
        assertEquals("আস-সাবূর", last.transliterationBn)
        assertEquals("As-Sabur", last.transliterationEn)
    }

    @Test
    fun testSearchFunctionality() {
        val searchByBangla = AllahNamesData.search("রহমান")
        assertTrue(searchByBangla.any { it.id == 1 })

        val searchByEnglish = AllahNamesData.search("Malik")
        assertTrue(searchByEnglish.any { it.id == 3 })

        val searchByMeaning = AllahNamesData.search("শান্তি")
        assertTrue(searchByMeaning.any { it.id == 5 })

        val searchById = AllahNamesData.search("99")
        assertEquals(1, searchById.size)
        assertEquals(99, searchById.first().id)
    }
}
