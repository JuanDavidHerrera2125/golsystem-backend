package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Grupo;
import java.util.List;
import java.util.Optional;

public interface GrupoService {
    
    List<Grupo> findAll();
    
    Optional<Grupo> findById(Long id);
    
    Optional<Grupo> findByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo);
    
    Grupo save(Grupo grupo);
    
    Grupo update(Long id, Grupo grupo);
    
    void deleteById(Long id);
    
    List<Grupo> findByFaseIdOrderByNombreGrupo(Long faseId);
    
    List<Grupo> findByFaseId(Long faseId);
    
    boolean existsByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo);
}
