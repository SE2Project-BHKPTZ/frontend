package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyCreate
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import okhttp3.Response
import java.io.IOException

class CreateLobbyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun btnMainActivity(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnCreateLobby(view: View) {
        cLobby(null)
    }

    private fun cLobby(response: Response?) {
        val lobbyName = findViewById<EditText>(R.id.inputName).text.toString()
        val lobbyIsPublic = findViewById<CheckBox>(R.id.checkBoxIsPublic).isChecked
        val lobbyMaxPlayers = findViewById<Slider>(R.id.sliderMaxPlayers).value.toInt()

        if (lobbyName.isEmpty()) {
            showToast(this, "Name cannot be empty")
            return
        }

        val lobbyToCreate = LobbyCreate(lobbyName, if (lobbyIsPublic) 1 else 0, lobbyMaxPlayers)
        val jsonLobby = Gson().toJson(lobbyToCreate)

        HttpClient.post(
            "/lobbys",
            jsonLobby,
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessCreateLobby)
        )
    }

    private fun onFailureLobby(e: IOException) {
        val message = when (e) {
            is java.net.ConnectException -> "Could not connect to the server"
            else -> "Lobby functionality failed"
        }
        runOnUiThread {
            showToast(this, message)
        }
    }

    private fun onSuccessCreateLobby(response: Response) {
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
}