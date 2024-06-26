package at.aau.serg.adapters


import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.activities.LobbyActivity
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.JoinLobbyLobby
import at.aau.serg.models.LobbyJoin
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Response
import java.io.IOException


class JoinLobbyLobbiesAdapter(
    private val context: ContextWrapper,
    private val listdata: MutableList<JoinLobbyLobby>
) :
    RecyclerView.Adapter<JoinLobbyLobbiesAdapter.ViewHolder>() {

    private var lobbyID = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.joinlobby_lobby, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lobby: JoinLobbyLobby = listdata[position]
        holder.txtLobbyName.text = lobby.name
        holder.txtPlayerCount.text = context.getString(R.string.joinlobbyLobbyPlayerCountPlaceholder, lobby.currentPlayers,lobby.maxPlayers);
        holder.btnJoin.setOnClickListener {
            lobbyID = lobby.lobbyID
            joinLobby(null)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun joinLobby(response: Response?) {
        HttpClient.post(
            "/lobbys/join",
            Gson().toJson(LobbyJoin(lobbyID)),
            StoreToken(context).getAccessToken(),
            CallbackCreator().createCallback(::onFailureLobby, ::onSuccessJoinLobby)
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFailureLobby(e: IOException) {
            showToast(context, "Lobby functionality failed")
    }

    private fun onSuccessJoinLobby(response: Response) {
        Log.d("Lobby", response.toString())

        if (response.isSuccessful) {
            response.body?.string()?.let {
                Log.d("Lobby", it)
                val intent = Intent(context, LobbyActivity::class.java)
                intent.putExtra("lobbyCode", it)
                context.startActivity(intent)
            }
        } else {
            HttpClient.get(
                "/lobbys/leave",
                StoreToken(context).getAccessToken(),
                CallbackCreator().createCallback(::onFailureLobby, ::joinLobby)
            )
        }
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnJoin: Button = itemView.findViewById<View>(R.id.btnJoin) as Button
        var txtLobbyName: TextView = itemView.findViewById<View>(R.id.tvLobbyName) as TextView
        var txtPlayerCount: TextView = itemView.findViewById<View>(R.id.tvPlayerCount) as TextView
    }
}