package com.example.bang

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bang.modelos.Flechas
import com.example.bang.modelos.Jugador
import com.example.bang.modelos.Personaje
import com.example.bang.viewModel.JuegoViewModel
import kotlin.math.min


class JuegoActivity : AppCompatActivity() {
    private val viewModel: JuegoViewModel by viewModels()

    private lateinit var tvJugadorTurno: TextView
    private lateinit var tvFlechasInfo: TextView
    private lateinit var tvListaJugadores: TextView
    private lateinit var btnLanzarDados: Button
    private lateinit var btnFinTurno: Button
    private lateinit var layoutDados: LinearLayout
    val jugadorNulo = Jugador(
        nombre = "Omitir",
        saludTotal = 0,
        salud = 0,
        rol = "",
        flechas = 0,
        personaje = Personaje("", 0, ""),
        muerto = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Si es la primera vez, guarda la lista
        if (viewModel.getJugadores().isEmpty()) {
            val jugadoresIntent = intent.getSerializableExtra("jugadores") as? ArrayList<Jugador>
            if (jugadoresIntent != null) {
                viewModel.setJugadores(jugadoresIntent)
            }
        }

        tvJugadorTurno = findViewById(R.id.tvJugadorTurno)
        tvFlechasInfo = findViewById(R.id.tvFlechasInfo)
        tvListaJugadores = findViewById(R.id.tvListaJugadores)
        btnLanzarDados = findViewById(R.id.btnLanzarDados)
        btnFinTurno = findViewById(R.id.btnFinTurno)
        layoutDados = findViewById(R.id.layoutDados)

        actualizarUI()
        mostrarDados()

        btnLanzarDados.setOnClickListener {
             if (viewModel.puedeTirar()) {
                viewModel.tirarDados()
             }else{
                 btnLanzarDados.isEnabled = false
             }
            mostrarDados()
            viewModel.verificarMuerto(this)
            finalizarJuego()
            actualizarUI()
        }
        btnFinTurno.setOnClickListener {
            if (!viewModel.exploto()) {
                aplicarEfectosDeDados()
            }
            viewModel.verificarMuerto(this)
            finalizarJuego()
            viewModel.siguienteTurno()
            mostrarDados()
            btnLanzarDados.isEnabled = true

            val jugador = viewModel.getJugadorActual()
            if (jugador?.personaje?.nombre == "Sid Ketchum") {
                val opciones = viewModel.getJugadores().filter { it.salud < it.saludTotal && !it.muerto }
                if (opciones.isNotEmpty()) {
                    viewModel.mostrarDialogoSeleccion(this,"Sid Ketchum cura a alguien", opciones) { seleccionado ->
                        seleccionado.salud = min(seleccionado.salud + 1, seleccionado.saludTotal)
                    }
                }else {
                    Toast.makeText(this, "Nadie necesita curacion.", Toast.LENGTH_SHORT).show()
                }
            }
            actualizarUI()
        }
    }

    private fun actualizarUI() {
        val jugador = viewModel.getJugadorActual()
        tvJugadorTurno.text = "Turno de: ${jugador?.nombre}"
        tvFlechasInfo.text = "Flechas disponibles: ${Flechas.disponibles}"

        tvListaJugadores.text = viewModel.getJugadores().joinToString("\n") {
            if (!it.muerto){
                "${it.nombre}- \"${it.personaje.nombre}\" - Habilidad: ${it.personaje.habilidad} - ❤ ${it.salud}/${it.saludTotal} - ➳ ${it.flechas}"
            }else {
                "${it.nombre} - ☠\uFE0F Muerto"
            }
        }

        btnLanzarDados.isEnabled = viewModel.puedeTirar()
    }

