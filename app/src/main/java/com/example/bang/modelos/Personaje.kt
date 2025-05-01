package com.example.bang.modelos

import java.io.Serializable

data class Personaje(
    val nombre: String,
    val vidaBase: Int,
    val habilidad: String
): Serializable
val personajesDisponibles = listOf(
    Personaje("Bart Cassidy", 8, "Puedes coger una flecha en lugar de perder un punto de vida (excepto por indios o dinamita)."),
    Personaje("Paul Regret", 9, "Nunca pierdes puntos de vida por la ametralladora Gatling."),
    Personaje("Black Jack", 8, "Puedes volver a lanzar dinamitas (pero no si sacas tres o más dinamitas)."),
    Personaje("Pedro Ramírez", 8, "Cada vez que pierdes un punto de vida, puedes descartar una de tus flechas."),
    Personaje("Calamity Janet", 8, "Puedes usar Tiro 1 como Tiro 2 y viceversa."),
    Personaje("Rose Doolan", 9, "Puedes atacar a un jugador un puesto más allá."),
    Personaje("El Gringo", 7, "Quien te haga perder vida coge una flecha."),
    Personaje("Sid Ketchum", 8, "Al comenzar tu turno, cualquier jugador (incluso tú) recupera un punto de vida."),
    Personaje("Jesse Jones", 9, "Si tienes 4 vidas o menos, cada cerveza contigo mismo cura 2 vidas."),
    Personaje("Slab “el Asesino”", 8, "Una vez por turno puedes usar una cerveza para usar un disparo especial."),
    Personaje("Jourdonnais", 7, "Nunca pierdes más de 1 vida por indios."),
    Personaje("Suzy Lafayette", 8, "Si no sacaste ningún disparo, ganas 2 vidas al final del turno."),
    Personaje("Kit Carlson", 7, "Cada dinamita te permite quitar una flecha a cualquier jugador."),
    Personaje("“Buitre” Sam", 9, "Cuando otro jugador muere, ganas 2 vidas."),
    Personaje("Lucky Duke", 8, "Puedes lanzar los dados hasta 4 veces por turno."),
    Personaje("Willy “el Niño”", 8, "Solo necesitas 2 Gatlings para activarla.")
)