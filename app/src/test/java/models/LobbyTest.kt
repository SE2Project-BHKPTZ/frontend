package models

import at.aau.serg.models.Lobby
import at.aau.serg.models.LobbyStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyTest {
    private val uuid = "12345"
    private val lobbyid = "lobby123"
    private val status = LobbyStatus.CREATED
    private val name = "Test Lobby"
    private val timestamp = "2024-06-15T12:34:56Z"
    private val players = listOf(mapOf("id" to "player1", "name" to "Player One"))
    private val maxPlayers = 10
    private val isPublic = true
    private val maxRounds = 5

    @Test
    fun `Lobby instantiation`() {
        val lobby = Lobby(uuid, lobbyid, status, name, timestamp, players, maxPlayers, isPublic, maxRounds)

        assertEquals(uuid, lobby.uuid)
        assertEquals(lobbyid, lobby.lobbyid)
        assertEquals(status, lobby.status)
        assertEquals(name, lobby.name)
        assertEquals(timestamp, lobby.timestamp)
        assertEquals(players, lobby.players)
        assertEquals(maxPlayers, lobby.maxPlayers)
        assertEquals(isPublic, lobby.isPublic)
        assertEquals(maxRounds, lobby.maxRounds)
    }
}