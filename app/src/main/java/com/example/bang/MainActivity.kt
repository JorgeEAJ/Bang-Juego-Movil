package com.example.bang

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Explode
import androidx.transition.TransitionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val btnLocal : Button = findViewById(R.id.btnLocal)
            val btnCreate : Button = findViewById(R.id.btnCreateGame)
            val btnJoin : Button = findViewById(R.id.btnJoinGame)

            btnLocal.setOnClickListener {
                val intent = Intent(this, LocalGameSetupActivity::class.java)
                startActivity(intent)
                TransitionHelper.applyTransition(this, R.anim.slide_down, R.anim.fade_out)
            }
            btnCreate.setOnClickListener {
                // Aquí irá la lógica para hostear una partida
            }

            btnJoin.setOnClickListener {
                // Aquí irá la lógica para unirse a una partida
            }
            insets
        }
    }
}