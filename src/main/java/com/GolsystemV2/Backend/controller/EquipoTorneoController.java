package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.service.EquipoTorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos-torneo")
public class EquipoTorneoController {

    @Autowired
    private EquipoTorneoService equipoTorneoService;

    @GetMapping
    public ResponseEntity<List<EquipoTorneo>> findAll() {
        List<EquipoTorneo> equiposTorneo = equipoTorneoService.findAll();
        return ResponseEntity.ok(equiposTorneo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoTorneo> findById(@PathVariable Long id) {
        return equipoTorneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/equipo/{equipoId}")
    public ResponseEntity<EquipoTorneo> findByTorneoIdAndEquipoId(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        return equipoTorneoService.findByTorneoIdAndEquipoId(torneoId, equipoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/activos")
    public ResponseEntity<List<EquipoTorneo>> findByTorneoIdAndEliminadoFalse(@PathVariable Long torneoId) {
        List<EquipoTorneo> equiposTorneo = equipoTorneoService.findByTorneoIdAndEliminadoFalse(torneoId);
        return ResponseEntity.ok(equiposTorneo);
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<EquipoTorneo>> findByTorneoId(@PathVariable Long torneoId) {
        List<EquipoTorneo> equiposTorneo = equipoTorneoService.findByTorneoId(torneoId);
        return ResponseEntity.ok(equiposTorneo);
    }

    @GetMapping("/torneo/{torneoId}/count-activos")
    public ResponseEntity<Integer> countEquiposActivosPorTorneo(@PathVariable Long torneoId) {
        Integer count = equipoTorneoService.countEquiposActivosPorTorneo(torneoId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody EquipoTorneo equipoTorneo) {
        try {
            EquipoTorneo savedEquipoTorneo = equipoTorneoService.save(equipoTorneo);
            return ResponseEntity.ok(savedEquipoTorneo);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Equipo duplicado");
            error.put("message", "El equipo ya está inscrito en este torneo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de validación");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoTorneo> update(@PathVariable Long id, @RequestBody EquipoTorneo equipoTorneo) {
        try {
            EquipoTorneo updatedEquipoTorneo = equipoTorneoService.update(id, equipoTorneo);
            return ResponseEntity.ok(updatedEquipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/eliminar")
    public ResponseEntity<EquipoTorneo> eliminarEquipoDelTorneo(@PathVariable Long id) {
        try {
            EquipoTorneo equipoTorneo = equipoTorneoService.eliminarEquipoDelTorneo(id);
            return ResponseEntity.ok(equipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<EquipoTorneo> reactivarEquipoEnTorneo(@PathVariable Long id) {
        try {
            EquipoTorneo equipoTorneo = equipoTorneoService.reactivarEquipoEnTorneo(id);
            return ResponseEntity.ok(equipoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            equipoTorneoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/torneo/{torneoId}/equipo/{equipoId}")
    public ResponseEntity<Boolean> existsByTorneoIdAndEquipoId(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        boolean exists = equipoTorneoService.existsByTorneoIdAndEquipoId(torneoId, equipoId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validar/inscripcion/torneo/{torneoId}/equipo/{equipoId}")
    public ResponseEntity<Boolean> validarInscripcionEquipo(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        try {
            boolean puedeInscribirse = equipoTorneoService.validarInscripcionEquipo(torneoId, equipoId);
            return ResponseEntity.ok(puedeInscribirse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirEquipoEnTorneo(@RequestParam Long torneoId, @RequestParam Long equipoId) {
        try {
            EquipoTorneo savedEquipoTorneo = equipoTorneoService.inscribirEquipoEnTorneo(torneoId, equipoId);
            return ResponseEntity.ok(savedEquipoTorneo);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Equipo duplicado");
            error.put("message", "El equipo ya está inscrito en este torneo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de validación");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
