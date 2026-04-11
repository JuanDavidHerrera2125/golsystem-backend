package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<Equipo>> findAll() {
        List<Equipo> equipos = equipoService.findAll();
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> findById(@PathVariable Long id) {
        return equipoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoEquipo}")
    public ResponseEntity<Equipo> findByCodigoEquipo(@PathVariable String codigoEquipo) {
        return equipoService.findByCodigoEquipo(codigoEquipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Equipo>> findByActivoTrue() {
        List<Equipo> equipos = equipoService.findByActivoTrue();
        return ResponseEntity.ok(equipos);
    }

    @PostMapping
    public ResponseEntity<Equipo> save(@RequestBody Equipo equipo) {
        try {
            Equipo savedEquipo = equipoService.save(equipo);
            return ResponseEntity.ok(savedEquipo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> update(@PathVariable Long id, @RequestBody Equipo equipo) {
        try {
            Equipo updatedEquipo = equipoService.update(id, equipo);
            return ResponseEntity.ok(updatedEquipo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Equipo> desactivarEquipo(@PathVariable Long id) {
        try {
            Equipo equipo = equipoService.desactivarEquipo(id);
            return ResponseEntity.ok(equipo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Equipo> activarEquipo(@PathVariable Long id) {
        try {
            Equipo equipo = equipoService.activarEquipo(id);
            return ResponseEntity.ok(equipo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            equipoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/codigo/{codigoEquipo}")
    public ResponseEntity<Boolean> existsByCodigoEquipo(@PathVariable String codigoEquipo) {
        boolean exists = equipoService.existsByCodigoEquipo(codigoEquipo);
        return ResponseEntity.ok(exists);
    }
}
