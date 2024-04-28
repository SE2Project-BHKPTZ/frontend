package at.aau.serg.models

data class LobbyPlayer(var uuid: String, var name: String, var isVisible: Visibilities) {
    constructor() : this(uuid ="",name="",isVisible=Visibilities.INVISIBLE)

}