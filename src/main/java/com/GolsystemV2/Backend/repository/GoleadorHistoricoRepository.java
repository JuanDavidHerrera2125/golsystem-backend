package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoleadorHistoricoRepository extends JpaRepository<GoleadorHistorico, Long> {
    
    List<GoleadorHistorico> findByTorneoIdOrderByTotalGolesDesc(Long torneoId);
    
    List<GoleadorHistorico> findByJugadorIdOrderByTotalGolesDesc(Long jugadorId);
    
    List<GoleadorHistorico> findByEquipoIdOrderByTotalGolesDesc(Long equipoId);
    
    Optional<GoleadorHistorico> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    @Query("SELECT gh FROM GoleadorHistorico gh WHERE gh.torneo.id = :torneoId ORDER BY gh.totalGoles DESC")
    List<GoleadorHistorico> findTablaGoleadoresPorTorneo(Long torneoId);
    
    @Query("SELECT COUNT(gh) FROM GoleadorHistorico gh WHERE gh.jugador.id = :jugadorId")
    Integer countTorneosGoleador(Long jugadorId);
    
    boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
}
