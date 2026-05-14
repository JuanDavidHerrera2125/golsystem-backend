package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoRequestDTO {
    private String nombre;
    private String codigoEquipo;
    private String logoUrl;
    private Boolean activo = true;
    private Long torneoId; // Opcional: si se envía, se crea la relación Equipo-Torneo
}
