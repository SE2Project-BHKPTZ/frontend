package models

import at.aau.serg.models.Score
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ScoreTest {
    @Test
    fun `CardItem instantiation`() {
        val score = Score("50", 0)
        Assertions.assertEquals("50", score.score)
        Assertions.assertEquals(0, score.position)
    }
}