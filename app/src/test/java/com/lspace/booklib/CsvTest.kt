package com.lspace.booklib

import com.lspace.booklib.data.importexport.Csv
import org.junit.Assert.assertEquals
import org.junit.Test

class CsvTest {

    @Test
    fun encodesFieldsNeedingQuotes() {
        assertEquals("plain", Csv.encodeField("plain"))
        assertEquals("\"a,b\"", Csv.encodeField("a,b"))
        assertEquals("\"she said \"\"hi\"\"\"", Csv.encodeField("she said \"hi\""))
        assertEquals("\"line1\nline2\"", Csv.encodeField("line1\nline2"))
    }

    @Test
    fun parsesQuotedFieldsAndNewlines() {
        val text = "a,b,c\n\"x,1\",\"y\"\"2\",z\n"
        val rows = Csv.parse(text)
        assertEquals(2, rows.size)
        assertEquals(listOf("a", "b", "c"), rows[0])
        assertEquals(listOf("x,1", "y\"2", "z"), rows[1])
    }

    @Test
    fun handlesCarriageReturns() {
        val rows = Csv.parse("a,b\r\n1,2\r\n")
        assertEquals(listOf("a", "b"), rows[0])
        assertEquals(listOf("1", "2"), rows[1])
    }
}
