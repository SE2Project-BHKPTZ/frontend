package utils

import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import at.aau.serg.utils.CardsConverter.convertCard
import at.aau.serg.utils.CardsConverter.convertCards
import io.mockk.every
import io.mockk.mockk
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class CardsConverterTest {
    @Test
    fun `test convertCards`() {
        // Mock data
        val jsonArray = mockk<JSONArray>()
        val jsonObject1 = mockk<JSONObject>()
        val jsonObject2 = mockk<JSONObject>()

        every { jsonArray.length() } returns 2
        every { jsonArray.getJSONObject(0) } returns jsonObject1
        every { jsonArray.getJSONObject(1) } returns jsonObject2
        every { jsonObject1.getString("value") } returns "10"
        every { jsonObject1.getString("suit") } returns "HEARTS"
        every { jsonObject2.getString("value") } returns "13"
        every { jsonObject2.getString("suit") } returns "CLUBS"

        // Expected result
        val expected = arrayOf(
            CardItem("10", Suit.HEARTS),
            CardItem("13", Suit.CLUBS)
        )

        // Call function
        val result = convertCards(jsonArray)

        // Assertions
        assertArrayEquals(expected, result)
    }

    @Test
    fun `test convertCard`() {
        // Mock data
        val jsonObject = mockk<JSONObject>()
        every { jsonObject.getString("value") } returns "10"
        every { jsonObject.getString("suit") } returns "SPADES"

        // Expected result
        val expected = CardItem("10", Suit.HEARTS)

        // Call function
        val result = convertCard(jsonObject)

        // Assertions
        assertEquals(expected, result)
    }
}