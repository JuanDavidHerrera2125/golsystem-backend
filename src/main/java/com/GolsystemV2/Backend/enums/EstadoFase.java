package com.GolsystemV2.Backend.enums;

public enum EstadoFase {
    /**
     * La fase está siendo configurada: se puede definir número de grupos,
     * formato de encuentro y cantidad de equipos. No hay partidos generados.
     */
    CONFIGURACION,

    /**
     * La fase está activa: los grupos están llenos, los partidos están generados.
     * No se permiten cambios estructurales (grupos, equipos).
     */
    EN_CURSO,

    /**
     * Todos los partidos terminaron, se ejecutó la promoción de equipos
     * y se dispararon los eventos de histórico.
     */
    FINALIZADA
}
