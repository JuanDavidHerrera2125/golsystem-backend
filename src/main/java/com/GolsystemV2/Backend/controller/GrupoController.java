package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping
    public ResponseEntity<List<Grupo>> findAll() {
        List<Grupo> grupos = grupoService.findAll();
        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupo> findById(@PathVariable Long id) {
        return grupoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fase/{faseId}/nombre/{nombreGrupo}")
    public ResponseEntity<Grupo> findByFaseIdAndNombreGrupo(@PathVariable Long faseId, @PathVariable String nombreGrupo) {
        return grupoService.findByFaseIdAndNombreGrupo(faseId, nombreGrupo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fase/{faseId}")
    public ResponseEntity<List<Grupo>> findByFaseIdOrderByNombreGrupo(@PathVariable Long faseId) {
        List<Grupo> grupos = grupoService.findByFaseIdOrderByNombreGrupo(faseId);
        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/fase/{faseId}/todos")
    public ResponseEntity<List<Grupo>> findByFaseId(@PathVariable Long faseId) {
        List<Grupo> grupos = grupoService.findByFaseId(faseId);
        return ResponseEntity.ok(grupos);
    }

    @PostMapping
    public ResponseEntity<Grupo> save(@RequestBody Grupo grupo) {
        try {
            Grupo savedGrupo = grupoService.save(grupo);
            return ResponseEntity.ok(savedGrupo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupo> update(@PathVariable Long id, @RequestBody Grupo grupo) {
        try {
            Grupo updatedGrupo = grupoService.update(id, grupo);
            return ResponseEntity.ok(updatedGrupo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            grupoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/fase/{faseId}/nombre/{nombreGrupo}")
    public ResponseEntity<Boolean> existsByFaseIdAndNombreGrupo(@PathVariable Long faseId, @PathVariable String nombreGrupo) {
        boolean exists = grupoService.existsByFaseIdAndNombreGrupo(faseId, nombreGrupo);
        return ResponseEntity.ok(exists);
    }
}
