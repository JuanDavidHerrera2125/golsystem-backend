package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.FaseConfigDto;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoFase;
import com.GolsystemV2.Backend.service.FaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FaseController — REST API para la gestión de fases del torneo.
 *
 * Endpoints de ciclo de vida (delegados a PhaseManagerService):
 *   POST /api/fases/configurar               → Crea y configura una fase nueva
 *   POST /api/fases/{id}/activar             → CONFIGURACION → EN_CURSO
 *   POST /api/fases/{id}/cerrar              → EN_CURSO → FINALIZADA (+ dispara históricos)
 *   POST /api/fases/torneo/{torneoId}/avanzar → Crea la Fase N+1 en CONFIGURACION
 *   GET  /api/fases/{id}/estado              → FaseEstadoDto con métricas en tiempo real
 *   GET  /api/fases/{id}/puede-activarse     → boolean
 */
@RestController
@RequestMapping("/api/fases")
public class FaseController {

    @Autowired
    private FaseService faseService;

    // ─── CRUD BÁSICO ──────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Fase>> findAll() {
        return ResponseEntity.ok(faseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fase> findById(@PathVariable Long id) {
        return faseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}/numero/{numeroFase}")
    public ResponseEntity<Fase> findByTorneoIdAndNumeroFase(
            @PathVariable Long torneoId,
            @PathVariable Integer numeroFase) {
        return faseService.findByTorneoIdAndNumeroFase(torneoId, numeroFase)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Fase>> findByTorneoIdOrderByNumeroFase(@PathVariable Long torneoId) {
        return ResponseEntity.ok(faseService.findByTorneoIdOrderByNumeroFase(torneoId));
    }

    @GetMapping("/torneo/{torneoId}/estado/{estadoFase}")
    public ResponseEntity<List<Fase>> findByTorneoIdAndEstadoFase(
            @PathVariable Long torneoId,
            @PathVariable EstadoFase estadoFase) {
        return ResponseEntity.ok(faseService.findByTorneoIdAndEstadoFase(torneoId, estadoFase));
    }

    @GetMapping("/torneo/{torneoId}/max-numero")
    public ResponseEntity<Integer> findMaxNumeroFaseByTorneoId(@PathVariable Long torneoId) {
        return ResponseEntity.ok(faseService.findMaxNumeroFaseByTorneoId(torneoId));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Fase fase) {
        try {
            return ResponseEntity.ok(faseService.save(fase));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Fase fase) {
        try {
            return ResponseEntity.ok(faseService.update(id, fase));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            faseService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── CICLO DE VIDA — PhaseManagerService ─────────────────────────────────

    /**
     * Crea y configura una nueva fase para un torneo.
     * Valida progresión lineal, límite de grupos (1-15) y equipos disponibles.
     *
     * Body: FaseConfigDto
     * Returns: FaseEstadoDto
     */
    @PostMapping("/configurar")
    public ResponseEntity<?> configurarFase(@RequestBody FaseConfigDto dto) {
        try {
            FaseEstadoDto resultado = faseService.configurarFase(dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Activa una fase: distribuye equipos en grupos con Snake Draft,
     * inicializa tablas de posiciones, cambia estado a EN_CURSO.
     * Desde este momento la fase está BLOQUEADA para cambios estructurales.
     */
    @PostMapping("/{id}/activar")
    public ResponseEntity<?> activarFase(@PathVariable Long id) {
        try {
            FaseEstadoDto resultado = faseService.activarFase(id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cierra una fase:
     *  1. Valida que TODOS los encuentros están FINALIZADOS.
     *  2. Dispara el evento de histórico (GoleadorHistorico, CampeonHistorico si es la última).
     *  3. Marca la fase como FINALIZADA.
     */
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarFase(@PathVariable Long id) {
        try {
            FaseEstadoDto resultado = faseService.cerrarFase(id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Avanza al siguiente número de fase (N+1).
     * Requiere que la fase actual esté FINALIZADA (progresión lineal obligatoria).
     * Crea la nueva fase en estado CONFIGURACION.
     */
    @PostMapping("/torneo/{torneoId}/avanzar")
    public ResponseEntity<?> avanzarSiguienteFase(@PathVariable Long torneoId) {
        try {
            FaseEstadoDto resultado = faseService.avanzarSiguienteFase(torneoId);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Devuelve el estado enriquecido de la fase con métricas en tiempo real:
     * total equipos, partidos finalizados, porcentaje de avance, flags de control.
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<?> obtenerEstadoFase(@PathVariable Long id) {
        try {
            FaseEstadoDto resultado = faseService.obtenerEstadoFase(id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verifica si la fase cumple todas las condiciones para ser activada.
     */
    @GetMapping("/{id}/puede-activarse")
    public ResponseEntity<Boolean> puedeActivarFase(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(faseService.puedeActivarFase(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @deprecated Usar POST /{id}/cerrar que provee FaseEstadoDto completo.
     */
    @Deprecated
    @GetMapping("/{id}/validar-cierre")
    public ResponseEntity<Boolean> validarCierreFase(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(faseService.validarCierreFase(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
