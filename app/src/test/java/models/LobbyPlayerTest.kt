package models


import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Visibilities
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyPlayerTest {
    @Test
    fun `LobbyPlayer instantiation`() {
        val lobbyPlayer = LobbyPlayer("TestPlayerUUID","TestPlayer" ,Visibilities.VISIBLE)
        assertEquals("TestPlayerUUID", lobbyPlayer.uuid)
        assertEquals("TestPlayer", lobbyPlayer.name)
        assertEquals(Visibilities.VISIBLE, lobbyPlayer.isVisible)
    }

    @Test
    fun `LobbyPlayer setter`() {
        val lobbyPlayer = LobbyPlayer("TestPlayerUUID","TestPlayer" ,Visibilities.VISIBLE)

        lobbyPlayer.uuid = "otherUUID"
        lobbyPlayer.name = "otherName"
        lobbyPlayer.isVisible = Visibilities.INVISIBLE

        assertEquals("otherUUID", lobbyPlayer.uuid)
        assertEquals("otherName", lobbyPlayer.name)
        assertEquals(Visibilities.INVISIBLE, lobbyPlayer.isVisible)
    }
}