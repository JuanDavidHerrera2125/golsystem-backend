package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import java.util.List;
import java.util.Optional;

public interface TorneoService {
    
    List<Torneo> findAll();
    
    Optional<Torneo> findById(Long id);
    
    Optional<Torneo> findByNombre(String nombre);
    
    Torneo save(Torneo torneo);
    
    Torneo update(Long id, Torneo torneo);
    
    void deleteById(Long id);
    
    boolean existsByNombre(String nombre);
    
    List<Torneo> findByEstado(EstadoTorneo estado);
    
    List<Torneo> findTorneosEnConfiguracion();
    
    List<Torneo> findTorneosEnCurso();
    
    Torneo iniciarTorneo(Long torneoId);
    
    Torneo finalizarTorneo(Long torneoId);
    
    boolean validarInicioTorneo(Long torneoId);
}
