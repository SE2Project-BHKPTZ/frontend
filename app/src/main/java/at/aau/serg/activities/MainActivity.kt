package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.CardItem
import at.aau.serg.models.CardItemDeserializer
import at.aau.serg.models.GameRecovery
import at.aau.serg.models.Lobby
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Player
import at.aau.serg.models.Score
import at.aau.serg.models.Suit
import at.aau.serg.models.Visibilities
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.SocketHandler
import at.aau.serg.utils.CardsConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!Authentication.getCurrentUser(
                CallbackCreator().createCallback(
                    ::startLoginOnFailure,
                    ::checkIfUserIsAuthenticated
                ), StoreToken(this)
            )
        ) {
            this.startActivity(Intent(this, LoginActivity::class.java))
        } else {
            Authentication.getCurrentUser(
                CallbackCreator().createCallback(
                    ::startLoginOnFailure,
                    ::connectSocket
                ), StoreToken(this)
            )
        }
    }

    private fun connectSocket(response: Response) {
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                StoreToken(this).storeUUID(JSONObject(responseBody).getString("uuid"))
                SocketHandler.connect(JSONObject(responseBody).getString("uuid"))
                SocketHandler.on("recovery", ::recoverGameState)
                return
            }
        }
        startLoginActivity()
    }

    private fun startLoginOnFailure(e: IOException) {
        startLoginActivity()
    }

    private fun startLoginActivity() {
        this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    private fun checkIfUserIsAuthenticated(response: Response) {
        if (response.isSuccessful)
            return

        if (response.code != 403) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }

        if (!Authentication.updateToken(
                CallbackCreator().createCallback(
                    ::startLoginOnFailure,
                    ::checkIfUpdateAccessTokenWorked
                ), StoreToken(this@MainActivity)
            )
        ) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    private fun checkIfUpdateAccessTokenWorked(response: Response) {
        if (!response.isSuccessful) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }
        val responseBody = response.body?.string()
        if (responseBody == null) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }

        try {
            StoreToken(this@MainActivity).storeAccessTokenFromBody(JSONObject(responseBody))
        } catch (e: JSONException) {
            e.printStackTrace()
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }
    }

    fun btnGoToLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun openGameActivity(view: View){
        val intent = Intent(
            baseContext,
            GameScreenActivity::class.java
        )

        val sampleCards: Array<CardItem> = arrayOf(
            CardItem("2", Suit.HEARTS),
            CardItem("5", Suit.SPADES),
            CardItem("7", Suit.DIAMONDS),
            CardItem("13", Suit.CLUBS),
            CardItem("4", Suit.HEARTS)
        )
        intent.putExtra("cards", sampleCards)
        intent.putExtra("trump", CardItem("3", Suit.HEARTS))
        intent.putExtra("playerCount", 4)
        intent.putExtra("me", 0)

        startActivity(intent)
    }

    fun btnCLobbyClicked(view: View) {
        startActivity(Intent(this, CreateLobbyActivity::class.java))
    }

    fun btnJLobbyClicked(view: View) {
        startActivity(Intent(this, JoinLobbyActivity::class.java))
    }

    fun openResultActivity(view: View) {
        val scores = hashMapOf(
            "1" to Score("60", 1),
            "2" to Score("100", 2),
            "3" to Score("20", 3)
        )

        val players = arrayOf(LobbyPlayer("1", "Player 1", Visibilities.VISIBLE), LobbyPlayer("2", "Player 2", Visibilities.VISIBLE), LobbyPlayer("3", "Player 3", Visibilities.VISIBLE))

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("scores", scores)
            putExtra("players", players)
        }
        startActivity(intent)
    }

    private fun recoverGameState(socketResponse: Array<Any>) {
        Log.d("Recovery", "Recovery called")
        val data = (socketResponse[0] as JSONObject)
        val status = data.getString("status")

        SocketHandler.off("recovery")

        if (status == "JOIN_LOBBY") {
            val lobby = parseLobbyJson(data.getJSONObject("state"))

            val intent = Intent(this, LobbyActivity::class.java)
            intent.putExtra("lobbyCode", lobby.uuid)
            startActivity(intent)
        } else if (status == "PLAYING") {
            Log.d("recovery", data.toString())

            val gameData = parseGameDataJson(data.getJSONObject("state"))
            Log.d("gamedata", gameData.toString())

            val intent = Intent(baseContext, GameScreenActivity::class.java).apply {
                putExtra("gameData", gameData)
            }

            startActivity(intent)
        }
    }

    fun parseLobbyJson(jsonObject: JSONObject): Lobby {
        val gson = Gson()
        return gson.fromJson(jsonObject.toString(), Lobby::class.java)
    }

    fun parseGameDataJson(jsonObject: JSONObject): GameRecovery {
        val gson = GsonBuilder()
            .registerTypeAdapter(CardItem::class.java, CardItemDeserializer())
            .create()

        return gson.fromJson(jsonObject.toString(), GameRecovery::class.java)
    }
}