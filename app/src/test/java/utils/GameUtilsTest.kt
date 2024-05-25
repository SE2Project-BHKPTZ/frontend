package utils

import at.aau.serg.utils.GameUtils.calculatePositionOfPlayer
import org.junit.jupiter.api.Test
import org.junit.Assert.assertEquals

class GameUtilsTest {
    @Test
    fun testCalculatePositionOfPlayerSameIndex() {
        assertEquals(1, calculatePositionOfPlayer(0, 0, 6))
        assertEquals(1, calculatePositionOfPlayer(5, 5, 6))
    }

    @Test
    fun testCalculatePositionOfOtherPositions() {
        assertEquals(6, calculatePositionOfPlayer(0, 1, 6))
        assertEquals(5, calculatePositionOfPlayer(0, 2, 6))
        assertEquals(4, calculatePositionOfPlayer(0, 3, 6))
        assertEquals(3, calculatePositionOfPlayer(0, 4, 6))
        assertEquals(2, calculatePositionOfPlayer(0, 5, 6))
    }
}