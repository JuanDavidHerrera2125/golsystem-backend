package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Fase;
import java.util.List;
import java.util.Optional;

public interface FaseService {
    
    List<Fase> findAll();
    
    Optional<Fase> findById(Long id);
    
    Optional<Fase> findByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);
    
    Fase save(Fase fase);
    
    Fase update(Long id, Fase fase);
    
    void deleteById(Long id);
    
    List<Fase> findByTorneoIdOrderByNumeroFase(Long torneoId);
    
    List<Fase> findByTorneoIdAndEstaCerradaFalse(Long torneoId);
    
    Integer findMaxNumeroFaseByTorneoId(Long torneoId);
    
    Fase cerrarFase(Long faseId);
    
    Fase crearSiguienteFase(Long torneoId, Integer numeroFase);
    
    boolean validarCierreFase(Long faseId);
}
