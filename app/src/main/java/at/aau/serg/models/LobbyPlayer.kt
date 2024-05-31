package at.aau.serg.models

import java.io.Serializable

data class LobbyPlayer(var uuid: String, var name: String, var isVisible: Visibilities): Serializable {
    constructor() : this(uuid ="",name="",isVisible=Visibilities.INVISIBLE)

}