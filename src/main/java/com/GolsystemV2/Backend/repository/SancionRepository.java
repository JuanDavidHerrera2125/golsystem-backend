package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {
    
    List<Sancion> findByTorneoIdAndActivaTrue(Long torneoId);
    
    List<Sancion> findByJugadorEquipoTorneoIdAndActivaTrue(Long jugadorEquipoTorneoId);
    
    List<Sancion> findByJugadorEquipoTorneoId(Long jugadorEquipoTorneoId);
    
    Optional<Sancion> findByJugadorEquipoTorneoIdAndActivaTrueAndEncuentroOrigenIdIsNull(Long jugadorEquipoTorneoId);
    
    @Query("SELECT s FROM Sancion s WHERE s.jugadorEquipoTorneo.id = :jugadorEquipoTorneoId AND s.torneo.id = :torneoId AND s.activa = true")
    List<Sancion> findSancionesActivasPorJugadorTorneo(Long jugadorEquipoTorneoId, Long torneoId);
    
    @Query("SELECT COUNT(s) FROM Sancion s WHERE s.jugadorEquipoTorneo.id = :jugadorEquipoTorneoId AND s.torneo.id = :torneoId AND s.activa = true")
    Integer countSancionesActivasPorJugadorTorneo(Long jugadorEquipoTorneoId, Long torneoId);
    
    boolean existsByJugadorEquipoTorneoIdAndActivaTrue(Long jugadorEquipoTorneoId);
}
