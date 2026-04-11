package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Equipo;
import java.util.List;
import java.util.Optional;

public interface EquipoService {
    
    List<Equipo> findAll();
    
    Optional<Equipo> findById(Long id);
    
    Optional<Equipo> findByCodigoEquipo(String codigoEquipo);
    
    Equipo save(Equipo equipo);
    
    Equipo update(Long id, Equipo equipo);
    
    void deleteById(Long id);
    
    boolean existsByCodigoEquipo(String codigoEquipo);
    
    List<Equipo> findByActivoTrue();
    
    Equipo desactivarEquipo(Long id);
    
    Equipo activarEquipo(Long id);
}
