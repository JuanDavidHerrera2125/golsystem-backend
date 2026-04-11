package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.enums.TipoEvento;
import com.GolsystemV2.Backend.repository.EventoPartidoRepository;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.TablaPosicionesRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.service.EventoPartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoPartidoServiceImpl implements EventoPartidoService {

    @Autowired
    private EventoPartidoRepository eventoPartidoRepository;

    @Autowired
    private EncuentroRepository encuentroRepository;

    @Autowired
    private TablaPosicionesRepository tablaPosicionesRepository;
    
    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;
    
    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartido> findAll() {
        return eventoPartidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventoPartido> findById(Long id) {
        return eventoPartidoRepository.findById(id);
    }

    @Override
    public EventoPartido save(EventoPartido eventoPartido) {
        if (!validarRegistroEvento(eventoPartido.getEncuentro().getId())) {
            throw new RuntimeException("No se pueden registrar eventos en partidos finalizados");
        }
        
        EventoPartido savedEvento = eventoPartidoRepository.save(eventoPartido);
        
        actualizarEstadisticasAutomaticamente(savedEvento);
        
        return savedEvento;
    }

    @Override
    public EventoPartido update(Long id, EventoPartido eventoPartido) {
        EventoPartido existingEvento = eventoPartidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EventoPartido no encontrado con ID: " + id));
        
        existingEvento.setEncuentro(eventoPartido.getEncuentro());
        existingEvento.setJugadorEquipoTorneo(eventoPartido.getJugadorEquipoTorneo());
        existingEvento.setEquipoTorneo(eventoPartido.getEquipoTorneo());
        existingEvento.setTipoEvento(eventoPartido.getTipoEvento());
        existingEvento.setMinuto(eventoPartido.getMinuto());
        
        return eventoPartidoRepository.save(existingEvento);
    }

    @Override
    public void deleteById(Long id) {
        if (!eventoPartidoRepository.existsById(id)) {
            throw new RuntimeException("EventoPartido no encontrado con ID: " + id);
        }
        eventoPartidoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartido> findByEncuentroIdOrderByMinuto(Long encuentroId) {
        return eventoPartidoRepository.findByEncuentroIdOrderByMinuto(encuentroId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartido> findByJugadorEquipoTorneoIdOrderByMinuto(Long jugadorEquipoTorneoId) {
        return eventoPartidoRepository.findByJugadorEquipoTorneoIdOrderByMinuto(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartido> findByEncuentroIdAndTipoEvento(Long encuentroId, TipoEvento tipoEvento) {
        return eventoPartidoRepository.findByEncuentroIdAndTipoEvento(encuentroId, tipoEvento);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countGolesPorJugador(Long jugadorEquipoTorneoId) {
        return eventoPartidoRepository.countGolesPorJugador(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countAmarillasPorJugador(Long jugadorEquipoTorneoId) {
        return eventoPartidoRepository.countAmarillasPorJugador(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countRojasPorJugador(Long jugadorEquipoTorneoId) {
        return eventoPartidoRepository.countRojasPorJugador(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartido> findEventosPorEquipo(Long equipoTorneoId) {
        return eventoPartidoRepository.findEventosPorEquipo(equipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarRegistroEvento(Long encuentroId) {
        Optional<Encuentro> encuentroOpt = encuentroRepository.findById(encuentroId);
        
        if (encuentroOpt.isEmpty()) {
            return false;
        }
        
        Encuentro encuentro = encuentroOpt.get();
        return !encuentro.getEstado().name().equals("FINALIZADO");
    }

    @Override
    public EventoPartido registrarGol(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto) {
        EventoPartido evento = new EventoPartido();
        
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        evento.setEncuentro(encuentro);
        evento.setJugadorEquipoTorneo(jugadorEquipoTorneoRepository.findById(jugadorEquipoTorneoId)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + jugadorEquipoTorneoId)));
        evento.setEquipoTorneo(equipoTorneoRepository.findById(equipoTorneoId)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + equipoTorneoId)));
        evento.setTipoEvento(TipoEvento.GOL);
        evento.setMinuto(minuto);
        
        return save(evento);
    }

    @Override
    public EventoPartido registrarTarjetaAmarilla(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto) {
        EventoPartido evento = new EventoPartido();
        
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        evento.setEncuentro(encuentro);
        evento.setJugadorEquipoTorneo(jugadorEquipoTorneoRepository.findById(jugadorEquipoTorneoId)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + jugadorEquipoTorneoId)));
        evento.setEquipoTorneo(equipoTorneoRepository.findById(equipoTorneoId)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + equipoTorneoId)));
        evento.setTipoEvento(TipoEvento.AMARILLA);
        evento.setMinuto(minuto);
        
        return save(evento);
    }

    @Override
    public EventoPartido registrarTarjetaRoja(Long encuentroId, Long jugadorEquipoTorneoId, Long equipoTorneoId, Integer minuto) {
        EventoPartido evento = new EventoPartido();
        
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        evento.setEncuentro(encuentro);
        evento.setJugadorEquipoTorneo(jugadorEquipoTorneoRepository.findById(jugadorEquipoTorneoId)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + jugadorEquipoTorneoId)));
        evento.setEquipoTorneo(equipoTorneoRepository.findById(equipoTorneoId)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + equipoTorneoId)));
        evento.setTipoEvento(TipoEvento.ROJA);
        evento.setMinuto(minuto);
        
        return save(evento);
    }

    private void actualizarEstadisticasAutomaticamente(EventoPartido evento) {
        if (evento.getEncuentro() != null && evento.getEncuentro().getGrupo() != null) {
            Long grupoId = evento.getEncuentro().getGrupo().getId();
            Long equipoTorneoId = evento.getEquipoTorneo().getId();
            
            Optional<TablaPosiciones> tablaOpt = tablaPosicionesRepository.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId);
            
            if (tablaOpt.isPresent()) {
                TablaPosiciones tabla = tablaOpt.get();
                
                switch (evento.getTipoEvento()) {
                    case GOL:
                        tabla.setGf(tabla.getGf() + 1);
                        break;
                    case AMARILLA:
                        tabla.setAmarillas(tabla.getAmarillas() + 1);
                        break;
                    case ROJA:
                        tabla.setRojas(tabla.getRojas() + 1);
                        break;
                }
                
                tablaPosicionesRepository.save(tabla);
            }
        }
    }
}
