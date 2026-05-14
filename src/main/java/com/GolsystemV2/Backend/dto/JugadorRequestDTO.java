package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos del frontend al crear/actualizar jugador.
 * El frontend envía 'nombres' y 'apellidos' (plural) pero la entidad usa 'nombre' y 'apellido' (singular).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorRequestDTO {
    private Long id;
    private String documentoIdentidad;
    private String nombres;      // Frontend envía 'nombres'
    private String apellidos;    // Frontend envía 'apellidos'
    private String fotoUrl;
    private Boolean activo;
    private Long equipoId;       // ID del equipo al que pertenece el jugador
}
