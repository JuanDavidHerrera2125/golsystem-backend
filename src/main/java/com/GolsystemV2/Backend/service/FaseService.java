package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.dto.FaseConfigDto;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoFase;

import java.util.List;
import java.util.Optional;

public interface FaseService {

    List<Fase> findAll();

    Optional<Fase> findById(Long id);

    Optional<Fase> findByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);

    Fase save(Fase fase);

    Fase update(Long id, Fase fase);

    void deleteById(Long id);

    List<Fase> findByTorneoIdOrderByNumeroFase(Long torneoId);

    /** Reemplaza el antiguo findByTorneoIdAndEstaCerradaFalse */
    List<Fase> findByTorneoIdAndEstadoFase(Long torneoId, EstadoFase estadoFase);

    Integer findMaxNumeroFaseByTorneoId(Long torneoId);

    // ─── Delegado a PhaseManagerService ───────────────────────────────────────

    /** Configura una nueva fase (valida progresión lineal, grupos 1-15). */
    FaseEstadoDto configurarFase(FaseConfigDto dto);

    /** Activa la fase: distribuye equipos en grupos, bloquea edición. */
    FaseEstadoDto activarFase(Long faseId);

    /**
     * Cierra la fase: valida que todos los partidos estén FINALIZADO,
     * dispara históricos, marca FINALIZADA.
     */
    FaseEstadoDto cerrarFase(Long faseId);

    /** Avanza al siguiente número de fase (N+1) para un torneo. */
    FaseEstadoDto avanzarSiguienteFase(Long torneoId);

    /** Devuelve el estado enriquecido de la fase. */
    FaseEstadoDto obtenerEstadoFase(Long faseId);

    /** Verifica si la fase puede activarse. */
    boolean puedeActivarFase(Long faseId);

    /** @deprecated Usar cerrarFase(Long faseId) que retorna FaseEstadoDto */
    @Deprecated
    boolean validarCierreFase(Long faseId);
}
