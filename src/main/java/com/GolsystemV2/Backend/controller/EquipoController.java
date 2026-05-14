package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.EquipoRequestDTO;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Equipo> equipos = equipoService.findAll();
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("message", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
    public ResponseEntity<?> save(@RequestBody EquipoRequestDTO equipoDTO) {
        try {
            Equipo savedEquipo = equipoService.saveWithTorneo(equipoDTO);
            return ResponseEntity.ok(savedEquipo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de validación");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
