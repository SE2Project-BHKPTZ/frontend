package models


import at.aau.serg.models.LobbyKick
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyKickTest {
    @Test
    fun `LobbyKick instantiation`() {
        val lobbyKick = LobbyKick("PlayerUUID")
        assertEquals("PlayerUUID", lobbyKick.uuid)
    }
}