package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Sancion;
import com.GolsystemV2.Backend.service.SancionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanciones")
@CrossOrigin(origins = "*")
public class SancionController {

    @Autowired
    private SancionService sancionService;

    @GetMapping
    public ResponseEntity<List<Sancion>> findAll() {
        List<Sancion> sanciones = sancionService.findAll();
        return ResponseEntity.ok(sanciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sancion> findById(@PathVariable Long id) {
        return sancionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/activas")
    public ResponseEntity<List<Sancion>> findByTorneoIdAndActivaTrue(@PathVariable Long torneoId) {
        List<Sancion> sanciones = sancionService.findByTorneoIdAndActivaTrue(torneoId);
        return ResponseEntity.ok(sanciones);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/activas")
    public ResponseEntity<List<Sancion>> findByJugadorEquipoTorneoIdAndActivaTrue(@PathVariable Long jugadorEquipoTorneoId) {
        List<Sancion> sanciones = sancionService.findByJugadorEquipoTorneoIdAndActivaTrue(jugadorEquipoTorneoId);
        return ResponseEntity.ok(sanciones);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}")
    public ResponseEntity<List<Sancion>> findByJugadorEquipoTorneoId(@PathVariable Long jugadorEquipoTorneoId) {
        List<Sancion> sanciones = sancionService.findByJugadorEquipoTorneoId(jugadorEquipoTorneoId);
        return ResponseEntity.ok(sanciones);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/torneo/{torneoId}/count-activas")
    public ResponseEntity<Integer> countSancionesActivasPorJugadorTorneo(@PathVariable Long jugadorEquipoTorneoId, @PathVariable Long torneoId) {
        Integer count = sancionService.countSancionesActivasPorJugadorTorneo(jugadorEquipoTorneoId, torneoId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<Sancion> save(@RequestBody Sancion sancion) {
        Sancion savedSancion = sancionService.save(sancion);
        return ResponseEntity.ok(savedSancion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sancion> update(@PathVariable Long id, @RequestBody Sancion sancion) {
        try {
            Sancion updatedSancion = sancionService.update(id, sancion);
            return ResponseEntity.ok(updatedSancion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<Sancion> crearSancion(
            @RequestParam Long torneoId,
            @RequestParam Long jugadorEquipoTorneoId,
            @RequestParam(required = false) Long encuentroOrigenId,
            @RequestParam Integer cantidadFechas) {
        try {
            Sancion sancion = sancionService.crearSancion(torneoId, jugadorEquipoTorneoId, encuentroOrigenId, cantidadFechas);
            return ResponseEntity.ok(sancion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/incrementar-fechas")
    public ResponseEntity<Sancion> incrementarFechasCumplidas(@PathVariable Long id) {
        try {
            Sancion sancion = sancionService.incrementarFechasCumplidas(id);
            return ResponseEntity.ok(sancion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Sancion> desactivarSancion(@PathVariable Long id) {
        try {
            Sancion sancion = sancionService.desactivarSancion(id);
            return ResponseEntity.ok(sancion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/torneo/{torneoId}/esta-sancionado")
    public ResponseEntity<Boolean> jugadorEstaSancionado(@PathVariable Long jugadorEquipoTorneoId, @PathVariable Long torneoId) {
        boolean estaSancionado = sancionService.jugadorEstaSancionado(jugadorEquipoTorneoId, torneoId);
        return ResponseEntity.ok(estaSancionado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            sancionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
