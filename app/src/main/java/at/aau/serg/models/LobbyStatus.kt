package at.aau.serg.models

enum class LobbyStatus(val status: String) {
    CREATED("CREATED"),
    RUNNING("RUNNING"),
    FINISHED("FINISHED")
}