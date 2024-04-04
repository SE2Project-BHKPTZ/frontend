package at.aau.serg

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.network.SocketHandler
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val socket = SocketHandler("http://192.168.0.15:8080")
        socket.setupBasicListeners()
        socket.connect()
        socket.on("test:test") { payload: Any ->
            print(payload)
        }

        while(true) {
            socket.emit("test:test", "test1234")
            sleep(500)
        }
    }
}