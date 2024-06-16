package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.LobbyPlayerAdapter
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Visibilities
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import at.aau.serg.network.SocketHandler
import at.aau.serg.utils.CardsConverter
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Arrays

class LobbyActivity : AppCompatActivity() {

    private val lobbyPlayers: Array<LobbyPlayer> = Array(6) {
        LobbyPlayer()
    }
    private lateinit var adapter: LobbyPlayerAdapter
    private var isAdmin: Boolean = false
    private var maxRounds : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<View>(R.id.recyclerViewPlayers) as RecyclerView
        adapter = LobbyPlayerAdapter(this, lobbyPlayers)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)

        val lobbyID = intent.getStringExtra("lobbyCode")
        val txtLobbyCode = findViewById<TextView>(R.id.tvLobbyCode)
        txtLobbyCode.text = getString(R.string.lobbyCode, lobbyID)

        HttpClient.get(
            "lobbys/my",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailure, ::onSuccessGetLobby)
        )

        SocketHandler.on("lobby:userJoined", ::userJoined)
        SocketHandler.on("lobby:userLeft", ::userLeft)
        SocketHandler.on("lobby:userKick", ::userKick)
        SocketHandler.on("startGame", ::startGame)
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.off("lobby:userJoined")
        SocketHandler.off("lobby:userLeft")
        SocketHandler.off("lobby:userKick")
        SocketHandler.off("startGame")
    }

    private fun onSuccessGetLobby(response: Response) {
        if (response.isSuccessful) {
            response.body?.string()?.let { responseBody ->
                maxRounds = JSONObject(responseBody).getInt("maxRounds")

                val players = JSONArray(JSONObject(responseBody).getString("players"))
                for (i in 0 until players.length()) {
                    val player = players.getJSONObject(i)
                    lobbyPlayers[i].apply {
                        uuid = player.getString("uuid")
                        name = player.getString("username")
                    }
                    runOnUiThread { adapter.notifyItemChanged(i) }
                }
                isAdmin = players.getJSONObject(0).getString("uuid") == StoreToken(this).getUUID().toString()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFailure(e: IOException) {
        this.startActivity(Intent(this, MainActivity::class.java))
    }

    private fun userJoined(socketResponse: Array<Any>) {
        val playerData = (socketResponse[0] as JSONObject)
        Log.d("Lobby", "$playerData joined")

        for ((index, player) in lobbyPlayers.withIndex()) {
            if (player.uuid.isEmpty()) {
                player.apply {
                    uuid = playerData.getString("playerUUID")
                    name = playerData.getString("playerName")
                    if (isAdmin) isVisible = Visibilities.VISIBLE
                }
                runOnUiThread { adapter.notifyItemChanged(index) }
                break
            }
        }

        if (playerCount() >= 3 && isAdmin) {
            runOnUiThread { findViewById<Button>(R.id.btnStartGame).isEnabled = true }
        }
    }

    private fun userLeft(socketResponse: Array<Any>) {
        val playerData = (socketResponse[0] as JSONObject).getString("playerUUID")
        Log.d("Lobby", "$playerData left")
        val playerToRemove = lobbyPlayers.indexOfFirst { it.uuid == playerData }
        if (playerToRemove != -1) {
            lobbyPlayers[playerToRemove].apply {
                uuid = ""
                name = ""
                isVisible = Visibilities.INVISIBLE
            }
            runOnUiThread { adapter.notifyItemChanged(playerToRemove) }
        }

        if (playerCount() < 3 && isAdmin) {
            runOnUiThread { findViewById<Button>(R.id.btnStartGame).isEnabled = false }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun userKick(socketResponse: Array<Any>) {
        showToast(this, "You are kicked from the lobby")
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnLeaveClicked(view: View) {
        HttpClient.get(
            "/lobbys/leave",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLeaveLobby, ::leftLobby)
        )
    }

    fun btnStartGame(view: View) {
        SocketHandler.emit("startGame", "")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun leftLobby(response: Response) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFailureLeaveLobby(e: IOException) {
        showToast(this, "Error leaving lobby")
    }

    private fun startGame(socketResponse: Array<Any>) {
        val gameData = (socketResponse[0] as JSONObject)
        val cards = gameData.getJSONArray("hands").getJSONArray(getPlayerIndex())
        val trumpCard = gameData.optJSONObject("trump")

        val intent = Intent(baseContext, GameScreenActivity::class.java).apply {
            putExtra("cards", CardsConverter.convertCards(cards))
            putExtra("trump", trumpCard?.let { CardsConverter.convertCard(it) })
            putExtra("playerCount", gameData.getJSONArray("hands").length())
            putExtra("maxRounds",maxRounds)
            putExtra("me", getPlayerIndex())
            putExtra("players", lobbyPlayers)
        }

        startActivity(intent)
    }

    private fun playerCount(): Int {
        return Arrays.stream<Any>(lobbyPlayers).filter { e: Any? -> e != null }.count()
            .toInt();
    }

    private fun getPlayerIndex(): Int {
        val uuid: String = StoreToken(this).getUUID().toString()
        return lobbyPlayers.indexOfFirst { player -> player.uuid == uuid}
    }
}