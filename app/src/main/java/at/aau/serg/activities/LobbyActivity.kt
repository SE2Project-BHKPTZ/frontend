package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import at.aau.serg.models.CardItem
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Suit
import at.aau.serg.models.Visibilities
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import at.aau.serg.network.SocketHandler
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.Arrays

class LobbyActivity : AppCompatActivity() {

    private val lobbyPlayers: Array<LobbyPlayer> = Array(6) {
        LobbyPlayer()
    }
    private lateinit var adapter: LobbyPlayerAdapter
    private var isAdmin: Boolean = false

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

    private fun onSuccessGetLobby(response: Response) {
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                val players = Array(JSONObject(responseBody).getJSONArray("players").length()) {
                    JSONObject(responseBody).getJSONArray("players").getString(it)
                }
                for ((index, player) in players.withIndex()) {
                    lobbyPlayers[index].uuid = JSONObject(player).getString("uuid")
                    lobbyPlayers[index].name = JSONObject(player).getString("username")
                    runOnUiThread { // fixes a bug that can occur
                        adapter.notifyItemChanged(index)
                    }
                }
                val adminUUID: String = JSONObject(players[0]).getString("uuid")
                val uuid: String = StoreToken(this).getUUID().toString()
                if (adminUUID == uuid) {
                    isAdmin = true
                }
            }
        }
    }

    private fun onFailure() {
        this.startActivity(Intent(this, MainActivity::class.java))
    }

    private fun userJoined(socketResponse: Array<Any>) {
        val playerData = (socketResponse[0] as JSONObject)
        Log.d("Lobby", "$playerData joined")

        for ((index, player) in lobbyPlayers.withIndex()) {
            if (player.uuid == "") {
                player.uuid = playerData.getString("playerUUID")
                player.name = playerData.getString("playerName")
                if (isAdmin) {
                    player.isVisible = Visibilities.VISIBLE
                }
                adapter.notifyItemChanged(index)
                break
            }
        }

        if (playerCount() >= 3 && isAdmin) {
            this.runOnUiThread {
                val startBtn: Button = findViewById(R.id.btnStartGame)
                startBtn.isEnabled = true
            }
        }
    }

    private fun userLeft(socketResponse: Array<Any>) {
        val playerData = (socketResponse[0] as JSONObject).getString("playerUUID")
        Log.d("Lobby", "$playerData left")
        val playerToRemove = lobbyPlayers.indexOfFirst { it.uuid == playerData }
        lobbyPlayers[playerToRemove].uuid = ""
        lobbyPlayers[playerToRemove].name = ""
        lobbyPlayers[playerToRemove].isVisible = Visibilities.INVISIBLE
        adapter.notifyItemChanged(playerToRemove)

        if (playerCount() < 3 && isAdmin) {
            this.runOnUiThread {
                val startBtn: Button = findViewById(R.id.btnStartGame)
                startBtn.isEnabled = false
            }
        }
    }

    private fun userKick(socketResponse: Array<Any>) {
        this.runOnUiThread {
            Toast.makeText(this, "You are kicked from the lobby", Toast.LENGTH_SHORT).show()
        }
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

    private fun leftLobby(response: Response) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun onFailureLeaveLobby() {
        this.runOnUiThread {
            Toast.makeText(this, "Error leaving lobby", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGame(socketResponse: Array<Any>) {
        val gameData = (socketResponse[0] as JSONObject)
        val cards = gameData.getJSONArray("hands").getJSONArray(getPlayerIndex())
        val trumpCard = gameData.getJSONObject("trump")

        val intent = Intent(
            baseContext,
            GameScreenActivity::class.java
        )
        intent.putExtra("cards", convertCards(cards))
        intent.putExtra("trump", convertCard(trumpCard))
        intent.putExtra("playerCount", gameData.getJSONArray("hands").length())


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

    private fun convertCards(cardsJsonArray: JSONArray): Array<CardItem> {
        val cardsList = mutableListOf<CardItem>()
        for (i in 0 until cardsJsonArray.length()) {
            val cardJson = cardsJsonArray.getJSONObject(i)
            val card = convertCard(cardJson)
            cardsList.add(card)
        }

        return cardsList.toTypedArray()
    }

    private fun convertCard(cardJson: JSONObject): CardItem {
        return CardItem(
            cardJson.getString("value"),
            stringToSuit(cardJson.getString("suit"))
        )
    }

    private fun stringToSuit(suitString: String): Suit {
        return when (suitString.uppercase()) {
            "HEARTS" -> Suit.HEARTS
            "DIAMONDS" -> Suit.DIAMONDS
            "CLUBS" -> Suit.CLUBS
            "SPADES" -> Suit.SPADES
            else -> throw IllegalArgumentException("Unknown suit: $suitString")
        }
    }
}