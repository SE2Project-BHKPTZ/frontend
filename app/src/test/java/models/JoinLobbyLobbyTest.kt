package models


import at.aau.serg.models.JoinLobbyLobby
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JoinLobbyLobbyTest {
    @Test
    fun `JoinLobbyLobby instantiation`() {
        val lobbyjoin = JoinLobbyLobby("TestLobby", 1,3,"lobbyid")
        assertEquals("TestLobby", lobbyjoin.name)
        assertEquals(1, lobbyjoin.currentPlayers)
        assertEquals(3, lobbyjoin.maxPlayers)
        assertEquals("lobbyid", lobbyjoin.lobbyID)
    }
}