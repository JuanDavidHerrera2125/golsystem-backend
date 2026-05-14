package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.InscripcionJugadorDTO;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.service.JugadorEquipoTorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jugadores-equipos-torneo")
public class JugadorEquipoTorneoController {

    @Autowired
    private JugadorEquipoTorneoService jugadorEquipoTorneoService;

    @GetMapping
    public ResponseEntity<List<JugadorEquipoTorneo>> findAll() {
        List<JugadorEquipoTorneo> jugadoresEquiposTorneo = jugadorEquipoTorneoService.findAll();
        return ResponseEntity.ok(jugadoresEquiposTorneo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorEquipoTorneo> findById(@PathVariable Long id) {
        return jugadorEquipoTorneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/jugador/{jugadorId}/torneo/{torneoId}")
    public ResponseEntity<JugadorEquipoTorneo> findByJugadorIdAndTorneoId(@PathVariable Long jugadorId, @PathVariable Long torneoId) {
        return jugadorEquipoTorneoService.findByJugadorIdAndTorneoId(jugadorId, torneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/equipo-torneo/{equipoTorneoId}/activos")
    public ResponseEntity<List<JugadorEquipoTorneo>> findByEquipoTorneoIdAndActivoTrue(@PathVariable Long equipoTorneoId) {
        List<JugadorEquipoTorneo> jugadoresEquiposTorneo = jugadorEquipoTorneoService.findByEquipoTorneoIdAndActivoTrue(equipoTorneoId);
        return ResponseEntity.ok(jugadoresEquiposTorneo);
    }

    @GetMapping("/equipo-torneo/{equipoTorneoId}")
    public ResponseEntity<List<JugadorEquipoTorneo>> findByEquipoTorneoId(@PathVariable Long equipoTorneoId) {
        List<JugadorEquipoTorneo> jugadoresEquiposTorneo = jugadorEquipoTorneoService.findByEquipoTorneoId(equipoTorneoId);
        return ResponseEntity.ok(jugadoresEquiposTorneo);
    }

    @GetMapping("/equipo-torneo/{equipoTorneoId}/count-activos")
    public ResponseEntity<Integer> countJugadoresActivosPorEquipoTorneo(@PathVariable Long equipoTorneoId) {
        Integer count = jugadorEquipoTorneoService.countJugadoresActivosPorEquipoTorneo(equipoTorneoId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<JugadorEquipoTorneo> save(@RequestBody JugadorEquipoTorneo jugadorEquipoTorneo) {
        try {
            JugadorEquipoTorneo savedJugadorEquipoTorneo = jugadorEquipoTorneoService.save(jugadorEquipoTorneo);
            return ResponseEntity.ok(savedJugadorEquipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorEquipoTorneo> update(@PathVariable Long id, @RequestBody JugadorEquipoTorneo jugadorEquipoTorneo) {
        try {
            JugadorEquipoTorneo updatedJugadorEquipoTorneo = jugadorEquipoTorneoService.update(id, jugadorEquipoTorneo);
            return ResponseEntity.ok(updatedJugadorEquipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/desvincular")
    public ResponseEntity<JugadorEquipoTorneo> desvincularJugador(@PathVariable Long id) {
        try {
            JugadorEquipoTorneo jugadorEquipoTorneo = jugadorEquipoTorneoService.desvincularJugador(id);
            return ResponseEntity.ok(jugadorEquipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/vincular")
    public ResponseEntity<JugadorEquipoTorneo> vincularJugador(@PathVariable Long id) {
        try {
            JugadorEquipoTorneo jugadorEquipoTorneo = jugadorEquipoTorneoService.vincularJugador(id);
            return ResponseEntity.ok(jugadorEquipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            jugadorEquipoTorneoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/jugador/{jugadorId}/torneo/{torneoId}")
    public ResponseEntity<Boolean> existsByJugadorIdAndTorneoId(@PathVariable Long jugadorId, @PathVariable Long torneoId) {
        boolean exists = jugadorEquipoTorneoService.existsByJugadorIdAndTorneoId(jugadorId, torneoId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validar/inscripcion/jugador/{jugadorId}/torneo/{torneoId}/equipo-torneo/{equipoTorneoId}")
    public ResponseEntity<Boolean> validarInscripcionJugador(@PathVariable Long jugadorId, @PathVariable Long torneoId, @PathVariable Long equipoTorneoId) {
        try {
            boolean puedeInscribirse = jugadorEquipoTorneoService.validarInscripcionJugador(jugadorId, torneoId, equipoTorneoId);
            return ResponseEntity.ok(puedeInscribirse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validar/limite-jugadores/equipo-torneo/{equipoTorneoId}/torneo/{torneoId}")
    public ResponseEntity<Boolean> validarLimiteJugadores(@PathVariable Long equipoTorneoId, @PathVariable Long torneoId) {
        boolean puedeInscribirse = jugadorEquipoTorneoService.validarLimiteJugadores(equipoTorneoId, torneoId);
        return ResponseEntity.ok(puedeInscribirse);
    }

    /**
     * Endpoint para inscribir un jugador a un equipo en un torneo.
     * Busca el jugador por documento, lo crea si no existe, y valida reglas de negocio.
     */
    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirJugador(@RequestBody InscripcionJugadorDTO dto) {
        try {
            Map<String, Object> resultado = jugadorEquipoTorneoService.inscribirJugadorCompleto(dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Busca un jugador por documento de identidad.
     * Retorna 200 si existe, 404 si no existe.
     */
    @GetMapping("/buscar-jugador/{documentoIdentidad}")
    public ResponseEntity<?> buscarJugadorPorDocumento(@PathVariable String documentoIdentidad) {
        Optional<Map<String, Object>> jugador = jugadorEquipoTorneoService.buscarJugadorPorDocumento(documentoIdentidad);
        
        if (jugador.isPresent()) {
            return ResponseEntity.ok(jugador.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Jugador no encontrado con documento: " + documentoIdentidad));
        }
    }
}
