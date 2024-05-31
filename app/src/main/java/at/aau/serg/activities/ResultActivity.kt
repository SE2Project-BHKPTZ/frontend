package at.aau.serg.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.adapters.ScoreBoardAdapter
import at.aau.serg.androidutils.GameUtils.convertSerializableToArray
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

        var scores: Array<Score>?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scores = intent.getSerializableExtra("scores", Array<Score>::class.java)
        } else {
            scores = convertSerializableToArray(intent.getSerializableExtra("scores"))
        }

        if (scores != null) {
            Log.d("Result", scores.size.toString())
        }

        val scoreRecyclerView: RecyclerView = findViewById(R.id.recyclerViewScore)
        with(scoreRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = scores?.let { ScoreBoardAdapter(it) }
        }
    }
}