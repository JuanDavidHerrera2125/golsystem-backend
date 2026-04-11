package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.enums.TipoEvento;
import java.util.List;
import java.util.Optional;

public interface EventoPartidoService {
    
    List<EventoPartido> findAll();
    
    Optional<EventoPartido> findById(Long id);
    
    EventoPartido save(EventoPartido eventoPartido);
    
    EventoPartido update(Long id, EventoPartido eventoPartido);
    
    void deleteById(Long id);
    
    List<EventoPartido> findByEncuentroIdOrderByMinuto(Long encuentroId);
    
    List<EventoPartido> findByJugadorEquipoTorneoIdOrderByMinuto(Long jugadorEquipoTorneoId);
    
    List<EventoPartido> findByEncuentroIdAndTipoEvento(Long encuentroId, TipoEvento tipoEvento);
    
    Integer countGolesPorJugador(Long jugadorEquipoTorneoId);
    
    Integer countAmarillasPorJugador(Long jugadorEquipoTorneoId);
    
    Integer countRojasPorJugador(Long jugadorEquipoTorneoId);
    
    List<EventoPartido> findEventosPorEquipo(Long equipoTorneoId);
    
    boolean validarRegistroEvento(Long encuentroId);
    
    EventoPartido registrarGol(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto);
    
    EventoPartido registrarTarjetaAmarilla(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto);
    
    EventoPartido registrarTarjetaRoja(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto);
}
