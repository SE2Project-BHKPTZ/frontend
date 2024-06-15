package models

import at.aau.serg.models.CardItemDeserializer
import at.aau.serg.models.Suit
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardItemDeserializerTest {
    private val deserializer = CardItemDeserializer()

    @Test
    fun `deserialize should return CardItem with valid JSON`() {
        val json = JsonObject().apply {
            add("value", JsonPrimitive("10"))
            add("suit", JsonPrimitive("HEARTS"))
        }
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("10", result.value)
        assertEquals(Suit.HEARTS, result.suit)
    }

    @Test
    fun `deserialize should handle missing fields gracefully`() {
        val json = JsonObject()
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("", result.value)
        assertEquals(Suit.NOSUIT, result.suit)
    }

    @Test
    fun `deserialize should handle null JSON gracefully`() {
        val json: JsonElement? = null
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("", result.value)
        assertEquals(Suit.NOSUIT, result.suit)
    }
}