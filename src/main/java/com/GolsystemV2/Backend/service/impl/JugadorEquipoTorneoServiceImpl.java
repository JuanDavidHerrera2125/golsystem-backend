package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.dto.InscripcionJugadorDTO;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.JugadorRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.service.JugadorEquipoTorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class JugadorEquipoTorneoServiceImpl implements JugadorEquipoTorneoService {

    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JugadorEquipoTorneo> findAll() {
        return jugadorEquipoTorneoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JugadorEquipoTorneo> findById(Long id) {
        return jugadorEquipoTorneoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JugadorEquipoTorneo> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId) {
        return jugadorEquipoTorneoRepository.findByJugadorIdAndEquipoTorneoTorneoId(jugadorId, torneoId);
    }

    @Override
    public JugadorEquipoTorneo save(JugadorEquipoTorneo jugadorEquipoTorneo) {
        Long jugadorId = jugadorEquipoTorneo.getJugador().getId();
        Long torneoId = jugadorEquipoTorneo.getEquipoTorneo().getTorneo().getId();
        Long equipoTorneoId = jugadorEquipoTorneo.getEquipoTorneo().getId();
        
        if (existsByJugadorIdAndTorneoId(jugadorId, torneoId)) {
            throw new RuntimeException("El jugador ya está inscrito en este torneo");
        }
        
        if (!validarInscripcionJugador(jugadorId, torneoId, equipoTorneoId)) {
            throw new RuntimeException("No se puede inscribir el jugador en este torneo");
        }
        
        if (!validarLimiteJugadores(equipoTorneoId, torneoId)) {
            throw new RuntimeException("El equipo ha alcanzado el límite máximo de jugadores para este torneo");
        }
        
        return jugadorEquipoTorneoRepository.save(jugadorEquipoTorneo);
    }

    @Override
    public JugadorEquipoTorneo update(Long id, JugadorEquipoTorneo jugadorEquipoTorneo) {
        JugadorEquipoTorneo existingJugadorEquipoTorneo = jugadorEquipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + id));
        
        existingJugadorEquipoTorneo.setNumeroCamiseta(jugadorEquipoTorneo.getNumeroCamiseta());
        existingJugadorEquipoTorneo.setActivo(jugadorEquipoTorneo.getActivo());
        
        return jugadorEquipoTorneoRepository.save(existingJugadorEquipoTorneo);
    }

    @Override
    public void deleteById(Long id) {
        if (!jugadorEquipoTorneoRepository.existsById(id)) {
            throw new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + id);
        }
        jugadorEquipoTorneoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId) {
        return jugadorEquipoTorneoRepository.existsByJugadorIdAndEquipoTorneoTorneoId(jugadorId, torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorEquipoTorneo> findByEquipoTorneoIdAndActivoTrue(Long equipoTorneoId) {
        return jugadorEquipoTorneoRepository.findByEquipoTorneoIdAndActivoTrue(equipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorEquipoTorneo> findByEquipoTorneoId(Long equipoTorneoId) {
        return jugadorEquipoTorneoRepository.findByEquipoTorneoId(equipoTorneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countJugadoresActivosPorEquipoTorneo(Long equipoTorneoId) {
        return jugadorEquipoTorneoRepository.countJugadoresActivosPorEquipoTorneo(equipoTorneoId);
    }

    @Override
    public JugadorEquipoTorneo desvincularJugador(Long id) {
        JugadorEquipoTorneo jugadorEquipoTorneo = jugadorEquipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + id));
        jugadorEquipoTorneo.setActivo(false);
        return jugadorEquipoTorneoRepository.save(jugadorEquipoTorneo);
    }

    @Override
    public JugadorEquipoTorneo vincularJugador(Long id) {
        JugadorEquipoTorneo jugadorEquipoTorneo = jugadorEquipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JugadorEquipoTorneo no encontrado con ID: " + id));
        jugadorEquipoTorneo.setActivo(true);
        return jugadorEquipoTorneoRepository.save(jugadorEquipoTorneo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarInscripcionJugador(Long jugadorId, Long torneoId, Long equipoTorneoId) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(jugadorId);
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        Optional<EquipoTorneo> equipoTorneoOpt = equipoTorneoRepository.findById(equipoTorneoId);
        
        if (jugadorOpt.isEmpty() || torneoOpt.isEmpty() || equipoTorneoOpt.isEmpty()) {
            return false;
        }
        
        Jugador jugador = jugadorOpt.get();
        Torneo torneo = torneoOpt.get();
        EquipoTorneo equipoTorneo = equipoTorneoOpt.get();
        
        if (!jugador.getActivo()) {
            throw new RuntimeException("El jugador no está activo");
        }
        
        if (!equipoTorneo.getTorneo().getId().equals(torneoId)) {
            throw new RuntimeException("El equipo no pertenece al torneo especificado");
        }
        
        if (equipoTorneo.getEliminado()) {
            throw new RuntimeException("El equipo está eliminado del torneo");
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarLimiteJugadores(Long equipoTorneoId, Long torneoId) {
        Integer jugadoresActuales = countJugadoresActivosPorEquipoTorneo(equipoTorneoId);
        Optional<EquipoTorneo> equipoTorneoOpt = equipoTorneoRepository.findById(equipoTorneoId);
        
        if (equipoTorneoOpt.isEmpty()) {
            return false;
        }
        
        EquipoTorneo equipoTorneo = equipoTorneoOpt.get();
        Torneo torneo = equipoTorneo.getTorneo();
        
        return jugadoresActuales < torneo.getMaxJugadores();
    }
    
    @Override
    @Transactional
    public Map<String, Object> inscribirJugadorCompleto(InscripcionJugadorDTO dto) {
        Map<String, Object> resultado = new HashMap<>();
        
        // 1. Buscar o crear el jugador
        Jugador jugador;
        Optional<Jugador> jugadorExistente = jugadorRepository.findByDocumentoIdentidad(dto.getDocumentoIdentidad());
        
        if (jugadorExistente.isPresent()) {
            jugador = jugadorExistente.get();
            resultado.put("jugadorExistente", true);
            resultado.put("mensajeJugador", "Jugador encontrado en registro global");
        } else {
            // Crear nuevo jugador
            if (dto.getNombre() == null || dto.getApellido() == null) {
                throw new RuntimeException("Nombre y apellido son requeridos para crear nuevo jugador");
            }
            
            jugador = new Jugador();
            jugador.setDocumentoIdentidad(dto.getDocumentoIdentidad());
            jugador.setNombre(dto.getNombre());
            jugador.setApellido(dto.getApellido());
            jugador.setFechaNacimiento(dto.getFechaNacimiento());
            jugador.setFotoUrl(dto.getFotoUrl());
            jugador.setActivo(true);
            
            jugador = jugadorRepository.save(jugador);
            resultado.put("jugadorExistente", false);
            resultado.put("mensajeJugador", "Nuevo jugador creado en registro global");
        }
        
        // 2. Buscar el equipoTorneo
        EquipoTorneo equipoTorneo = equipoTorneoRepository.findById(dto.getEquipoTorneoId())
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + dto.getEquipoTorneoId()));
        
        Long torneoId = equipoTorneo.getTorneo().getId();
        Long jugadorId = jugador.getId();
        
        // 3. Validar que el jugador no esté en otro equipo del mismo torneo
        Optional<JugadorEquipoTorneo> inscripcionExistente = 
                jugadorEquipoTorneoRepository.findJugadorActivoEnTorneo(jugadorId, torneoId);
        
        if (inscripcionExistente.isPresent()) {
            Long equipoActualId = inscripcionExistente.get().getEquipoTorneo().getId();
            if (!equipoActualId.equals(dto.getEquipoTorneoId())) {
                String nombreEquipoActual = inscripcionExistente.get().getEquipoTorneo().getEquipo().getNombre();
                throw new RuntimeException("El jugador ya está inscrito en otro equipo de este torneo: " + nombreEquipoActual);
            }
            throw new RuntimeException("El jugador ya está inscrito en este equipo");
        }
        
        // 4. Validar límites del torneo
        if (!validarLimiteJugadores(dto.getEquipoTorneoId(), torneoId)) {
            throw new RuntimeException("El equipo ha alcanzado el límite máximo de jugadores para este torneo");
        }
        
        // 5. Crear la relación JugadorEquipoTorneo
        JugadorEquipoTorneo jugadorEquipoTorneo = new JugadorEquipoTorneo();
        jugadorEquipoTorneo.setJugador(jugador);
        jugadorEquipoTorneo.setEquipoTorneo(equipoTorneo);
        jugadorEquipoTorneo.setNumeroCamiseta(dto.getNumeroCamiseta());
        jugadorEquipoTorneo.setActivo(true);
        
        JugadorEquipoTorneo guardado = jugadorEquipoTorneoRepository.save(jugadorEquipoTorneo);
        
        // 6. Preparar resultado
        resultado.put("inscripcion", guardado);
        resultado.put("jugador", jugador);
        resultado.put("equipo", equipoTorneo.getEquipo());
        resultado.put("torneo", equipoTorneo.getTorneo());
        resultado.put("mensaje", "Jugador inscrito exitosamente en el equipo");
        
        return resultado;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> buscarJugadorPorDocumento(String documentoIdentidad) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findByDocumentoIdentidad(documentoIdentidad);
        
        if (jugadorOpt.isPresent()) {
            Jugador jugador = jugadorOpt.get();
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("id", jugador.getId());
            resultado.put("documentoIdentidad", jugador.getDocumentoIdentidad());
            resultado.put("nombre", jugador.getNombre());
            resultado.put("apellido", jugador.getApellido());
            resultado.put("fechaNacimiento", jugador.getFechaNacimiento());
            resultado.put("fotoUrl", jugador.getFotoUrl());
            resultado.put("activo", jugador.getActivo());
            return Optional.of(resultado);
        }
        
        return Optional.empty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<JugadorEquipoTorneo> findByJugadorIdAndEquipoTorneoId(Long jugadorId, Long equipoTorneoId) {
        return jugadorEquipoTorneoRepository.findByJugadorIdAndEquipoTorneoId(jugadorId, equipoTorneoId);
    }
}
