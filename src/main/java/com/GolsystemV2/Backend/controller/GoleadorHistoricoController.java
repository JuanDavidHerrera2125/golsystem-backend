package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goleadores-historicos")
@CrossOrigin(origins = "*")
public class GoleadorHistoricoController {

    @Autowired
    private GoleadorHistoricoService goleadorHistoricoService;

    @GetMapping
    public ResponseEntity<List<GoleadorHistorico>> findAll() {
        List<GoleadorHistorico> goleadores = goleadorHistoricoService.findAll();
        return ResponseEntity.ok(goleadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoleadorHistorico> findById(@PathVariable Long id) {
        return goleadorHistoricoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/jugador/{jugadorId}/torneo/{torneoId}")
    public ResponseEntity<GoleadorHistorico> findByJugadorIdAndTorneoId(@PathVariable Long jugadorId, @PathVariable Long torneoId) {
        return goleadorHistoricoService.findByJugadorIdAndTorneoId(jugadorId, torneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<GoleadorHistorico>> findByTorneoIdOrderByTotalGolesDesc(@PathVariable Long torneoId) {
        List<GoleadorHistorico> goleadores = goleadorHistoricoService.findByTorneoIdOrderByTotalGolesDesc(torneoId);
        return ResponseEntity.ok(goleadores);
    }

    @GetMapping("/jugador/{jugadorId}")
    public ResponseEntity<List<GoleadorHistorico>> findByJugadorIdOrderByTotalGolesDesc(@PathVariable Long jugadorId) {
        List<GoleadorHistorico> goleadores = goleadorHistoricoService.findByJugadorIdOrderByTotalGolesDesc(jugadorId);
        return ResponseEntity.ok(goleadores);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<GoleadorHistorico>> findByEquipoIdOrderByTotalGolesDesc(@PathVariable Long equipoId) {
        List<GoleadorHistorico> goleadores = goleadorHistoricoService.findByEquipoIdOrderByTotalGolesDesc(equipoId);
        return ResponseEntity.ok(goleadores);
    }

    @GetMapping("/torneo/{torneoId}/tabla-goleadores")
    public ResponseEntity<List<GoleadorHistorico>> findTablaGoleadoresPorTorneo(@PathVariable Long torneoId) {
        List<GoleadorHistorico> goleadores = goleadorHistoricoService.findTablaGoleadoresPorTorneo(torneoId);
        return ResponseEntity.ok(goleadores);
    }

    @GetMapping("/jugador/{jugadorId}/count-torneos")
    public ResponseEntity<Integer> countTorneosGoleador(@PathVariable Long jugadorId) {
        Integer count = goleadorHistoricoService.countTorneosGoleador(jugadorId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<GoleadorHistorico> save(@RequestBody GoleadorHistorico goleadorHistorico) {
        GoleadorHistorico savedGoleadorHistorico = goleadorHistoricoService.save(goleadorHistorico);
        return ResponseEntity.ok(savedGoleadorHistorico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoleadorHistorico> update(@PathVariable Long id, @RequestBody GoleadorHistorico goleadorHistorico) {
        try {
            GoleadorHistorico updatedGoleadorHistorico = goleadorHistoricoService.update(id, goleadorHistorico);
            return ResponseEntity.ok(updatedGoleadorHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/actualizar-goles")
    public ResponseEntity<GoleadorHistorico> actualizarGoles(
            @RequestParam Long jugadorId,
            @RequestParam Long equipoId,
            @RequestParam Long torneoId,
            @RequestParam Integer totalGoles) {
        try {
            GoleadorHistorico goleadorHistorico = goleadorHistoricoService.actualizarGoles(jugadorId, equipoId, torneoId, totalGoles);
            return ResponseEntity.ok(goleadorHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists/jugador/{jugadorId}/torneo/{torneoId}")
    public ResponseEntity<Boolean> existsByJugadorIdAndTorneoId(@PathVariable Long jugadorId, @PathVariable Long torneoId) {
        boolean exists = goleadorHistoricoService.existsByJugadorIdAndTorneoId(jugadorId, torneoId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            goleadorHistoricoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
