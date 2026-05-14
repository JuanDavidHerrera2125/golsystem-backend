package com.GolsystemV2.Backend.dto;

import lombok.Data;

/**
 * DTO para registrar un evento de partido desde el Match Center
 */
@Data
public class EventoPartidoRequest {
    
    private String tipo; // GOL, TARJETA_AMARILLA, TARJETA_ROJA, SUSTITUCION
    private Long jugadorId; // ID del jugador que cometió el evento
    private Long equipoId; // ID del equipo
    private Integer minuto; // Minuto del partido
    private String tipoGol; // NORMAL, PENAL, TIRO_LIBRE, CABEZA, AUTOGOL (solo para goles)
    private Long asistenciaId; // ID del jugador que asistió (solo para goles)
    private String motivo; // Motivo de la tarjeta o descripción
    private Long jugadorEntraId; // Para sustituciones
    private Long jugadorSaleId; // Para sustituciones
}
