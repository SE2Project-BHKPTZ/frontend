package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import okhttp3.Response

class LobbyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lobbyID = intent.getStringExtra("lobbyData")

        val txtLobbyCode = findViewById<TextView>(R.id.txtLobbyCode)

        txtLobbyCode.text = getString(R.string.LobbyCode,lobbyID)

        Log.d("Lobby", lobbyID.toString())
    }

    fun btnLeaveClicked(view: View) {
        HttpClient.get("/lobbys/leave",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureCLobby, ::leftLobby))
    }

    private fun leftLobby(response: Response) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun onFailureCLobby() {
        TODO("Not yet implemented")
    }
}