package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import java.util.List;
import java.util.Optional;

public interface EquipoTorneoService {
    
    List<EquipoTorneo> findAll();
    
    Optional<EquipoTorneo> findById(Long id);
    
    Optional<EquipoTorneo> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    EquipoTorneo save(EquipoTorneo equipoTorneo);
    
    EquipoTorneo update(Long id, EquipoTorneo equipoTorneo);
    
    void deleteById(Long id);
    
    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    List<EquipoTorneo> findByTorneoIdAndEliminadoFalse(Long torneoId);
    
    List<EquipoTorneo> findByTorneoId(Long torneoId);
    
    Integer countEquiposActivosPorTorneo(Long torneoId);
    
    EquipoTorneo eliminarEquipoDelTorneo(Long id);
    
    EquipoTorneo reactivarEquipoEnTorneo(Long id);

    boolean validarInscripcionEquipo(Long torneoId, Long equipoId);

    /**
     * Inscribe un equipo en un torneo de forma directa.
     * Usado internamente cuando se crea un equipo con torneoId.
     */
    EquipoTorneo inscribirEquipoEnTorneo(Long torneoId, Long equipoId);
}
