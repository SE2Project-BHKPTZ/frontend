package at.aau.serg.models

data class Lobby (
    val uuid: String,
    val lobbyid: String,
    val status: LobbyStatus,
    val name: String,
    val timestamp: String,
    val players: List<Map<String, Any>>,
    val maxPlayers: Int? = null,
    val isPublic: Boolean = false,
    val maxRounds: Int
)