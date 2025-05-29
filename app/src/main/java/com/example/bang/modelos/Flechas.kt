package com.example.bang.modelos

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
            val damage  = jugador.flechas
            if (damage  > 0) {
                val damageFinal = if (jugador.personaje.nombre == "Jourdonnais") minOf(damage, 1) else damage
                jugador.salud -= damageFinal
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