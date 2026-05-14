package com.GolsystemV2.Backend.dto;

/**
 * DTO de información resumida de Equipo para respuestas.
 * Incluye solo los campos necesarios para mostrar en listas y cards.
 */
public class EquipoInfoDto {

    private Long id;
    private String nombre;
    private String logoUrl;
    private String codigoEquipo;

    public EquipoInfoDto() {}

    public EquipoInfoDto(Long id, String nombre, String logoUrl, String codigoEquipo) {
        this.id = id;
        this.nombre = nombre;
        this.logoUrl = logoUrl;
        this.codigoEquipo = codigoEquipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getCodigoEquipo() { return codigoEquipo; }
    public void setCodigoEquipo(String codigoEquipo) { this.codigoEquipo = codigoEquipo; }
}
