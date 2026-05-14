package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.repository.JugadorRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Jugador> findAll() {
        return jugadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Jugador> findById(Long id) {
        return jugadorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Jugador> findByDocumentoIdentidad(String documentoIdentidad) {
        return jugadorRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public Jugador save(Jugador jugador) {
        if (existsByDocumentoIdentidad(jugador.getDocumentoIdentidad())) {
            throw new RuntimeException("Ya existe un jugador con el documento de identidad: " + jugador.getDocumentoIdentidad());
        }
        // Inicializar campo activo si es null (el frontend puede no enviarlo)
        if (jugador.getActivo() == null) {
            jugador.setActivo(true);
        }
        
        System.out.println("[DEBUG Service] Guardando jugador - Equipo antes de save: " + (jugador.getEquipo() != null ? jugador.getEquipo().getId() : "NULL"));
        
        Jugador saved = jugadorRepository.save(jugador);
        
        System.out.println("[DEBUG Service] Jugador guardado - Equipo después de save: " + (saved.getEquipo() != null ? saved.getEquipo().getId() : "NULL"));
        
        return saved;
    }

    @Override
    public Jugador update(Long id, Jugador jugador) {
        Jugador existingJugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
        
        if (!existingJugador.getDocumentoIdentidad().equals(jugador.getDocumentoIdentidad()) 
                && existsByDocumentoIdentidad(jugador.getDocumentoIdentidad())) {
            throw new RuntimeException("Ya existe un jugador con el documento de identidad: " + jugador.getDocumentoIdentidad());
        }
        
        existingJugador.setNombre(jugador.getNombre());
        existingJugador.setApellido(jugador.getApellido());
        existingJugador.setFechaNacimiento(jugador.getFechaNacimiento());
        existingJugador.setFotoUrl(jugador.getFotoUrl());
        existingJugador.setDocumentoIdentidad(jugador.getDocumentoIdentidad());
        existingJugador.setActivo(jugador.getActivo());
        
        // Actualizar equipo si se proporciona
        if (jugador.getEquipo() != null) {
            existingJugador.setEquipo(jugador.getEquipo());
        }
        
        return jugadorRepository.save(existingJugador);
    }

    @Override
    public void deleteById(Long id) {
        if (!jugadorRepository.existsById(id)) {
            throw new RuntimeException("Jugador no encontrado con ID: " + id);
        }
        jugadorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return jugadorRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugador> findByActivoTrue() {
        return jugadorRepository.findAll().stream()
                .filter(Jugador::getActivo)
                .toList();
    }

    @Override
    public Jugador desactivarJugador(Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
        jugador.setActivo(false);
        return jugadorRepository.save(jugador);
    }

    @Override
    public Jugador activarJugador(Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
        jugador.setActivo(true);
        return jugadorRepository.save(jugador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugador> findByEquipoTorneoId(Long equipoTorneoId) {
        List<JugadorEquipoTorneo> jugadoresEquipo = jugadorEquipoTorneoRepository.findByEquipoTorneoId(equipoTorneoId);
        return jugadoresEquipo.stream()
                .map(JugadorEquipoTorneo::getJugador)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugador> findByEquipoId(Long equipoId) {
        return jugadorRepository.findByEquipoId(equipoId);
    }
}
