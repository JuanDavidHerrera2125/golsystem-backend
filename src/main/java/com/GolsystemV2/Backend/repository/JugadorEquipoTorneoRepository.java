package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorEquipoTorneoRepository extends JpaRepository<JugadorEquipoTorneo, Long> {
    
    Optional<JugadorEquipoTorneo> findByJugadorIdAndEquipoTorneoTorneoId(Long jugadorId, Long torneoId);
    
    boolean existsByJugadorIdAndEquipoTorneoTorneoId(Long jugadorId, Long torneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoIdAndActivoTrue(Long equipoTorneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoId(Long equipoTorneoId);
    
    @Query("SELECT COUNT(jet) FROM JugadorEquipoTorneo jet WHERE jet.equipoTorneo.id = :equipoTorneoId AND jet.activo = true")
    Integer countJugadoresActivosPorEquipoTorneo(Long equipoTorneoId);
    
    @Query("SELECT jet FROM JugadorEquipoTorneo jet WHERE jet.jugador.id = :jugadorId AND jet.equipoTorneo.torneo.id = :torneoId AND jet.activo = true")
    Optional<JugadorEquipoTorneo> findJugadorActivoEnTorneo(Long jugadorId, Long torneoId);
}
