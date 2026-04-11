package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.entity.CampeonHistorico;
import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.CampeonHistoricoRepository;
import com.GolsystemV2.Backend.repository.GoleadorHistoricoRepository;
import com.GolsystemV2.Backend.service.TorneoService;
import com.GolsystemV2.Backend.service.CampeonHistoricoService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TorneoServiceImpl implements TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;

    @Autowired
    private CampeonHistoricoRepository campeonHistoricoRepository;

    @Autowired
    private GoleadorHistoricoRepository goleadorHistoricoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torneo> findByNombre(String nombre) {
        return torneoRepository.findByNombre(nombre);
    }

    @Override
    public Torneo save(Torneo torneo) {
        if (existsByNombre(torneo.getNombre())) {
            throw new RuntimeException("Ya existe un torneo con el nombre: " + torneo.getNombre());
        }
        return torneoRepository.save(torneo);
    }

    @Override
    public Torneo update(Long id, Torneo torneo) {
        Torneo existingTorneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + id));
        
        if (!existingTorneo.getNombre().equals(torneo.getNombre()) 
                && existsByNombre(torneo.getNombre())) {
            throw new RuntimeException("Ya existe un torneo con el nombre: " + torneo.getNombre());
        }
        
        existingTorneo.setNombre(torneo.getNombre());
        existingTorneo.setLogoUrl(torneo.getLogoUrl());
        existingTorneo.setCategoria(torneo.getCategoria());
        existingTorneo.setMinJugadores(torneo.getMinJugadores());
        existingTorneo.setMaxJugadores(torneo.getMaxJugadores());
        
        return torneoRepository.save(existingTorneo);
    }

    @Override
    public void deleteById(Long id) {
        if (!torneoRepository.existsById(id)) {
            throw new RuntimeException("Torneo no encontrado con ID: " + id);
        }
        torneoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return torneoRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findByEstado(EstadoTorneo estado) {
        return torneoRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findTorneosEnConfiguracion() {
        return torneoRepository.findTorneosEnConfiguracion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findTorneosEnCurso() {
        return torneoRepository.findTorneosEnCurso();
    }

    @Override
    public Torneo iniciarTorneo(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        if (!EstadoTorneo.CONFIGURACION.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden iniciar torneos en estado CONFIGURACION");
        }
        
        if (!validarInicioTorneo(torneoId)) {
            throw new RuntimeException("No se puede iniciar el torneo. Algunos equipos no cumplen con el mínimo de jugadores requerido");
        }
        
        torneo.setEstado(EstadoTorneo.EN_CURSO);
        return torneoRepository.save(torneo);
    }

    @Override
    public Torneo finalizarTorneo(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        if (!EstadoTorneo.EN_CURSO.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden finalizar torneos en estado EN_CURSO");
        }
        
        torneo.setEstado(EstadoTorneo.FINALIZADO);
        return torneoRepository.save(torneo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarInicioTorneo(Long torneoId) {
        List<EquipoTorneo> equiposActivos = equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        
        for (EquipoTorneo equipoTorneo : equiposActivos) {
            Integer jugadoresActivos = jugadorEquipoTorneoRepository.countJugadoresActivosPorEquipoTorneo(equipoTorneo.getId());
            Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
            
            if (torneo != null && jugadoresActivos < torneo.getMinJugadores()) {
                return false;
            }
        }
        
        return true;
    }
}
