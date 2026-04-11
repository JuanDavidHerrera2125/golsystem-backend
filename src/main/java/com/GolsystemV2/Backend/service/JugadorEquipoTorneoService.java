package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import java.util.List;
import java.util.Optional;

public interface JugadorEquipoTorneoService {
    
    List<JugadorEquipoTorneo> findAll();
    
    Optional<JugadorEquipoTorneo> findById(Long id);
    
    Optional<JugadorEquipoTorneo> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    JugadorEquipoTorneo save(JugadorEquipoTorneo jugadorEquipoTorneo);
    
    JugadorEquipoTorneo update(Long id, JugadorEquipoTorneo jugadorEquipoTorneo);
    
    void deleteById(Long id);
    
    boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoIdAndActivoTrue(Long equipoTorneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoId(Long equipoTorneoId);
    
    Integer countJugadoresActivosPorEquipoTorneo(Long equipoTorneoId);
    
    JugadorEquipoTorneo desvincularJugador(Long id);
    
    JugadorEquipoTorneo vincularJugador(Long id);
    
    boolean validarInscripcionJugador(Long jugadorId, Long torneoId, Long equipoTorneoId);
    
    boolean validarLimiteJugadores(Long equipoTorneoId, Long torneoId);
}
