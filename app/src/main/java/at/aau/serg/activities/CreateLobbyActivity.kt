package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.androidutils.ErrorUtils.getErrorMessageFromJSONResponse
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyCreate
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import okhttp3.Response
import java.io.IOException

class CreateLobbyActivity : AppCompatActivity() {


    lateinit var lobbyMaxPlayers : Slider
    lateinit var lobbyMaxRounds : Slider
    lateinit var lobbyMaxPlayersText : TextView
    lateinit var lobbyMaxRoundsText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lobbyMaxPlayers = findViewById<Slider>(R.id.sliderMaxPlayers)
        lobbyMaxRounds = findViewById<Slider>(R.id.sliderMaxRounds)

        lobbyMaxPlayersText = findViewById<TextView>(R.id.txtMaxPlayers)
        lobbyMaxRoundsText = findViewById<TextView>(R.id.txtMaxRounds)


        lobbyMaxPlayersText.text = this.getString(R.string.createLobbyMaxPlayerText, 3);
        lobbyMaxRoundsText.text = this.getString(R.string.createLobbyMaxRoundsText, 5);

        lobbyMaxPlayers.addOnChangeListener { _, value, _ ->

            lobbyMaxPlayersText.text = this.getString(R.string.createLobbyMaxPlayerText, value.toInt());

            when (value.toInt()) {
                3 -> lobbyMaxRounds.valueTo = 20F
                4 -> lobbyMaxRounds.valueTo = 15F
                5 -> lobbyMaxRounds.valueTo = 12F
                6 -> lobbyMaxRounds.valueTo = 10F
                else -> {
                    lobbyMaxRounds.valueTo = 1F
                }
            }
            if(lobbyMaxRounds.value.toInt() > lobbyMaxRounds.valueTo.toInt()){
                lobbyMaxRounds.value = lobbyMaxRounds.valueTo
            }

        }
        lobbyMaxRounds.addOnChangeListener { _, value, _ ->
            lobbyMaxRoundsText.text = this.getString(R.string.createLobbyMaxRoundsText, value.toInt());
        }
    }

    fun btnMainActivity(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnCreateLobby(view: View) {
        cLobby(null)
    }


    @Suppress("UNUSED_PARAMETER")
    private fun cLobby(response: Response?) {
        val lobbyName = findViewById<EditText>(R.id.inputName).text.toString()
        val lobbyIsPublic = findViewById<CheckBox>(R.id.checkBoxIsPublic).isChecked

        if (lobbyName.isEmpty()) {
            showToast(this, "Name cannot be empty")
            return
        }

        val lobbyToCreate = LobbyCreate(lobbyName, if (lobbyIsPublic) 1 else 0, lobbyMaxPlayers.value.toInt(),lobbyMaxRounds.value.toInt())
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
        showToast(this, message)
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
            val errorMessage = getErrorMessageFromJSONResponse(
                response,
                getString(R.string.loginFailed)
            )

            if (errorMessage == "Lobby with name already exists") {
                showToast(this, errorMessage)
                return
            }

            HttpClient.get(
                "/lobbys/leave",
                StoreToken(this).getAccessToken(),
                CallbackCreator().createCallback(::onFailureLobby, ::cLobby)
            )
        }
    }
}