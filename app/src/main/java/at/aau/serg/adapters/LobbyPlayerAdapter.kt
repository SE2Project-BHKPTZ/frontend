package at.aau.serg.adapters


import android.content.ContextWrapper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.LobbyKick
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Response
import java.io.IOException


class LobbyPlayerAdapter(private val context: ContextWrapper, private val listdata: Array<LobbyPlayer>) :
    RecyclerView.Adapter<LobbyPlayerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.lobby_player, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player: LobbyPlayer = listdata[position]
        holder.txtPlayerName.text = player.name
        holder.btnKick.visibility = player.isVisible.value
        holder.btnKick.setOnClickListener { view ->
            kickPlayer(player)
            showToast(view.context,"click on item: " + player.uuid)
        }
    }

    private fun kickPlayer(player: LobbyPlayer) {
        HttpClient.post(
            "lobbys/kick", Gson().toJson(LobbyKick(player.uuid)),
            StoreToken(context).getAccessToken(),
            CallbackCreator().createCallback(::onKickFailure, ::onSuccessKickPlayer)
        )
    }

    private fun onSuccessKickPlayer(response: Response) {
        val responseBody = response.body.toString()

        Log.d("LobbyPlayerAdapter",responseBody)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onKickFailure(e: IOException) {
        showToast(context, "Error kicking player")
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
            txtPlayerName = itemView.findViewById<View>(R.id.tvPlayerName) as TextView
            constraintLayout = itemView.findViewById<View>(R.id.constraintLayout) as ConstraintLayout
        }
    }
}