package models


import at.aau.serg.models.LobbyJoin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyJoinTest {
    @Test
    fun `LobbyJoin instantiation`() {
        val lobbyJoin = LobbyJoin("TestLobbyID")
        assertEquals("TestLobbyID", lobbyJoin.lobbyID)
    }
}