package com.GolsystemV2.Backend.dto;

import com.GolsystemV2.Backend.enums.CategoriaTorneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDTO {
    private Long id;
    private String nombre;
    private String logoUrl;
    private CategoriaTorneo categoria;
    private Integer minJugadores;
    private Integer maxJugadores;
    private EstadoTorneo estado;
    
    // Nuevos campos para sincronización con frontend
    private String tipo;
    private String fase;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    
    private String descripcion;
    private Integer cantidadDeGrupos;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;
}
