package com.GolsystemV2.Backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class InscripcionJugadorDTO {
    
    // Datos del equipo-torneo al que se inscribe
    private Long equipoTorneoId;
    
    // Datos del jugador (para búsqueda o creación)
    private String documentoIdentidad;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String fotoUrl;
    
    // Datos de la inscripción
    private Integer numeroCamiseta;
    
    // Flag para indicar si es nuevo jugador
    private Boolean esNuevoJugador = false;
}
