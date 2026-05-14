package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorDTO {
    private Long id;
    private String documentoIdentidad;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String fotoUrl;
    private Boolean activo;
    
    // Información del equipo
    private Long equipoId;
    private String equipoNombre;
    private String equipoCodigo;
}
