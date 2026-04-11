package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TablaPosicionesRepository extends JpaRepository<TablaPosiciones, Long> {
    
    List<TablaPosiciones> findByGrupoIdOrderByIdDesc(Long grupoId);
    
    Optional<TablaPosiciones> findByGrupoIdAndEquipoTorneoId(Long grupoId, Long equipoTorneoId);
    
    List<TablaPosiciones> findByGrupoIdAndEstado(Long grupoId, EstadoTablaPosiciones estado);
    
    @Query("SELECT tp FROM TablaPosiciones tp WHERE tp.grupo.id = :grupoId ORDER BY tp.pts DESC, tp.dg DESC, tp.gf DESC")
    List<TablaPosiciones> findTablaPosicionesOrdenada(Long grupoId);
    
    boolean existsByGrupoIdAndEquipoTorneoId(Long grupoId, Long equipoTorneoId);
}
