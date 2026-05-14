package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Jugador;
import java.util.List;
import java.util.Optional;

public interface JugadorService {
    
    List<Jugador> findAll();
    
    Optional<Jugador> findById(Long id);
    
    Optional<Jugador> findByDocumentoIdentidad(String documentoIdentidad);
    
    Jugador save(Jugador jugador);
    
    Jugador update(Long id, Jugador jugador);
    
    void deleteById(Long id);
    
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    
    List<Jugador> findByActivoTrue();
    
    Jugador desactivarJugador(Long id);
    
    Jugador activarJugador(Long id);
    
    List<Jugador> findByEquipoTorneoId(Long equipoTorneoId);
    
    List<Jugador> findByEquipoId(Long equipoId);
}
