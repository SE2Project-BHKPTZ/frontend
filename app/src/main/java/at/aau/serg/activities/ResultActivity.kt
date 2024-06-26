package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.ScoreBoardAdapter
import at.aau.serg.androidutils.GameUtils.serializable
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Score

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            }
        })

        val scores: HashMap<String, Score>? = intent.serializable("scores")
        val players: Array<LobbyPlayer> = intent.serializable("players") ?: emptyArray()


        val sortedScoresMap = scores?.toList()?.sortedByDescending { (_, value) -> value.score }?.toMap()

        val scoreRecyclerView: RecyclerView = findViewById(R.id.recyclerViewScore)
        with(scoreRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = sortedScoresMap?.let { ScoreBoardAdapter(it, players) }
        }
    }

    fun btnHomeClicked(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}