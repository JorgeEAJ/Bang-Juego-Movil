package com.example.bang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bang.modelos.Jugador
import com.example.bang.viewModel.JuegoViewModel

@Suppress("DEPRECATION")
class VerJugador : AppCompatActivity() {

    private lateinit var layoutInfo: LinearLayout
    private lateinit var btnMostrar: Button
    private lateinit var btnSiguiente: Button

    private val viewModel: JuegoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_jugador)

        // Obtener vista
        layoutInfo = findViewById(R.id.layoutInfo)
        btnMostrar = findViewById(R.id.btnMostrarCarta)
        btnSiguiente = findViewById(R.id.btnSiguiente)

        // Recuperar jugadores si aún no están en el ViewModel
        if (viewModel.getJugadores().isEmpty()) {
            @Suppress("UNCHECKED_CAST")
            val jugadoresIntent = intent.getSerializableExtra("jugadores") as? ArrayList<*>
            val jugadores = jugadoresIntent?.filterIsInstance<Jugador>() as? ArrayList<Jugador>
            if (!jugadores.isNullOrEmpty()) {
                viewModel.setJugadores(jugadores)
            } else {
                println("No se recibieron jugadores en el intent")
            }
        }

        btnMostrar.setOnClickListener {
            mostrarJugador()
            layoutInfo.visibility = View.VISIBLE
            btnMostrar.visibility = View.GONE
            btnSiguiente.visibility = View.VISIBLE
        }

        btnSiguiente.setOnClickListener {
            val hayMas = viewModel.avanzarJugador()
            if (hayMas) {
                layoutInfo.visibility = View.GONE
                btnMostrar.visibility = View.VISIBLE
                btnSiguiente.visibility = View.GONE
            } else {
                val intent = Intent(this, JuegoActivity::class.java)
                intent.putExtra("jugadores", ArrayList(viewModel.getJugadores()))
                startActivity(intent)
                finish()
            }
        }
    }

    private fun mostrarJugador() {
        val jugador = viewModel.getJugadorActual()
        var tvRolCarta = findViewById<ImageView>(R.id.tvRolCarta)
        jugador?.let {
            findViewById<TextView>(R.id.tvNombre).text = getString(R.string.jugador_nombre, it.nombre)
            findViewById<TextView>(R.id.tvRol).text = getString(R.string.jugador_rol, it.rol)
            when (jugador.rol) {
                "Sheriff" -> tvRolCarta.setImageResource(R.drawable.sheriff)
                "Renegado" -> tvRolCarta.setImageResource(R.drawable.renegade)
                "Forajido" -> tvRolCarta.setImageResource(R.drawable.outlaw)
                "Alguacil" -> tvRolCarta.setImageResource(R.drawable.deputy)
            }
            findViewById<TextView>(R.id.tvPersonaje).text = getString(R.string.jugador_personaje, it.personaje.nombre)
            findViewById<TextView>(R.id.tvSalud).text = getString(R.string.jugador_salud, it.salud)
            findViewById<TextView>(R.id.tvHabilidad).text = it.personaje.habilidad
        }
    }
}