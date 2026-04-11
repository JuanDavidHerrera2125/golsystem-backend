package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.service.FaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fases")
@CrossOrigin(origins = "*")
public class FaseController {

    @Autowired
    private FaseService faseService;

    @GetMapping
    public ResponseEntity<List<Fase>> findAll() {
        List<Fase> fases = faseService.findAll();
        return ResponseEntity.ok(fases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fase> findById(@PathVariable Long id) {
        return faseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/numero/{numeroFase}")
    public ResponseEntity<Fase> findByTorneoIdAndNumeroFase(@PathVariable Long torneoId, @PathVariable Integer numeroFase) {
        return faseService.findByTorneoIdAndNumeroFase(torneoId, numeroFase)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Fase>> findByTorneoIdOrderByNumeroFase(@PathVariable Long torneoId) {
        List<Fase> fases = faseService.findByTorneoIdOrderByNumeroFase(torneoId);
        return ResponseEntity.ok(fases);
    }

    @GetMapping("/torneo/{torneoId}/abiertas")
    public ResponseEntity<List<Fase>> findByTorneoIdAndEstaCerradaFalse(@PathVariable Long torneoId) {
        List<Fase> fases = faseService.findByTorneoIdAndEstaCerradaFalse(torneoId);
        return ResponseEntity.ok(fases);
    }

    @GetMapping("/torneo/{torneoId}/max-numero")
    public ResponseEntity<Integer> findMaxNumeroFaseByTorneoId(@PathVariable Long torneoId) {
        Integer maxNumero = faseService.findMaxNumeroFaseByTorneoId(torneoId);
        return ResponseEntity.ok(maxNumero);
    }

    @PostMapping
    public ResponseEntity<Fase> save(@RequestBody Fase fase) {
        try {
            Fase savedFase = faseService.save(fase);
            return ResponseEntity.ok(savedFase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fase> update(@PathVariable Long id, @RequestBody Fase fase) {
        try {
            Fase updatedFase = faseService.update(id, fase);
            return ResponseEntity.ok(updatedFase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Fase> cerrarFase(@PathVariable Long id) {
        try {
            Fase fase = faseService.cerrarFase(id);
            return ResponseEntity.ok(fase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/torneo/{torneoId}/siguiente/{numeroFase}")
    public ResponseEntity<Fase> crearSiguienteFase(@PathVariable Long torneoId, @PathVariable Integer numeroFase) {
        try {
            Fase fase = faseService.crearSiguienteFase(torneoId, numeroFase);
            return ResponseEntity.ok(fase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/validar-cierre")
    public ResponseEntity<Boolean> validarCierreFase(@PathVariable Long id) {
        try {
            boolean puedeCerrar = faseService.validarCierreFase(id);
            return ResponseEntity.ok(puedeCerrar);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            faseService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
