package com.example.bang.viewModel

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.bang.modelos.Dado
import com.example.bang.modelos.Flechas
import com.example.bang.modelos.Jugador
import kotlin.math.min

class JuegoViewModel : ViewModel() {

    private var jugadores: List<Jugador> = emptyList()
    private var indexActual: Int = 0

    var tirosRealizados = 0
    val dados = MutableList(5) { Dado() }
    val caras = listOf("Tiro 1", "Tiro 2", "Cerveza", "Dinamita", "Gatling", "Flecha")
    var dinamitasActuales = 0
    var banderagringa = true
    var disparo = false
    var tiroDobleUsado = false

    //Jugadores
    fun setJugadores(lista: List<Jugador>) {
        jugadores = lista
    }

    fun getJugadores(): List<Jugador> = jugadores

    fun getJugadorActual(): Jugador? = jugadores.getOrNull(indexActual)

    fun getIndexActual(): Int = indexActual

    fun avanzarJugador(): Boolean {
        if (indexActual + 1 < jugadores.size) {
            indexActual++
            return true // hay más jugadores
        }
        return false // ya fue el último
    }
    fun siguienteTurno()  {
        do {
            indexActual = (indexActual + 1) % jugadores.size
        } while (jugadores[indexActual].muerto)
        reiniciarTiros()
    }
    fun darvida(jugador: Jugador,seleccionado: Jugador, saludactual: Int){
        if (seleccionado == jugador) {
            val cantidad = if (jugador.personaje.nombre == "Jesse Jones" && saludactual <= 4) 2 else 1
            jugador.salud = min(jugador.salud + cantidad, jugador.saludTotal)
        } else {
            seleccionado.salud = min(seleccionado.salud + 1, seleccionado.saludTotal)
        }
    }

    //Dados
    fun tirarDados() {
        if (!puedeTirar()) return
        val jugador = getJugadorActual() ?: return
        tirosRealizados++
        if (dados.none { it.seleccionado }) {
            dados.forEach {
                if (it.valor != "Dinamita" || jugador.nombre == "Black Jack") it.seleccionado = true
            }
        }
        dados.forEach {
            if (it.valor == "Dinamita" && jugador.nombre == "Black Jack" && it.seleccionado) dinamitasActuales--
        }

        dados.forEachIndexed { i, dado ->
            if (dado.seleccionado) {
                val nuevoValor = caras.random()
                dado.valor = nuevoValor
                dado.seleccionado = false

                when (nuevoValor) {
                    "Flecha" -> {
                         Flechas.tomarFlecha(jugador, jugadores)
                    }
                    "Dinamita" -> dinamitasActuales++
                }
                if (dinamitasActuales >= 3) {
                    jugador.salud--
                    tirosRealizados = 4
                }
            }

        }
    }

    fun reiniciarTiros() {
        tirosRealizados = 0
        dinamitasActuales = 0
        dados.forEach {
            it.valor = ""
            it.seleccionado = false
        }
    }

    fun puedeTirar(): Boolean{
        val jugador = getJugadorActual() ?: return false
        return if (jugador.personaje.nombre == "Lucky Duke") {
            tirosRealizados < 4
        } else {
            tirosRealizados < 3
        }
    }

    fun exploto(): Boolean = dinamitasActuales >= 3

    fun jugadoresAdyacentes(distancia: Int): List<Jugador> {
        val jugadores = getJugadores()
        val index = getIndexActual()
        val alcance = if (getJugadorActual()?.personaje?.nombre == "Rose Doolan") distancia + 1 else distancia

        val izquierda = (index - alcance + jugadores.size) % jugadores.size
        val derecha = (index + alcance) % jugadores.size

        val indices = if (izquierda != derecha) listOf(izquierda, derecha) else listOf(izquierda)


        return indices.mapNotNull { i ->
            jugadores.getOrNull(i)?.takeIf { !it.muerto }
        }
    }

    fun verificarFinDelJuego(): String? {
        val vivos = jugadores.filter { !it.muerto }
        val sheriff = vivos.find { it.rol == "Sheriff" }
        val forajidos = vivos.filter { it.rol == "Forajido" }
        val renegados = vivos.filter { it.rol == "Renegado" }

        // a) El Sheriff es eliminado
        if (sheriff == null) {
            return when {
                renegados.size == 1 && forajidos.isEmpty() -> "¡El Renegado gana!"
                else -> "¡Los Forajidos ganan!"
            }
        }

        // b) Todos los Forajidos y Renegados son eliminados
        if (forajidos.isEmpty() && renegados.isEmpty()) {
            return "¡El Sheriff y los Alguaciles ganan!"
        }

        // c) Todos los jugadores eliminados a la vez
        if (vivos.isEmpty()) {
            return "¡Los Forajidos ganan!"
        }

        // No ha terminado aún
        return null
    }
    fun mostrarDialogoOpcion(
        contexto: Context,
        mensaje: String,
        textoPositivo: String,
        textoNegativo: String,
        onResultado: (Boolean) -> Unit
    ) {
        val builder = AlertDialog.Builder(contexto)
        builder.setMessage(mensaje)
            .setPositiveButton(textoPositivo) { _, _ -> onResultado(true) }
            .setNegativeButton(textoNegativo) { _, _ -> onResultado(false) }
            .setCancelable(false)
            .create()
            .show()
    }
    fun mostrarDialogoSeleccion(
        contexto: Context,
        titulo: String,
        opciones: List<Jugador>,
        onSeleccionado: (Jugador) -> Unit
    ) {
        val nombres = opciones.map { it.nombre }.toTypedArray()
        AlertDialog.Builder(contexto)
            .setTitle(titulo)
            .setItems(nombres) { _, which ->
                onSeleccionado(opciones[which])
            }
            .setCancelable(false)
            .show()
    }

    fun verificarMuerto(contexto: Context) {
        val jugadores = getJugadores()
        val muertos = jugadores.filter { it.salud <= 0 && !it.muerto }

        muertos.forEach { muerto ->
            muerto.muerto = true
            muerto.salud = 0
            Flechas.reiniciarFlechas(muerto)

            AlertDialog.Builder(contexto)
                .setTitle("Muerte")
                .setMessage("Murió: ${muerto.nombre}")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show()

            val buitre = jugadores.find { it.personaje.nombre == "“Buitre” Sam" && !it.muerto }
            if (buitre != null) {
                buitre.salud = min(buitre.salud + 2, buitre.saludTotal)
            }
        }
    }
}



