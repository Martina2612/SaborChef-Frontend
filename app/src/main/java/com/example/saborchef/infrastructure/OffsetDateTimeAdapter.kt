package com.example.saborchef.infrastructure

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonToken.NULL
import java.io.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeAdapter(private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME) : TypeAdapter<OffsetDateTime>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter?, value: OffsetDateTime?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(formatter.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader?): OffsetDateTime? {
        reader ?: return null

        return when (reader.peek()) {
            NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                val dateStr = reader.nextString()
                try {
                    OffsetDateTime.parse(dateStr, formatter)
                } catch (e: Exception) {
                    // Intentamos como LocalDateTime sin zona horaria
                    try {
                        val local = java.time.LocalDateTime.parse(dateStr)
                        local.atOffset(java.time.ZoneOffset.UTC)
                    } catch (e2: Exception) {
                        throw IOException("Cannot parse date: $dateStr", e2)
                    }
                }
            }
        }
    }

}
