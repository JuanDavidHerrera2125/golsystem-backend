package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.CampeonHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampeonHistoricoRepository extends JpaRepository<CampeonHistorico, Long> {
    
    List<CampeonHistorico> findByTorneoIdOrderByFechaLogroDesc(Long torneoId);
    
    List<CampeonHistorico> findByEquipoIdOrderByFechaLogroDesc(Long equipoId);
    
    Optional<CampeonHistorico> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    @Query("SELECT COUNT(ch) FROM CampeonHistorico ch WHERE ch.equipo.id = :equipoId")
    Integer countCampeonatosPorEquipo(Long equipoId);
    
    @Query("SELECT ch FROM CampeonHistorico ch ORDER BY ch.fechaLogro DESC")
    List<CampeonHistorico> findAllOrderByFechaLogroDesc();
    
    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
}
