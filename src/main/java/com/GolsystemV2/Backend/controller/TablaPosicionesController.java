package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
import com.GolsystemV2.Backend.service.TablaPosicionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tablas-posiciones")
@CrossOrigin(origins = "*")
public class TablaPosicionesController {

    @Autowired
    private TablaPosicionesService tablaPosicionesService;

    @GetMapping
    public ResponseEntity<List<TablaPosiciones>> findAll() {
        List<TablaPosiciones> tablasPosiciones = tablaPosicionesService.findAll();
        return ResponseEntity.ok(tablasPosiciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablaPosiciones> findById(@PathVariable Long id) {
        return tablaPosicionesService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/grupo/{grupoId}/equipo-torneo/{equipoTorneoId}")
    public ResponseEntity<TablaPosiciones> findByGrupoIdAndEquipoTorneoId(@PathVariable Long grupoId, @PathVariable Long equipoTorneoId) {
        return tablaPosicionesService.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<TablaPosiciones>> findByGrupoIdOrderByIdDesc(@PathVariable Long grupoId) {
        List<TablaPosiciones> tablasPosiciones = tablaPosicionesService.findByGrupoIdOrderByIdDesc(grupoId);
        return ResponseEntity.ok(tablasPosiciones);
    }

    @GetMapping("/grupo/{grupoId}/ordenada")
    public ResponseEntity<List<TablaPosiciones>> findTablaPosicionesOrdenada(@PathVariable Long grupoId) {
        List<TablaPosiciones> tablasPosiciones = tablaPosicionesService.findTablaPosicionesOrdenada(grupoId);
        return ResponseEntity.ok(tablasPosiciones);
    }

    @GetMapping("/grupo/{grupoId}/estado/{estado}")
    public ResponseEntity<List<TablaPosiciones>> findByGrupoIdAndEstado(@PathVariable Long grupoId, @PathVariable EstadoTablaPosiciones estado) {
        List<TablaPosiciones> tablasPosiciones = tablaPosicionesService.findByGrupoIdAndEstado(grupoId, estado);
        return ResponseEntity.ok(tablasPosiciones);
    }

    @PostMapping
    public ResponseEntity<TablaPosiciones> save(@RequestBody TablaPosiciones tablaPosiciones) {
        TablaPosiciones savedTablaPosiciones = tablaPosicionesService.save(tablaPosiciones);
        return ResponseEntity.ok(savedTablaPosiciones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TablaPosiciones> update(@PathVariable Long id, @RequestBody TablaPosiciones tablaPosiciones) {
        try {
            TablaPosiciones updatedTablaPosiciones = tablaPosicionesService.update(id, tablaPosiciones);
            return ResponseEntity.ok(updatedTablaPosiciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<TablaPosiciones> cerrarTabla(@PathVariable Long id) {
        try {
            TablaPosiciones tablaPosiciones = tablaPosicionesService.cerrarTabla(id);
            return ResponseEntity.ok(tablaPosiciones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/abrir")
    public ResponseEntity<TablaPosiciones> abrirTabla(@PathVariable Long id) {
        try {
            TablaPosiciones tablaPosiciones = tablaPosicionesService.abrirTabla(id);
            return ResponseEntity.ok(tablaPosiciones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/grupo/{grupoId}/equipo-torneo/{equipoTorneoId}/actualizar-estadisticas")
    public ResponseEntity<Void> actualizarEstadisticas(
            @PathVariable Long grupoId,
            @PathVariable Long equipoTorneoId,
            @RequestParam Integer pg,
            @RequestParam Integer pe,
            @RequestParam Integer pp,
            @RequestParam Integer gf,
            @RequestParam Integer gc,
            @RequestParam Integer amarillas,
            @RequestParam Integer rojas) {
        tablaPosicionesService.actualizarEstadisticas(grupoId, equipoTorneoId, pg, pe, pp, gf, gc, amarillas, rojas);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            tablaPosicionesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
