package com.GolsystemV2.Backend.enums;

/**
 * Estados posibles del cronometro de un partido
 */
public enum EstadoCronometro {
    SIN_INICIAR,      // Partido no ha comenzado
    EN_JUEGO,         // Cronometro corriendo
    PAUSADO,          // Pausa por lesion/evento
    ENTRETIEMPO,      // Medio tiempo
    TIEMPO_EXTRA,     // Prorroga
    FINALIZADO        // Partido terminado
}
