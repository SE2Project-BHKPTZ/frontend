package models

import at.aau.serg.models.LobbyStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyStatusTest {
    @Test
    fun `LobbyStatus instantiation`() {
        assertEquals("CREATED", LobbyStatus.CREATED.status)
        assertEquals("RUNNING", LobbyStatus.RUNNING.status)
        assertEquals("FINISHED", LobbyStatus.FINISHED.status)
    }
}