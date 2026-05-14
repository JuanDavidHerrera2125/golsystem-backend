package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import java.util.List;
import java.util.Optional;

public interface GoleadorHistoricoService {
    
    List<GoleadorHistorico> findAll();
    
    Optional<GoleadorHistorico> findById(Long id);
    
    Optional<GoleadorHistorico> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    GoleadorHistorico save(GoleadorHistorico goleadorHistorico);
    
    GoleadorHistorico update(Long id, GoleadorHistorico goleadorHistorico);
    
    void deleteById(Long id);
    
    List<GoleadorHistorico> findByTorneoIdOrderByTotalGolesDesc(Long torneoId);
    
    List<GoleadorHistorico> findByJugadorIdOrderByTotalGolesDesc(Long jugadorId);
    
    List<GoleadorHistorico> findByEquipoIdOrderByTotalGolesDesc(Long equipoId);
    
    List<GoleadorHistorico> findTablaGoleadoresPorTorneo(Long torneoId);
    
    Integer countTorneosGoleador(Long jugadorId);
    
    GoleadorHistorico actualizarGoles(Long jugadorId, Long equipoId, Long torneoId, Integer totalGoles);
    
    boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);

    /**
     * Recorre todos los jugadores con goles en el torneo y actualiza/crea
     * sus registros en GoleadorHistorico. Disparado por PhaseManagerService
     * al cerrar una fase.
     *
     * @param torneoId ID del torneo
     */
    void registrarGoleadoresDelTorneo(Long torneoId);
}
