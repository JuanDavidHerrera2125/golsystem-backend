package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.CampeonHistorico;
import java.util.List;
import java.util.Optional;

public interface CampeonHistoricoService {
    
    List<CampeonHistorico> findAll();
    
    Optional<CampeonHistorico> findById(Long id);
    
    Optional<CampeonHistorico> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
    
    CampeonHistorico save(CampeonHistorico campeonHistorico);
    
    CampeonHistorico update(Long id, CampeonHistorico campeonHistorico);
    
    void deleteById(Long id);
    
    List<CampeonHistorico> findByTorneoIdOrderByFechaLogroDesc(Long torneoId);
    
    List<CampeonHistorico> findByEquipoIdOrderByFechaLogroDesc(Long equipoId);
    
    List<CampeonHistorico> findAllOrderByFechaLogroDesc();
    
    Integer countCampeonatosPorEquipo(Long equipoId);
    
    CampeonHistorico registrarCampeon(Long torneoId, Long equipoId);
    
    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);
}
