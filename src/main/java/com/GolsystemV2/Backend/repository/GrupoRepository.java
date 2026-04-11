package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    
    List<Grupo> findByFaseIdOrderByNombreGrupo(Long faseId);
    
    Optional<Grupo> findByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo);
    
    boolean existsByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo);
    
    List<Grupo> findByFaseId(Long faseId);
}
