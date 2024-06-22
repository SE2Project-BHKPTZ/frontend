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
import at.aau.serg.androidutils.GameUtils.parseGameDataJson
import at.aau.serg.androidutils.GameUtils.parseLobbyJson
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.SocketHandler
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

    override fun onStop() {
        super.onStop()
        SocketHandler.off("recovery")
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

    @Suppress("UNUSED_PARAMETER")
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

    fun btnCLobbyClicked(view: View) {
        startActivity(Intent(this, CreateLobbyActivity::class.java))
    }

    fun btnJLobbyClicked(view: View) {
        startActivity(Intent(this, JoinLobbyActivity::class.java))
    }

    private fun recoverGameState(socketResponse: Array<Any>) {
        Log.d("Recovery", "Recovery called")
        val data = (socketResponse[0] as JSONObject)
        val status = data.getString("status")

        if (status == "JOIN_LOBBY") {
            val lobby = parseLobbyJson(data.getJSONObject("state"))

            val intent = Intent(this, LobbyActivity::class.java)
            intent.putExtra("lobbyCode", lobby.uuid)
            startActivity(intent)
        } else if (status == "PLAYING") {
            val gameData = parseGameDataJson(data.getJSONObject("state"))
            Log.d("gamedata", gameData.toString())

            val intent = Intent(baseContext, GameScreenActivity::class.java).apply {
                putExtra("gameData", gameData)
            }

            startActivity(intent)
        }
    }
}