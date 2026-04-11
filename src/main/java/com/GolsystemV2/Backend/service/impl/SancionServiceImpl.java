package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Sancion;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.repository.SancionRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.service.SancionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SancionServiceImpl implements SancionService {

    @Autowired
    private SancionRepository sancionRepository;
    
    @Autowired
    private TorneoRepository torneoRepository;
    
    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;
    
    @Autowired
    private EncuentroRepository encuentroRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Sancion> findAll() {
        return sancionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sancion> findById(Long id) {
        return sancionRepository.findById(id);
    }

    @Override
    public Sancion save(Sancion sancion) {
        return sancionRepository.save(sancion);
    }

    @Override
    public Sancion update(Long id, Sancion sancion) {
        Sancion existingSancion = sancionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sancion no encontrada con ID: " + id));
        
        existingSancion.setTorneo(sancion.getTorneo());
        existingSancion.setJugadorEquipoTorneo(sancion.getJugadorEquipoTorneo());
        existingSancion.setEncuentroOrigen(sancion.getEncuentroOrigen());
        existingSancion.setCantidadFechas(sancion.getCantidadFechas());
        existingSancion.setFechasCumplidas(sancion.getFechasCumplidas());
        existingSancion.setActiva(sancion.getActiva());
        
        return sancionRepository.save(existingSancion);
    }

    @Override
    public void deleteById(Long id) {
        if (!sancionRepository.existsById(id)) {
            throw new RuntimeException("Sancion no encontrada con ID: " + id);
        }
        sancionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sancion> findByTorneoIdAndActivaTrue(Long torneoId) {
        return sancionRepository.findByTorneoIdAndActivaTrue(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sancion> findByJugadorEquipoTorneoIdAndActivaTrue(Long jugadorEquipoTorneoId) {
        return sancionRepository.findByJugadorEquipoTorneoIdAndActivaTrue(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sancion> findByJugadorEquipoTorneoId(Long jugadorEquipoTorneoId) {
        return sancionRepository.findByJugadorEquipoTorneoId(jugadorEquipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countSancionesActivasPorJugadorTorneo(Long jugadorEquipoTorneoId, Long torneoId) {
        return sancionRepository.countSancionesActivasPorJugadorTorneo(jugadorEquipoTorneoId, torneoId);
    }

    @Override
    public Sancion crearSancion(Long torneoId, Long jugadorEquipoTorneoId, Long encuentroOrigenId, Integer cantidadFechas) {
        Sancion sancion = new Sancion();
        
        sancion.setTorneo(torneoRepository.findById(torneoId)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId)));
        sancion.setJugadorEquipoTorneo(jugadorEquipoTorneoRepository.findById(jugadorEquipoTorneoId)
            .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + jugadorEquipoTorneoId)));
        if (encuentroOrigenId != null) {
            sancion.setEncuentroOrigen(encuentroRepository.findById(encuentroOrigenId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroOrigenId)));
        }
        sancion.setCantidadFechas(cantidadFechas);
        sancion.setFechasCumplidas(0);
        sancion.setActiva(true);
        
        return sancionRepository.save(sancion);
    }

    @Override
    public Sancion incrementarFechasCumplidas(Long sancionId) {
        Sancion sancion = sancionRepository.findById(sancionId)
                .orElseThrow(() -> new RuntimeException("Sancion no encontrada con ID: " + sancionId));
        
        sancion.setFechasCumplidas(sancion.getFechasCumplidas() + 1);
        
        if (sancion.getFechasCumplidas() >= sancion.getCantidadFechas()) {
            sancion.setActiva(false);
        }
        
        return sancionRepository.save(sancion);
    }

    @Override
    public Sancion desactivarSancion(Long sancionId) {
        Sancion sancion = sancionRepository.findById(sancionId)
                .orElseThrow(() -> new RuntimeException("Sancion no encontrada con ID: " + sancionId));
        
        sancion.setActiva(false);
        
        return sancionRepository.save(sancion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean jugadorEstaSancionado(Long jugadorEquipoTorneoId, Long torneoId) {
        Integer sancionesActivas = countSancionesActivasPorJugadorTorneo(jugadorEquipoTorneoId, torneoId);
        return sancionesActivas > 0;
    }
}
