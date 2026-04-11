package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import com.GolsystemV2.Backend.service.EncuentroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/encuentros")
@CrossOrigin(origins = "*")
public class EncuentroController {

    @Autowired
    private EncuentroService encuentroService;

    @GetMapping
    public ResponseEntity<List<Encuentro>> findAll() {
        List<Encuentro> encuentros = encuentroService.findAll();
        return ResponseEntity.ok(encuentros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encuentro> findById(@PathVariable Long id) {
        return encuentroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fase/{faseId}")
    public ResponseEntity<List<Encuentro>> findByFaseIdOrderByFecha(@PathVariable Long faseId) {
        List<Encuentro> encuentros = encuentroService.findByFaseIdOrderByFecha(faseId);
        return ResponseEntity.ok(encuentros);
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<Encuentro>> findByGrupoIdOrderByFecha(@PathVariable Long grupoId) {
        List<Encuentro> encuentros = encuentroService.findByGrupoIdOrderByFecha(grupoId);
        return ResponseEntity.ok(encuentros);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Encuentro>> findByEquipoId(@PathVariable Long equipoId) {
        List<Encuentro> encuentros = encuentroService.findByEquipoId(equipoId);
        return ResponseEntity.ok(encuentros);
    }

    @GetMapping("/fase/{faseId}/estado/{estado}")
    public ResponseEntity<List<Encuentro>> findByFaseIdAndEstado(@PathVariable Long faseId, @PathVariable EstadoEncuentro estado) {
        List<Encuentro> encuentros = encuentroService.findByFaseIdAndEstado(faseId, estado);
        return ResponseEntity.ok(encuentros);
    }

    @GetMapping("/fecha-entre")
    public ResponseEntity<List<Encuentro>> findByFechaBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Encuentro> encuentros = encuentroService.findByFechaBetween(inicio, fin);
        return ResponseEntity.ok(encuentros);
    }

    @PostMapping
    public ResponseEntity<Encuentro> save(@RequestBody Encuentro encuentro) {
        Encuentro savedEncuentro = encuentroService.save(encuentro);
        return ResponseEntity.ok(savedEncuentro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Encuentro> update(@PathVariable Long id, @RequestBody Encuentro encuentro) {
        try {
            Encuentro updatedEncuentro = encuentroService.update(id, encuentro);
            return ResponseEntity.ok(updatedEncuentro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Encuentro> iniciarPartido(@PathVariable Long id) {
        try {
            Encuentro encuentro = encuentroService.iniciarPartido(id);
            return ResponseEntity.ok(encuentro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Encuentro> finalizarPartido(
            @PathVariable Long id,
            @RequestParam Integer golesLocal,
            @RequestParam Integer golesVisitante) {
        try {
            Encuentro encuentro = encuentroService.finalizarPartido(id, golesLocal, golesVisitante);
            return ResponseEntity.ok(encuentro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/validar-modificacion")
    public ResponseEntity<Boolean> validarModificacionPartido(@PathVariable Long id) {
        boolean puedeModificar = encuentroService.validarModificacionPartido(id);
        return ResponseEntity.ok(puedeModificar);
    }

    @GetMapping("/{id}/validar-finalizacion")
    public ResponseEntity<Boolean> validarFinalizacionPartido(@PathVariable Long id) {
        boolean puedeFinalizar = encuentroService.validarFinalizacionPartido(id);
        return ResponseEntity.ok(puedeFinalizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            encuentroService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
