package com.GolsystemV2.Backend.dto;

/**
 * DTO de referencia ligera para Equipo.
 * Se usa para recibir solo el ID del equipo en listas y relaciones.
 */
public class EquipoRefDto {

    private Long id;

    public EquipoRefDto() {}

    public EquipoRefDto(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
