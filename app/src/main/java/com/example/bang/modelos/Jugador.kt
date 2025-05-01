package com.example.bang.modelos

import java.io.Serializable

data class Jugador(
    val nombre: String,
    var rol: String,
    var salud: Int,
    var saludTotal: Int,
    var flechas: Int = 0,
    var personaje: Personaje,
    var muerto: Boolean
): Serializable