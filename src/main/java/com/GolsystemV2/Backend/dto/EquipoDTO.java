package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private Long id;
    private String codigoEquipo;
    private String nombre;
    private String logoUrl;
    private Boolean activo;
    private Long torneoId;  // ID del torneo al que pertenece (null si no está en ninguno)
}
