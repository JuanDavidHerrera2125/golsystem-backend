package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.service.JugadorEquipoTorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores-equipos-torneo")
@CrossOrigin(origins = "*")
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
}
