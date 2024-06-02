package at.aau.serg.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.ScoreBoardAdapter
import at.aau.serg.androidutils.GameUtils.convertSerializableToArray
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

        val scores: HashMap<String, Score>?
        val players: Array<LobbyPlayer>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scores = intent.getSerializableExtra("scores", HashMap::class.java) as HashMap<String, Score>
            players = intent.getSerializableExtra("players", Array<LobbyPlayer>::class.java) ?: emptyArray()
        } else {
            scores = intent.getSerializableExtra("scores") as HashMap<String, Score>
            players = convertSerializableToArray(intent.getSerializableExtra("players")) ?: emptyArray()
        }

        val sortedScoresMap = scores.toList().sortedByDescending { (_, value) -> value.score }.toMap()

        val scoreRecyclerView: RecyclerView = findViewById(R.id.recyclerViewScore)
        with(scoreRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreBoardAdapter(sortedScoresMap, players)
        }
    }

    fun btnHomeClicked(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}