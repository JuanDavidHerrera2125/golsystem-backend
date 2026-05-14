package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.CampeonHistorico;
import com.GolsystemV2.Backend.service.CampeonHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campeones-historicos")
public class CampeonHistoricoController {

    @Autowired
    private CampeonHistoricoService campeonHistoricoService;

    @GetMapping
    public ResponseEntity<List<CampeonHistorico>> findAll() {
        List<CampeonHistorico> campeones = campeonHistoricoService.findAll();
        return ResponseEntity.ok(campeones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampeonHistorico> findById(@PathVariable Long id) {
        return campeonHistoricoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/equipo/{equipoId}")
    public ResponseEntity<CampeonHistorico> findByTorneoIdAndEquipoId(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        return campeonHistoricoService.findByTorneoIdAndEquipoId(torneoId, equipoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<CampeonHistorico>> findByTorneoIdOrderByFechaLogroDesc(@PathVariable Long torneoId) {
        List<CampeonHistorico> campeones = campeonHistoricoService.findByTorneoIdOrderByFechaLogroDesc(torneoId);
        return ResponseEntity.ok(campeones);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<CampeonHistorico>> findByEquipoIdOrderByFechaLogroDesc(@PathVariable Long equipoId) {
        List<CampeonHistorico> campeones = campeonHistoricoService.findByEquipoIdOrderByFechaLogroDesc(equipoId);
        return ResponseEntity.ok(campeones);
    }

    @GetMapping("/todos-ordenados")
    public ResponseEntity<List<CampeonHistorico>> findAllOrderByFechaLogroDesc() {
        List<CampeonHistorico> campeones = campeonHistoricoService.findAllOrderByFechaLogroDesc();
        return ResponseEntity.ok(campeones);
    }

    @GetMapping("/equipo/{equipoId}/count-campeonatos")
    public ResponseEntity<Integer> countCampeonatosPorEquipo(@PathVariable Long equipoId) {
        Integer count = campeonHistoricoService.countCampeonatosPorEquipo(equipoId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<CampeonHistorico> save(@RequestBody CampeonHistorico campeonHistorico) {
        CampeonHistorico savedCampeonHistorico = campeonHistoricoService.save(campeonHistorico);
        return ResponseEntity.ok(savedCampeonHistorico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampeonHistorico> update(@PathVariable Long id, @RequestBody CampeonHistorico campeonHistorico) {
        try {
            CampeonHistorico updatedCampeonHistorico = campeonHistoricoService.update(id, campeonHistorico);
            return ResponseEntity.ok(updatedCampeonHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar-campeon")
    public ResponseEntity<CampeonHistorico> registrarCampeon(
            @RequestParam Long torneoId,
            @RequestParam Long equipoId) {
        try {
            CampeonHistorico campeonHistorico = campeonHistoricoService.registrarCampeon(torneoId, equipoId);
            return ResponseEntity.ok(campeonHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists/torneo/{torneoId}/equipo/{equipoId}")
    public ResponseEntity<Boolean> existsByTorneoIdAndEquipoId(@PathVariable Long torneoId, @PathVariable Long equipoId) {
        boolean exists = campeonHistoricoService.existsByTorneoIdAndEquipoId(torneoId, equipoId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            campeonHistoricoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
