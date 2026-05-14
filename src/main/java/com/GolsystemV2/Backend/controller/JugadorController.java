package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.JugadorRequestDTO;
import com.GolsystemV2.Backend.dto.JugadorDTO;
import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.service.JugadorService;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoRepository equipoRepository;

    @GetMapping
    public ResponseEntity<List<JugadorDTO>> findAll() {
        List<Jugador> jugadores = jugadorService.findAll();
        List<JugadorDTO> dtos = jugadores.stream()
            .map(this::convertirEntidadADto)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> findById(@PathVariable Long id) {
        return jugadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documentoIdentidad}")
    public ResponseEntity<Jugador> findByDocumentoIdentidad(@PathVariable String documentoIdentidad) {
        return jugadorService.findByDocumentoIdentidad(documentoIdentidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Jugador>> findByActivoTrue() {
        List<Jugador> jugadores = jugadorService.findByActivoTrue();
        return ResponseEntity.ok(jugadores);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody JugadorRequestDTO jugadorDTO) {
        try {
            // Log para verificar qué llega del frontend
            System.out.println("[DEBUG POST] === INICIO CREACIÓN JUGADOR ===");
            System.out.println("[DEBUG POST] DTO recibido: " + jugadorDTO);
            System.out.println("[DEBUG POST] equipoId en DTO: " + jugadorDTO.getEquipoId());
            
            // Convertir DTO a entidad (mapear nombres -> nombre, apellidos -> apellido)
            Jugador jugador = convertirDtoAEntidad(jugadorDTO);
            
            System.out.println("[DEBUG POST] Entidad antes de guardar - Equipo: " + (jugador.getEquipo() != null ? jugador.getEquipo().getId() : "NULL"));
            
            Jugador savedJugador = jugadorService.save(jugador);
            
            System.out.println("[DEBUG POST] Entidad guardada - Equipo: " + (savedJugador.getEquipo() != null ? savedJugador.getEquipo().getId() : "NULL"));
            System.out.println("[DEBUG POST] === FIN CREACIÓN JUGADOR ===");
            
            // Devolver como DTO con info del equipo
            return ResponseEntity.ok(convertirEntidadADto(savedJugador));
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error al crear jugador: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of(
                "error", "Error al crear jugador",
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jugador> update(@PathVariable Long id, @RequestBody JugadorRequestDTO jugadorDTO) {
        try {
            // Convertir DTO a entidad
            Jugador jugador = convertirDtoAEntidad(jugadorDTO);
            jugador.setId(id); // Asegurar que el ID sea el de la URL
            Jugador updatedJugador = jugadorService.update(id, jugador);
            return ResponseEntity.ok(updatedJugador);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Método privado para convertir DTO a entidad Jugador.
     * Mapea nombres -> nombre y apellidos -> apellido
     */
    private Jugador convertirDtoAEntidad(JugadorRequestDTO dto) {
        Jugador jugador = new Jugador();
        jugador.setId(dto.getId());
        jugador.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        jugador.setNombre(dto.getNombres());      // nombres -> nombre
        jugador.setApellido(dto.getApellidos());  // apellidos -> apellido
        jugador.setFotoUrl(dto.getFotoUrl());
        jugador.setActivo(dto.getActivo());
        
        // Buscar y asignar el equipo si se proporciona el equipoId
        if (dto.getEquipoId() != null) {
            System.out.println("[DEBUG] Buscando equipo con ID: " + dto.getEquipoId());
            Equipo equipo = equipoRepository.findById(dto.getEquipoId()).orElse(null);
            if (equipo != null) {
                jugador.setEquipo(equipo);
                System.out.println("[DEBUG] Equipo asignado al jugador: " + equipo.getNombre() + " (ID: " + equipo.getId() + ")");
            } else {
                System.out.println("[DEBUG] No se encontró el equipo con ID: " + dto.getEquipoId());
            }
        } else {
            System.out.println("[DEBUG] No se proporcionó equipoId en el DTO");
        }
        
        return jugador;
    }

    /**
     * Método privado para convertir entidad Jugador a DTO.
     * Incluye información del equipo si está asignado.
     */
    private JugadorDTO convertirEntidadADto(Jugador jugador) {
        JugadorDTO dto = new JugadorDTO();
        dto.setId(jugador.getId());
        dto.setDocumentoIdentidad(jugador.getDocumentoIdentidad());
        dto.setNombre(jugador.getNombre());
        dto.setApellido(jugador.getApellido());
        dto.setFechaNacimiento(jugador.getFechaNacimiento());
        dto.setFotoUrl(jugador.getFotoUrl());
        dto.setActivo(jugador.getActivo());
        
        // Agregar información del equipo si está asignado
        if (jugador.getEquipo() != null) {
            dto.setEquipoId(jugador.getEquipo().getId());
            dto.setEquipoNombre(jugador.getEquipo().getNombre());
            dto.setEquipoCodigo(jugador.getEquipo().getCodigoEquipo());
        }
        
        return dto;
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<JugadorDTO>> obtenerJugadoresPorEquipo(@PathVariable Long equipoId) {
        List<Jugador> jugadores = jugadorService.findByEquipoId(equipoId);
        List<JugadorDTO> dtos = jugadores.stream()
                .map(this::convertirEntidadADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Jugador> desactivarJugador(@PathVariable Long id) {
        try {
            Jugador jugador = jugadorService.desactivarJugador(id);
            return ResponseEntity.ok(jugador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Jugador> activarJugador(@PathVariable Long id) {
        try {
            Jugador jugador = jugadorService.activarJugador(id);
            return ResponseEntity.ok(jugador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            jugadorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/documento/{documentoIdentidad}")
    public ResponseEntity<Boolean> existsByDocumentoIdentidad(@PathVariable String documentoIdentidad) {
        boolean exists = jugadorService.existsByDocumentoIdentidad(documentoIdentidad);
        return ResponseEntity.ok(exists);
    }

    // Endpoint para obtener jugadores por equipo-torneo
    // Este endpoint es usado por el frontend en MatchCenter.jsx
    @GetMapping("/equipo-torneo/{equipoTorneoId}")
    public ResponseEntity<List<Jugador>> findByEquipoTorneoId(@PathVariable Long equipoTorneoId) {
        // Delegar al JugadorEquipoTorneoService para obtener jugadores de ese equipo-torneo
        List<Jugador> jugadores = jugadorService.findByEquipoTorneoId(equipoTorneoId);
        return ResponseEntity.ok(jugadores);
    }
}
