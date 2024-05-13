package at.aau.serg.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.CardItem
import at.aau.serg.models.LobbyCreate
import at.aau.serg.models.LobbyJoin
import at.aau.serg.models.Suit
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import at.aau.serg.network.SocketHandler
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

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
                    ::startLoginActivity,
                    ::checkIfUserIsAuthenticated
                ), StoreToken(this)
            )
        ) {
            this.startActivity(Intent(this, LoginActivity::class.java))
        } else {
            Authentication.getCurrentUser(
                CallbackCreator().createCallback(
                    ::startLoginActivity,
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
                return
            }
        }
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
                    ::startLoginActivity,
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

    //START OF Temporary code to init lobby should be replaced with actual lobby creation see issue #9
    fun tmpBtnCLobbyClicked(view: View) {
        cLobby(null);
    }

    private fun cLobby(response: Response?) {
        val lobbyToCreate = LobbyCreate("testlobby", 1, 3)
        HttpClient.post(
            "/lobbys",
            Gson().toJson(lobbyToCreate),
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessCreateOrJoinLobby)
        )
    }

    private fun onFailureLobby() {
        this.runOnUiThread {
            Toast.makeText(this, "Lobby functionality failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun tmpBtnJLobbyClicked(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter LobbyID")
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT)
        builder.setView(input)
        builder.setPositiveButton("OK"
        ) { _, _ ->
            val lobbyToJoin = LobbyJoin(input.getText().toString())
            HttpClient.post(
                "/lobbys/join",
                Gson().toJson(lobbyToJoin),
                StoreToken(this).getAccessToken(),
                CallbackCreator().createCallback(::onFailureLobby, ::onSuccessCreateOrJoinLobby)
            )
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun onSuccessCreateOrJoinLobby(response: Response) {
        Log.d("Lobby", response.toString())

        if (response.isSuccessful) {
            response.body?.string()?.let {
                Log.d("Lobby", it)
                val intent = Intent(this, LobbyActivity::class.java)
                intent.putExtra("lobbyCode", it)
                startActivity(intent)
            }
        } else {
            HttpClient.get(
                "/lobbys/leave",
                StoreToken(this).getAccessToken(),
                CallbackCreator().createCallback(::onFailureLobby, ::cLobby)
            )
        }
    }
    //END OF Temporary code to init lobby should be replaced with actual lobby creation see issue #9
}