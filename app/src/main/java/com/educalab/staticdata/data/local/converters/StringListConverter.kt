package com.educalab.staticdata.data.local.converters

import androidx.room.TypeConverter

/**
 * Convertidor ligero List<String> <-> String para columnas de Room, sin
 * depender de una librería JSON externa. Usa el carácter de control
 * "Unit Separator" (U+001F), que nunca aparece en texto escrito por un
 * niño, como delimitador — evita colisiones con comas o texto libre.
 */
class StringListConverter {

    companion object {
        private const val SEP = "\u001F"

        fun encode(list: List<String>): String = list.joinToString(SEP)

        fun decode(raw: String): List<String> =
            if (raw.isEmpty()) emptyList() else raw.split(SEP)
    }

    @TypeConverter
    fun fromList(list: List<String>): String = encode(list)

    @TypeConverter
    fun toList(raw: String): List<String> = decode(raw)
}
