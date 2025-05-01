package com.example.bang

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.bang.modelos.Jugador
import com.example.bang.modelos.personajesDisponibles
import android.view.MotionEvent
import android.view.animation.AnimationUtils


class LocalGameSetupActivity : AppCompatActivity() {

    private lateinit var spinnerPlayers: Spinner
    private lateinit var playerInputsContainer: LinearLayout
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_game_setup)

        spinnerPlayers = findViewById(R.id.spinnerPlayers)
        playerInputsContainer = findViewById(R.id.playerInputsContainer)
        btnStart = findViewById(R.id.btnStartLocalGame)

        // Crear opciones del spinner (3 a 8 jugadores)
        val playerCounts = (3..8).toList()
        val adapter = ArrayAdapter(this, R.layout.spinner_item, playerCounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlayers.adapter = adapter

        spinnerPlayers.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
                spinnerPlayers.startAnimation(animation)
            }
            false
        }

        spinnerPlayers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val count = playerCounts[position]
                generarCamposDeJugadores(count)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnStart.setOnClickListener {
            val nombres = mutableListOf<String>()
            for (i in 0 until playerInputsContainer.childCount) {
                val editText = playerInputsContainer.getChildAt(i) as EditText
                nombres.add(editText.text.toString().ifBlank { "Jugador ${i + 1}" })
            }

            val jugadores = asignarRolesYPersonajes(nombres)
            for (j in jugadores) {
                println("${j.nombre}: ${j.rol}, ${j.salud} vidas")
            }
            val intent = Intent(this, VerJugador::class.java)
            intent.putExtra("jugadores", ArrayList(jugadores))
            startActivity(intent)
        }

    }
    private fun asignarRolesYPersonajes(nombres: List<String>): List<Jugador> {
        val total = nombres.size
        val roles = mutableListOf<String>()

        roles.add("Sheriff")
        roles.add("Renegado")
        roles.add("Forajido")

        if (total >= 4) roles.add("Forajido")
        if (total >= 5) roles.add("Alguacil")
        if (total >= 6) roles.add("Forajido")
        if (total >= 7) roles.add("Alguacil")
        if (total >= 8) roles.add("Renegado")

        roles.shuffle()

        val personajesRestantes = personajesDisponibles.shuffled().take(total)

        val jugadores = mutableListOf<Jugador>()
        for (i in nombres.indices) {
            val rol = roles[i]
            val personaje = personajesRestantes[i]
            val saludInicial = if (rol == "Sheriff") personaje.vidaBase + 2 else personaje.vidaBase

            jugadores.add(
                Jugador(
                    nombre = nombres[i],
                    rol = rol,
                    salud = saludInicial,
                    saludTotal = saludInicial,
                    flechas = 0,
                    personaje = personaje,
                    muerto = false
                )
            )
        }

        return jugadores
    }

    private fun generarCamposDeJugadores(cantidad: Int) {
        playerInputsContainer.removeAllViews()
        for (i in 1..cantidad) {
            val editText = EditText(this)
            editText.hint = "Nombre del jugador $i"
            playerInputsContainer.addView(editText)
        }
    }
}