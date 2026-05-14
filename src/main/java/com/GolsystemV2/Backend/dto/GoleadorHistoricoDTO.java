package com.GolsystemV2.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoleadorHistoricoDTO {
    private Long id;
    private Integer totalGoles;
    
    private JugadorDTO jugador;
    private EquipoDTO equipo;
    private TorneoDTO torneo;
}
