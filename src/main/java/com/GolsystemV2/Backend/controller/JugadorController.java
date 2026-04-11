package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
@CrossOrigin(origins = "*")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @GetMapping
    public ResponseEntity<List<Jugador>> findAll() {
        List<Jugador> jugadores = jugadorService.findAll();
        return ResponseEntity.ok(jugadores);
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
    public ResponseEntity<Jugador> save(@RequestBody Jugador jugador) {
        Jugador savedJugador = jugadorService.save(jugador);
        return ResponseEntity.ok(savedJugador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jugador> update(@PathVariable Long id, @RequestBody Jugador jugador) {
        try {
            Jugador updatedJugador = jugadorService.update(id, jugador);
            return ResponseEntity.ok(updatedJugador);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
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
}
