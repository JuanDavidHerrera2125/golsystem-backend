package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
@CrossOrigin(origins = "*")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping
    public ResponseEntity<List<Torneo>> findAll() {
        List<Torneo> torneos = torneoService.findAll();
        return ResponseEntity.ok(torneos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> findById(@PathVariable Long id) {
        return torneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Torneo> findByNombre(@PathVariable String nombre) {
        return torneoService.findByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Torneo>> findByEstado(@PathVariable EstadoTorneo estado) {
        List<Torneo> torneos = torneoService.findByEstado(estado);
        return ResponseEntity.ok(torneos);
    }

    @GetMapping("/configuracion")
    public ResponseEntity<List<Torneo>> findTorneosEnConfiguracion() {
        List<Torneo> torneos = torneoService.findTorneosEnConfiguracion();
        return ResponseEntity.ok(torneos);
    }

    @GetMapping("/curso")
    public ResponseEntity<List<Torneo>> findTorneosEnCurso() {
        List<Torneo> torneos = torneoService.findTorneosEnCurso();
        return ResponseEntity.ok(torneos);
    }

    @PostMapping
    public ResponseEntity<Torneo> save(@RequestBody Torneo torneo) {
        try {
            Torneo savedTorneo = torneoService.save(torneo);
            return ResponseEntity.ok(savedTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Torneo> update(@PathVariable Long id, @RequestBody Torneo torneo) {
        try {
            Torneo updatedTorneo = torneoService.update(id, torneo);
            return ResponseEntity.ok(updatedTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Torneo> iniciarTorneo(@PathVariable Long id) {
        try {
            Torneo torneo = torneoService.iniciarTorneo(id);
            return ResponseEntity.ok(torneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Torneo> finalizarTorneo(@PathVariable Long id) {
        try {
            Torneo torneo = torneoService.finalizarTorneo(id);
            return ResponseEntity.ok(torneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/validar-inicio")
    public ResponseEntity<Boolean> validarInicioTorneo(@PathVariable Long id) {
        boolean puedeIniciar = torneoService.validarInicioTorneo(id);
        return ResponseEntity.ok(puedeIniciar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            torneoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/nombre/{nombre}")
    public ResponseEntity<Boolean> existsByNombre(@PathVariable String nombre) {
        boolean exists = torneoService.existsByNombre(nombre);
        return ResponseEntity.ok(exists);
    }
}