    private fun mostrarDados() {
        val jugador = viewModel.getJugadorActual() ?: return
        layoutDados.removeAllViews()
        viewModel.dados.forEach { dado ->
            val btnDado = Button(this).apply {
                text = dado.valor
                setBackgroundColor(if (dado.seleccionado) 0xFFAAAAFF.toInt() else 0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(8, 0, 8, 0)
                }
                setPadding(0, 16, 0, 16)
                if (dado.valor != "Dinamita" || jugador.nombre == "Black Jack"){
                    isEnabled
                }
                setOnClickListener {
                    if (viewModel.tirosRealizados > 0 && dado.valor != "Dinamita") {
                        dado.seleccionado = !dado.seleccionado
                        mostrarDados()
                    }
                }
            }
            layoutDados.addView(btnDado)
        }
    }
    private fun aplicarEfectosDeDados() {
        val jugador = viewModel.getJugadorActual() ?: return
        val resultados = viewModel.dados.map { it.valor }
        val conteo = resultados.groupingBy { it }.eachCount()
        val jugadores = viewModel.getJugadores()
        val nombrePersonaje = jugador.personaje.nombre

        aplicarCervezas(jugador, conteo, jugadores,nombrePersonaje)
        aplicarTiros(jugador, conteo, nombrePersonaje)
        aplicarGatling(jugador, conteo, jugadores,nombrePersonaje)

        actualizarUI()
    }
    private fun aplicarTiros(jugador: Jugador, conteo: Map<String, Int>, nombrePersonaje: String) {
        var tiro1 = conteo["Tiro 1"] ?: 0
        var tiro2 = conteo["Tiro 2"] ?: 0
        var tiroEspecial = 0
        viewModel.banderagringa= true
        viewModel.disparo=false

        if (nombrePersonaje == "Calamity Janet") {
            tiroEspecial = tiro1 + tiro2
            tiro1 = 0
            tiro2 = 0
        }
        if (nombrePersonaje == "Slab “el Asesino”" && viewModel.tiroDobleUsado) {
            tiroEspecial++
            viewModel.tiroDobleUsado = false
        }

        repeat(tiro1) {
            aplicarTiro("Tiro 1", viewModel.jugadoresAdyacentes(1), jugador)
        }
        repeat(tiro2) {
            aplicarTiro("Tiro 2", viewModel.jugadoresAdyacentes(2), jugador)
        }
        repeat(tiroEspecial) {
            val objetivos = viewModel.jugadoresAdyacentes(1) + viewModel.jugadoresAdyacentes(2)
            aplicarTiro("Tiro Especial", objetivos, jugador)
        }
        if (nombrePersonaje == "Suzy Lafayette" && !viewModel.disparo) {
            jugador.salud = min(jugador.salud + 2, jugador.saludTotal)
            Toast.makeText(this, "Suzy Lafayette recupera 2 vidas por no disparar.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun aplicarTiro(titulo: String, objetivos: List<Jugador>, atacante: Jugador) {
        val opciones = objetivos + jugadorNulo
        viewModel.mostrarDialogoSeleccion(this,"Selecciona a quién disparar ($titulo)", opciones) { seleccionado ->
            if (seleccionado != jugadorNulo) {
                quitarVida(seleccionado,atacante)
                viewModel.verificarMuerto(this)
                actualizarUI()
            } else {
                Toast.makeText(this, "El tiro fue descartado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun aplicarCervezas(jugador: Jugador, conteo: Map<String, Int>, jugadores: List<Jugador>,nombrePersonaje: String) {
        val cervezas = conteo["Cerveza"] ?: 0
        val saludactual = jugador.salud
        repeat(cervezas) {
            if (nombrePersonaje == "Slab “el Asesino”" && !viewModel.tiroDobleUsado) {
                viewModel.mostrarDialogoOpcion(this,"¿Usar la cerveza para usar tiro especial?", "Sí", "No") { decision ->
                    if (decision) {
                            viewModel.tiroDobleUsado = true
                    } else {
                        darCerveza(jugador, jugadores, saludactual)
                    }
                }
            } else {
                darCerveza(jugador, jugadores, saludactual)
            }
        }
    }
    private fun darCerveza(jugador: Jugador, jugadores: List<Jugador>, saludActual: Int) {
        val opciones = jugadores.filter { it.salud < it.saludTotal && !it.muerto } + jugadorNulo
        if (opciones.size > 1) {
            viewModel.mostrarDialogoSeleccion(this,"¿A quién dar la cerveza?", opciones) { seleccionado ->
                if (seleccionado != jugadorNulo) {
                    viewModel.darvida(jugador, seleccionado, saludActual)
                    actualizarUI()
                } else {
                    Toast.makeText(this, "La cerveza fue descartada.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Nadie necesita cerveza. Se descarta.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun aplicarGatling(jugador: Jugador, conteo: Map<String, Int>, jugadores: List<Jugador>,nombrePersonaje: String) {
        val gatlings = conteo["Gatling"] ?: 0
        val umbral = if (nombrePersonaje == "Willy “el Niño”") 2 else 3

        if (gatlings >= umbral) {
            jugadores.filter { it != jugador && !it.muerto && it.personaje.nombre != "Paul Regret" }
                .forEach { quitarVida(jugador, it) }
            jugador.flechas = 0
            Flechas.reiniciarFlechas(jugador)
            Toast.makeText(this, "¡Gatling! Todos pierden 1 vida. Tú descartas tus flechas.", Toast.LENGTH_SHORT).show()
        }
        if (nombrePersonaje == "Kit Carlson") {
            repeat(gatlings) {
                val opciones = jugadores.filter { it.flechas > 0 }
                if (opciones.isNotEmpty()) {
                    viewModel.mostrarDialogoSeleccion(this,"Descarta una flecha con la habilidad de Kit Carlson", opciones) { seleccionado ->
                        if (seleccionado.flechas > 0) {
                            seleccionado.flechas--
                            Flechas.disponibles++
                            Toast.makeText(this, "Kit Carlson descartó una flecha de ${seleccionado.nombre}.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    fun quitarVida(jugador: Jugador,atacante: Jugador){
        if (jugador.personaje.nombre == "Bart Cassidy" && Flechas.disponibles > 0) {
            viewModel.mostrarDialogoOpcion(this,"¿Bart Cassidy quiere tomar una flecha en lugar de perder vida?", "Tomar una flecha", "Perder una vida") { decision ->
                    if (decision){
                        Flechas.tomarFlecha(jugador, viewModel.getJugadores())
                    }else{
                        jugador.salud--
                        aplicarEfectosSecundarios(jugador, atacante)
                    }
                    viewModel.disparo = true
                    actualizarUI()
                }
        } else {
            jugador.salud--
            aplicarEfectosSecundarios(jugador, atacante)
            viewModel.disparo = true
        }
    }
    private fun aplicarEfectosSecundarios(jugador: Jugador, atacante: Jugador) {
        if (jugador.personaje.nombre == "El Gringo" && viewModel.banderagringa) {
            Flechas.tomarFlecha(atacante, viewModel.getJugadores())
            viewModel.banderagringa = false
        }
        if (jugador.personaje.nombre == "Pedro Ramírez" && jugador.flechas > 0) {
            jugador.flechas--
        }
    }
    private fun finalizarJuego(){
        val resultado = viewModel.verificarFinDelJuego()
        if (resultado != null) {
        AlertDialog.Builder(this)
            .setTitle("Fin del juego")
            .setMessage(resultado)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
        }
    }
}