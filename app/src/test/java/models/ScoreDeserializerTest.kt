package models

import at.aau.serg.models.ScoreDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScoreDeserializerTest {
    private val deserializer = ScoreDeserializer()

    @Test
    fun `deserialize should return Score with valid JSON`() {
        val json = JsonObject().apply {
            add("score", JsonPrimitive("100"))
            add("index", JsonPrimitive(1))
        }
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("100", result.score)
        assertEquals(1, result.position)
    }

    @Test
    fun `deserialize should handle missing fields gracefully`() {
        val json = JsonObject()
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("", result.score)
        assertEquals(0, result.position)
    }

    @Test
    fun `deserialize should handle null JSON gracefully`() {
        val json: JsonElement? = null
        val context = mockk<JsonDeserializationContext>()

        val result = deserializer.deserialize(json, null, context)

        assertEquals("", result.score)
        assertEquals(0, result.position)
    }
}