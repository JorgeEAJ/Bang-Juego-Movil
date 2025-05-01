package com.example.bang.modelos

import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.bang.JuegoActivity
import com.example.bang.viewModel.JuegoViewModel
import kotlin.getValue

object Flechas {
    const val MAX_FLECHAS = 9
    var disponibles = MAX_FLECHAS

    fun tomarFlecha(jugador: Jugador, jugadores: List<Jugador>){
        if (disponibles > 0) {
            disponibles--
            jugador.flechas++
            if (disponibles == 0){
                atacarIndios(jugadores)
            }
        }
    }

    fun atacarIndios(jugadores: List<Jugador>) {
        jugadores.forEach { jugador ->
            val daño = jugador.flechas
            if (daño > 0) {
                val dañoFinal = if (jugador.personaje.nombre == "Jourdonnais") minOf(daño, 1) else daño
                jugador.salud -= dañoFinal
            }
            jugador.flechas = 0
        }
        disponibles = MAX_FLECHAS
    }
    fun reiniciarFlechas(jugador: Jugador){
        disponibles += jugador.flechas
        jugador.flechas = 0
    }
}