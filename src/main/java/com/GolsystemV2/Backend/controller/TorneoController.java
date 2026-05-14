package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.service.TorneoService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private GoleadorHistoricoService goleadorHistoricoService;

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
    public ResponseEntity<?> save(@RequestBody Torneo torneo) {
        try {
            Torneo savedTorneo = torneoService.save(torneo);
            return ResponseEntity.ok(savedTorneo);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de integridad de datos");
            error.put("message", "No se pudo guardar el torneo debido a una violación de restricción de datos. Verifique que los campos obligatorios estén completos y que no existan duplicados.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de validación");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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

    @PostMapping("/{id}/generar-grupos")
    public ResponseEntity<?> distribuirEquiposEnGrupos(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "SOLO_IDA") FormatoEncuentro formato) {
        try {
            Torneo torneo = torneoService.distribuirEquiposEnGrupos(id, formato);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Equipos distribuidos exitosamente en grupos");
            response.put("torneoId", torneo.getId());
            response.put("cantidadGrupos", torneo.getCantidadDeGrupos());
            response.put("formato", formato);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de distribución");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/grupos")
    public ResponseEntity<?> obtenerGrupos(@PathVariable Long id) {
        try {
            List<Map<String, Object>> grupos = torneoService.obtenerGruposConEquipos(id);
            return ResponseEntity.ok(grupos);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener grupos");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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

    @GetMapping("/{id}/fases")
    public ResponseEntity<?> obtenerFases(@PathVariable Long id) {
        try {
            List<Map<String, Object>> fases = torneoService.obtenerFases(id);
            return ResponseEntity.ok(fases);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener fases");
            error.put("message", e.getMessage());
            // Si el mensaje indica que el torneo no fue encontrado, devolver 404
            if (e.getMessage() != null && e.getMessage().contains("Torneo no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/encuentros")
    public ResponseEntity<?> obtenerEncuentros(@PathVariable Long id) {
        try {
            List<Map<String, Object>> encuentros = torneoService.obtenerEncuentros(id);
            return ResponseEntity.ok(encuentros);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener encuentros");
            error.put("message", e.getMessage());
            // Si el mensaje indica que el torneo no fue encontrado, devolver 404
            if (e.getMessage() != null && e.getMessage().contains("Torneo no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable Long id) {
        try {
            Map<String, Object> estadisticas = torneoService.obtenerEstadisticas(id);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas");
            error.put("message", e.getMessage());
            // Si el mensaje indica que el torneo no fue encontrado, devolver 404
            if (e.getMessage() != null && e.getMessage().contains("Torneo no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/goleadores")
    public ResponseEntity<?> obtenerGoleadores(@PathVariable Long id) {
        try {
            var goleadores = goleadorHistoricoService.findTablaGoleadoresPorTorneo(id);
            return ResponseEntity.ok(goleadores);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener goleadores");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/encuentros/activos")
    public ResponseEntity<?> obtenerEncuentrosActivos(@PathVariable Long id) {
        try {
            var encuentros = torneoService.obtenerEncuentrosActivos(id);
            return ResponseEntity.ok(encuentros);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener encuentros activos");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
