package com.GolsystemV2.Backend.dto;

import com.GolsystemV2.Backend.enums.EstadoFase;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.enums.TipoFase;

import java.util.List;

/**
 * DTO de respuesta con estado enriquecido de la Fase.
 * Devuelto por GET /api/fases/{id}/estado
 */
public class FaseEstadoDto {

    private Long id;
    private Long torneoId;
    private Integer numeroFase;
    private TipoFase tipoFase;
    private FormatoEncuentro formatoEncuentro;
    private EstadoFase estadoFase;

    private Integer cantidadGrupos;
    private Integer equiposClasificadosPorGrupo;
    private Integer totalEquipos;
    private Integer totalPartidos;
    private Integer partidosFinalizados;
    private Integer partidosPendientes;

    /** true si la fase ya no puede ser editada estructuralmente (estado != CONFIGURACION) */
    private Boolean bloqueadaParaEdicion;

    /** true si se cumplen todas las condiciones para pasar a EN_CURSO */
    private Boolean puedeActivarse;

    /** true si todos los partidos están finalizados y la fase puede cerrarse */
    private Boolean puedeCerrarse;

    /** Porcentaje de partidos completados (0–100) */
    private Integer porcentajeAvance;

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTorneoId() { return torneoId; }
    public void setTorneoId(Long torneoId) { this.torneoId = torneoId; }

    public Integer getNumeroFase() { return numeroFase; }
    public void setNumeroFase(Integer numeroFase) { this.numeroFase = numeroFase; }

    public TipoFase getTipoFase() { return tipoFase; }
    public void setTipoFase(TipoFase tipoFase) { this.tipoFase = tipoFase; }

    public FormatoEncuentro getFormatoEncuentro() { return formatoEncuentro; }
    public void setFormatoEncuentro(FormatoEncuentro formatoEncuentro) { this.formatoEncuentro = formatoEncuentro; }

    public EstadoFase getEstadoFase() { return estadoFase; }
    public void setEstadoFase(EstadoFase estadoFase) { this.estadoFase = estadoFase; }

    public Integer getCantidadGrupos() { return cantidadGrupos; }
    public void setCantidadGrupos(Integer cantidadGrupos) { this.cantidadGrupos = cantidadGrupos; }

    public Integer getEquiposClasificadosPorGrupo() { return equiposClasificadosPorGrupo; }
    public void setEquiposClasificadosPorGrupo(Integer equiposClasificadosPorGrupo) {
        this.equiposClasificadosPorGrupo = equiposClasificadosPorGrupo;
    }

    public Integer getTotalEquipos() { return totalEquipos; }
    public void setTotalEquipos(Integer totalEquipos) { this.totalEquipos = totalEquipos; }

    public Integer getTotalPartidos() { return totalPartidos; }
    public void setTotalPartidos(Integer totalPartidos) { this.totalPartidos = totalPartidos; }

    public Integer getPartidosFinalizados() { return partidosFinalizados; }
    public void setPartidosFinalizados(Integer partidosFinalizados) { this.partidosFinalizados = partidosFinalizados; }

    public Integer getPartidosPendientes() { return partidosPendientes; }
    public void setPartidosPendientes(Integer partidosPendientes) { this.partidosPendientes = partidosPendientes; }

    public Boolean getBloqueadaParaEdicion() { return bloqueadaParaEdicion; }
    public void setBloqueadaParaEdicion(Boolean bloqueadaParaEdicion) { this.bloqueadaParaEdicion = bloqueadaParaEdicion; }

    public Boolean getPuedeActivarse() { return puedeActivarse; }
    public void setPuedeActivarse(Boolean puedeActivarse) { this.puedeActivarse = puedeActivarse; }

    public Boolean getPuedeCerrarse() { return puedeCerrarse; }
    public void setPuedeCerrarse(Boolean puedeCerrarse) { this.puedeCerrarse = puedeCerrarse; }

    public Integer getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(Integer porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    /** Lista de equipos asignados a esta fase */
    private List<EquipoInfoDto> equipos;

    public List<EquipoInfoDto> getEquipos() { return equipos; }
    public void setEquipos(List<EquipoInfoDto> equipos) { this.equipos = equipos; }
}
