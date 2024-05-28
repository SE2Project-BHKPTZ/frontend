package at.aau.serg.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.JoinLobbyLobbiesAdapter
import at.aau.serg.androidutils.ErrorUtils
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.JoinLobbyLobby
import at.aau.serg.models.LobbyJoin
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class JoinLobbyActivity : AppCompatActivity() {

    private val lobbies: MutableList<JoinLobbyLobby> = mutableListOf()
    private lateinit var adapter: JoinLobbyLobbiesAdapter
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

        val recyclerView = findViewById<View>(R.id.recyclerViewLobbies) as RecyclerView
        adapter = JoinLobbyLobbiesAdapter(this, lobbies)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)

        HttpClient.get(
            "lobbys",
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessGetLobbies)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSuccessGetLobbies(response: Response) {
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                val openLobbies = Array(JSONArray(responseBody).length()) {
                    JSONArray(responseBody)[it]
                }
                for ((index, lobby) in openLobbies.withIndex()) {
                    Log.d("LO", lobby.toString())
                    val jsonLobby = JSONObject(lobby.toString())
                    if(jsonLobby.getJSONArray("players").length() == jsonLobby.getInt("maxPlayers") ) continue
                    lobbies.add(
                        index, JoinLobbyLobby(
                            jsonLobby.getString("name"),
                            jsonLobby.getJSONArray("players").length(),
                            jsonLobby.getInt("maxPlayers"),
                            jsonLobby.getString("lobbyid")
                        )
                    )
                }
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }

            Log.d("LO", lobbies.toString())
        }
    }


    fun btnJoinCode(view: View) {
        createJoinLobbyPrompt(null)
    }

    private fun createJoinLobbyPrompt(response: Response?) {
        if (lobbyToJoin.lobbyID != "") {
            joinLobby(lobbyToJoin)
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter LobbyID")
        val input = EditText(this)

        input.setInputType(InputType.TYPE_CLASS_TEXT)
        builder.setView(input)
        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            lobbyToJoin = LobbyJoin(input.getText().toString())
            joinLobby(lobbyToJoin)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun joinLobby(lobbyToJoin: LobbyJoin) {
        HttpClient.post(
            "/lobbys/join",
            Gson().toJson(lobbyToJoin),
            StoreToken(this).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessJoinLobby)
        )
    }

    private fun onFailureLobby(e: IOException) {
        showToast(this, "Lobby functionality failed")
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
            handleJoinLobbyError(response)
        }
    }

    private fun handleJoinLobbyError(response: Response) {
        val errorMessage = ErrorUtils.getErrorMessageFromJSONResponse(response, getString(R.string.loginFailed))
        if (errorMessage == "Lobby not found" || errorMessage == "Lobby is full") {
            showToast(this, errorMessage)
            lobbyToJoin = LobbyJoin("")
        } else {
            HttpClient.get(
                "/lobbys/leave",
                StoreToken(this).getAccessToken(),
                CallbackCreator().createCallback(::onFailureLobby, ::createJoinLobbyPrompt)
            )
        }
    }

    fun btnBack(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}