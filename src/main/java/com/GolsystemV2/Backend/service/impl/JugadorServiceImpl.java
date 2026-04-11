package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.repository.JugadorRepository;
import com.GolsystemV2.Backend.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

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
        return jugadorRepository.save(jugador);
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
}
