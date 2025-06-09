package com.example.saborchef.infrastructure

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonToken
import java.io.IOException
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class OffsetDateTimeAdapter(
    private val formatterWithOffset: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    private val formatterNoOffset: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
) : TypeAdapter<OffsetDateTime>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: OffsetDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatterWithOffset.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): OffsetDateTime? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val str = reader.nextString()
        return try {
            // Intentamos parsear con offset (Z o +hh:mm)
            OffsetDateTime.parse(str, formatterWithOffset)
        } catch (e: DateTimeParseException) {
            // Si no trae offset, lo parseamos como LocalDateTime y forzamos UTC
            val ldt = LocalDateTime.parse(str, formatterNoOffset)
            ldt.atOffset(ZoneOffset.UTC)
        }
    }
}
