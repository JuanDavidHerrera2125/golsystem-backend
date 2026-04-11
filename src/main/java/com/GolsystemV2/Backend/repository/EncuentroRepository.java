package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EncuentroRepository extends JpaRepository<Encuentro, Long> {
    
    List<Encuentro> findByFaseIdOrderByFecha(Long faseId);
    
    List<Encuentro> findByGrupoIdOrderByFecha(Long grupoId);
    
    List<Encuentro> findByFaseIdAndEstado(Long faseId, EstadoEncuentro estado);
    
    List<Encuentro> findByEquipoLocalIdOrEquipoVisitanteId(Long equipoId, Long equipoId2);
    
    @Query("SELECT e FROM Encuentro e WHERE e.equipoLocal.id = :equipoId OR e.equipoVisitante.id = :equipoId ORDER BY e.fecha")
    List<Encuentro> findPartidosPorEquipo(Long equipoId);
    
    @Query("SELECT e FROM Encuentro e WHERE e.fase.id = :faseId AND e.estado != 'FINALIZADO'")
    List<Encuentro> findPartidosNoFinalizadosPorFase(Long faseId);
    
    @Query("SELECT COUNT(e) FROM Encuentro e WHERE e.fase.id = :faseId AND e.estado = 'FINALIZADO'")
    Integer countPartidosFinalizadosPorFase(Long faseId);
    
    @Query("SELECT COUNT(e) FROM Encuentro e WHERE e.fase.id = :faseId")
    Integer countTotalPartidosPorFase(Long faseId);
    
    List<Encuentro> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
