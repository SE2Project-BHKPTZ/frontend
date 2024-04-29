package models


import at.aau.serg.models.LobbyCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyCreateTest {
    @Test
    fun `LobbyCreate instantiation`() {
        val lobbyCreate = LobbyCreate("TestLobby", 1,3)
        assertEquals("TestLobby", lobbyCreate.name)
        assertEquals(1, lobbyCreate.isPublic)
        assertEquals(3, lobbyCreate.maxPlayers)
    }
}