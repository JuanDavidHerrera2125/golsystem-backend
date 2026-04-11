package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {
    
    List<Fase> findByTorneoIdOrderByNumeroFase(Long torneoId);
    
    Optional<Fase> findByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);
    
    List<Fase> findByTorneoIdAndEstaCerradaFalse(Long torneoId);
    
    @Query("SELECT MAX(f.numeroFase) FROM Fase f WHERE f.torneo.id = :torneoId")
    Integer findMaxNumeroFaseByTorneoId(Long torneoId);
    
    boolean existsByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);
}
