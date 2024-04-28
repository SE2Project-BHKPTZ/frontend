package models


import at.aau.serg.models.LobbyPlayer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyPlayerTest {
    @Test
    fun `LobbyPlayer instantiation`() {
        val lobbyPlayer = LobbyPlayer("TestPlayerUUID","TestPlayer" ,0)
        assertEquals("TestPlayerUUID", lobbyPlayer.uuid)
        assertEquals("TestPlayer", lobbyPlayer.name)
        assertEquals(0, lobbyPlayer.isVisible)
    }

    @Test
    fun `LobbyPlayer setter`() {
        val lobbyPlayer = LobbyPlayer("TestPlayerUUID","TestPlayer" ,0)

        lobbyPlayer.uuid = "otherUUID"
        lobbyPlayer.name = "otherName"
        lobbyPlayer.isVisible = 4

        assertEquals("otherUUID", lobbyPlayer.uuid)
        assertEquals("otherName", lobbyPlayer.name)
        assertEquals(4, lobbyPlayer.isVisible)
    }
}