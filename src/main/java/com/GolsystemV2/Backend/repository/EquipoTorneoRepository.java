package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoTorneoRepository extends JpaRepository<EquipoTorneo, Long> {
    
    Optional<EquipoTorneo> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    List<EquipoTorneo> findByTorneoIdAndEliminadoFalse(Long torneoId);
    
    List<EquipoTorneo> findByTorneoId(Long torneoId);
    
    @Query("SELECT COUNT(et) FROM EquipoTorneo et WHERE et.torneo.id = :torneoId AND et.eliminado = false")
    Integer countEquiposActivosPorTorneo(Long torneoId);
    
    @Query("SELECT et FROM EquipoTorneo et JOIN JugadorEquipoTorneo jet ON et.id = jet.equipoTorneo.id WHERE jet.jugador.id = :jugadorId AND et.torneo.id = :torneoId AND jet.activo = true")
    Optional<EquipoTorneo> findByJugadorAndTorneo(Long jugadorId, Long torneoId);
}
