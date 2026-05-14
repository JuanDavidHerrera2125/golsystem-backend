package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import java.util.List;
import java.util.Map;
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
    
    Torneo distribuirEquiposEnGrupos(Long torneoId, FormatoEncuentro formatoEncuentro);
    
    List<Map<String, Object>> obtenerGruposConEquipos(Long torneoId);
    
    List<Map<String, Object>> obtenerFases(Long torneoId);
    
    List<Map<String, Object>> obtenerEncuentros(Long torneoId);

    List<Map<String, Object>> obtenerEncuentrosActivos(Long torneoId);

    Map<String, Object> obtenerEstadisticas(Long torneoId);
}
