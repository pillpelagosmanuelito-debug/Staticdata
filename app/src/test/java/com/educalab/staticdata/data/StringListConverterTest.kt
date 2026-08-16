package com.educalab.staticdata.data

import com.educalab.staticdata.data.local.converters.StringListConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StringListConverterTest {

    @Test
    fun `encode then decode returns the original list`() {
        val original = listOf("Manzana", "Pera", "Uva")
        assertEquals(original, StringListConverter.decode(StringListConverter.encode(original)))
    }

    @Test
    fun `decode of empty string returns empty list`() {
        assertTrue(StringListConverter.decode("").isEmpty())
    }

    @Test
    fun `encode of single element list has no separator artifacts`() {
        val encoded = StringListConverter.encode(listOf("SoloUno"))
        assertEquals(listOf("SoloUno"), StringListConverter.decode(encoded))
    }

    @Test
    fun `round trip preserves labels containing commas and accents`() {
        val original = listOf("Ciencia ficción, aventura", "Ñandú")
        assertEquals(original, StringListConverter.decode(StringListConverter.encode(original)))
    }
}
