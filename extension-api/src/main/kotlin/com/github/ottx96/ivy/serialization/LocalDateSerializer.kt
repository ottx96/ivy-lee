package com.github.ottx96.ivy.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LOCAL_DATE_SERIALIZER", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DATE_FORMAT)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString( value.format(DATE_FORMAT) )
    }
}