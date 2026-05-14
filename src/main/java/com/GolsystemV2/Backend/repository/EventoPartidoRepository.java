package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Long> {
    
    List<EventoPartido> findByEncuentroIdOrderByMinuto(Long encuentroId);
    
    List<EventoPartido> findByJugadorEquipoTorneoIdOrderByMinuto(Long jugadorEquipoTorneoId);
    
    List<EventoPartido> findByEncuentroIdAndTipoEvento(Long encuentroId, TipoEvento tipoEvento);
    
    @Query("SELECT COUNT(ep) FROM EventoPartido ep WHERE ep.jugadorEquipoTorneo.id = :jugadorEquipoTorneoId AND ep.tipoEvento = 'GOL'")
    Integer countGolesPorJugador(Long jugadorEquipoTorneoId);
    
    @Query("SELECT COUNT(ep) FROM EventoPartido ep WHERE ep.jugadorEquipoTorneo.id = :jugadorEquipoTorneoId AND ep.tipoEvento = 'AMARILLA'")
    Integer countAmarillasPorJugador(Long jugadorEquipoTorneoId);
    
    @Query("SELECT COUNT(ep) FROM EventoPartido ep WHERE ep.jugadorEquipoTorneo.id = :jugadorEquipoTorneoId AND ep.tipoEvento = 'ROJA'")
    Integer countRojasPorJugador(Long jugadorEquipoTorneoId);
    
    @Query("SELECT ep FROM EventoPartido ep WHERE ep.equipoTorneo.id = :equipoTorneoId ORDER BY ep.encuentro.fecha, ep.minuto")
    List<EventoPartido> findEventosPorEquipo(Long equipoTorneoId);

    @Query("SELECT COUNT(ep) FROM EventoPartido ep WHERE ep.jugadorEquipoTorneo.jugador.id = :jugadorId AND ep.encuentro.fase.torneo.id = :torneoId AND ep.tipoEvento = com.GolsystemV2.Backend.enums.TipoEvento.GOL")
    Integer countGolesPorJugadorEnTorneo(Long jugadorId, Long torneoId);
}
