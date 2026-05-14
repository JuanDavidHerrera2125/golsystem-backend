package com.GolsystemV2.Backend.dto;

import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.enums.MetodoDesempatePlayoff;
import com.GolsystemV2.Backend.enums.TipoFase;

import java.util.List;

/**
 * DTO de configuración inicial de una Fase.
 * Se usa en POST /api/fases/configurar para crear y configurar una fase nueva.
 */
public class FaseConfigDto {

    private Long torneoId;

    /** Número de la fase: 1, 2, 3... La progresión debe ser lineal. */
    private Integer numeroFase;

    /** Tipo de fase: GRUPOS o ELIMINACION_DIRECTA */
    private TipoFase tipoFase;

    /** Modalidad de los encuentros: SOLO_IDA o IDA_Y_VUELTA */
    private FormatoEncuentro formatoEncuentro;

    /** Cantidad de grupos (1 a 15). Obligatorio para fases de tipo GRUPOS. */
    private Integer cantidadGrupos;

    /** Cuántos equipos clasifican por grupo a la siguiente fase. */
    private Integer equiposClasificadosPorGrupo;

    /** Método de desempate para fases eliminatorias. Puede ser null para GRUPOS. */
    private MetodoDesempatePlayoff metodoDesempatePlayoff;

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public Long getTorneoId() { return torneoId; }
    public void setTorneoId(Long torneoId) { this.torneoId = torneoId; }

    public Integer getNumeroFase() { return numeroFase; }
    public void setNumeroFase(Integer numeroFase) { this.numeroFase = numeroFase; }

    public TipoFase getTipoFase() { return tipoFase; }
    public void setTipoFase(TipoFase tipoFase) { this.tipoFase = tipoFase; }

    public FormatoEncuentro getFormatoEncuentro() { return formatoEncuentro; }
    public void setFormatoEncuentro(FormatoEncuentro formatoEncuentro) { this.formatoEncuentro = formatoEncuentro; }

    public Integer getCantidadGrupos() { return cantidadGrupos; }
    public void setCantidadGrupos(Integer cantidadGrupos) { this.cantidadGrupos = cantidadGrupos; }

    public Integer getEquiposClasificadosPorGrupo() { return equiposClasificadosPorGrupo; }
    public void setEquiposClasificadosPorGrupo(Integer equiposClasificadosPorGrupo) {
        this.equiposClasificadosPorGrupo = equiposClasificadosPorGrupo;
    }

    public MetodoDesempatePlayoff getMetodoDesempatePlayoff() { return metodoDesempatePlayoff; }
    public void setMetodoDesempatePlayoff(MetodoDesempatePlayoff metodoDesempatePlayoff) {
        this.metodoDesempatePlayoff = metodoDesempatePlayoff;
    }

    /** Lista de equipos seleccionados para la fase (ids) */
    private List<EquipoRefDto> equipos;

    public List<EquipoRefDto> getEquipos() { return equipos; }
    public void setEquipos(List<EquipoRefDto> equipos) { this.equipos = equipos; }
}
