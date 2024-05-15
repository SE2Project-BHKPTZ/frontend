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
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyJoin
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Response

class JoinLobbyActivity : AppCompatActivity() {

    private var lobbyToJoin = LobbyJoin("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_lobby)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun btnJoinCode(view: View) {
        jLobbyWithCode(null)
    }

    private fun jLobbyWithCode(response: Response?) {
        if(lobbyToJoin.lobbyID!="") {
            joinLobby(lobbyToJoin)
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter LobbyID")
        val input = EditText(this)

        input.setInputType(InputType.TYPE_CLASS_TEXT)
        builder.setView(input)
        builder.setPositiveButton("OK"
        ) { _, _ ->
            lobbyToJoin = LobbyJoin(input.getText().toString())
            joinLobby(lobbyToJoin)
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun joinLobby(lobbyToJoin: LobbyJoin){
        HttpClient.post(
            "/lobbys/join",
            Gson().toJson(lobbyToJoin),
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessJoinLobby)
        )
    }

    private fun onFailureLobby() {
        this.runOnUiThread {
            Toast.makeText(this, "Lobby functionality failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSuccessJoinLobby(response: Response) {
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
                CallbackCreator().createCallback(::onFailureLobby, ::jLobbyWithCode)
            )
        }
    }
}