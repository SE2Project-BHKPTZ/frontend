package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.LobbyPlayerAdapter
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Visibilities
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import at.aau.serg.network.SocketHandler
import okhttp3.Response
import org.json.JSONObject


class LobbyActivity : AppCompatActivity() {

    val lobbyPlayers: Array<LobbyPlayer> = Array(6) {
        LobbyPlayer()
    }

    private lateinit var adapter: LobbyPlayerAdapter
    private var isAdmin:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<View>(R.id.rcylPlayers) as RecyclerView
        adapter = LobbyPlayerAdapter(this,lobbyPlayers)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)

        val lobbyID = intent.getStringExtra("lobbyCode")
        val txtLobbyCode = findViewById<TextView>(R.id.txtLobbyCode)
        txtLobbyCode.text = getString(R.string.LobbyCode, lobbyID)

        HttpClient.get(
            "lobbys/my",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailure, ::onSuccessGetLobby)
        )

        SocketHandler.on("lobby:userJoined", ::userJoined)
        SocketHandler.on("lobby:userLeft", ::userLeft)
        SocketHandler.on("lobby:userKick", ::userKick)
    }

       private fun onSuccessGetLobby(response: Response) {
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                Log.d("Lobby", responseBody)
                val players = Array(JSONObject(responseBody).getJSONArray("players").length()) {
                    JSONObject(responseBody).getJSONArray("players").getString(it)
                }
                for ((index, player) in players.withIndex()) {
                    Log.d("Lobby", player.toString())
                    lobbyPlayers[index].uuid = JSONObject(player).getString("uuid")
                    lobbyPlayers[index].name = JSONObject(player).getString("username")
                    adapter.notifyItemChanged(index)
                }
                val adminUUID: String = JSONObject(players[0]).getString("uuid")
                val uuid: String = StoreToken(this).getUUID().toString()
                if(adminUUID == uuid){
                    isAdmin = true
                }

                Log.d("Lobby", adminUUID)
                Log.d("Lobby", uuid)
            }
        }
    }

    private fun onFailure() {
        this.startActivity(Intent(this, MainActivity::class.java))
    }

    private fun userJoined(anies: Array<Any>) {
        val playerData = (anies[0] as JSONObject)
        Log.d("Lobby", "$playerData joined")

        for ((index, player) in lobbyPlayers.withIndex()) {
            if (player.uuid == "") {
                player.uuid = playerData.getString("playerUUID")
                player.name = playerData.getString("playerName")
                if(isAdmin){
                    player.isVisible = Visibilities.VISIBLE
                }
                adapter.notifyItemChanged(index)
                break
            }
        }
    }

    private fun userLeft(anies: Array<Any>) {
        val playerData = (anies[0] as JSONObject).getString("playerUUID")
        Log.d("Lobby", "$playerData left")
        val plyerToRemove = lobbyPlayers.indexOfFirst { it.uuid == playerData }
        lobbyPlayers[plyerToRemove].uuid = ""
        lobbyPlayers[plyerToRemove].name = ""
        lobbyPlayers[plyerToRemove].isVisible = Visibilities.INVISIBLE
        adapter.notifyItemChanged(plyerToRemove)
    }

    private fun userKick(anies: Array<Any>) {
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "You are kicked from the lobby", Toast.LENGTH_SHORT).show()
        })
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnLeaveClicked(view: View) {
        HttpClient.get(
            "/lobbys/leave",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLeaveLobby, ::leftLobby)
        )
    }

    private fun leftLobby(response: Response) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun onFailureLeaveLobby() {
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "Error leaving lobby", Toast.LENGTH_SHORT).show()
        })
    }
}