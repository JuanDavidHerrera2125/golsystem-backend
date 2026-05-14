package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampeonHistoricoDTO {
    private Long id;
    private LocalDate fechaLogro;
    
    private EquipoDTO equipo;
    private TorneoDTO torneo;
}
