package at.aau.serg.logic


import android.content.ContextWrapper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.models.LobbyKick
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Response


class LobbyPlayerAdapter(context: ContextWrapper,listdata: Array<LobbyPlayer>) :
    RecyclerView.Adapter<LobbyPlayerAdapter.ViewHolder>() {
    private val listdata: Array<LobbyPlayer>
    private val context:ContextWrapper
    init {
        this.listdata = listdata
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.lobby_player, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData: LobbyPlayer = listdata[position]
        holder.txtPlayerName.text = myListData.name
        holder.btnKick.visibility = myListData.isVisible
        holder.btnKick.setOnClickListener { view ->

            kickPlayer(myListData)

            Toast.makeText(
                view.context,
                "click on item: " + myListData.uuid,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun kickPlayer(myListData: LobbyPlayer) {

        HttpClient.post(
            "lobbys/kick", Gson().toJson(LobbyKick(myListData.uuid)),
            StoreToken(context).getAccessToken(),
            CallbackCreator().createCallback(::onKickFailure, ::onSuccessGetLobby)
        )
    }

    private fun onSuccessGetLobby(response: Response) {

        val responseBody = response.body.toString()

        Log.d("Lobby",responseBody)
    }

    private fun onKickFailure() {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnKick: Button
        var txtPlayerName: TextView
        var constraintLayout: ConstraintLayout

        init {
            btnKick = itemView.findViewById<View>(R.id.btnKick) as Button
            txtPlayerName = itemView.findViewById<View>(R.id.txtPlayerName) as TextView
            constraintLayout = itemView.findViewById<View>(R.id.constraintLayout) as ConstraintLayout
        }
    }
}