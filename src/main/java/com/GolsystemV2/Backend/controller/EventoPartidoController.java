package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.enums.TipoEvento;
import com.GolsystemV2.Backend.service.EventoPartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos-partido")
@CrossOrigin(origins = "*")
public class EventoPartidoController {

    @Autowired
    private EventoPartidoService eventoPartidoService;

    @GetMapping
    public ResponseEntity<List<EventoPartido>> findAll() {
        List<EventoPartido> eventosPartido = eventoPartidoService.findAll();
        return ResponseEntity.ok(eventosPartido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoPartido> findById(@PathVariable Long id) {
        return eventoPartidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/encuentro/{encuentroId}")
    public ResponseEntity<List<EventoPartido>> findByEncuentroIdOrderByMinuto(@PathVariable Long encuentroId) {
        List<EventoPartido> eventosPartido = eventoPartidoService.findByEncuentroIdOrderByMinuto(encuentroId);
        return ResponseEntity.ok(eventosPartido);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}")
    public ResponseEntity<List<EventoPartido>> findByJugadorEquipoTorneoIdOrderByMinuto(@PathVariable Long jugadorEquipoTorneoId) {
        List<EventoPartido> eventosPartido = eventoPartidoService.findByJugadorEquipoTorneoIdOrderByMinuto(jugadorEquipoTorneoId);
        return ResponseEntity.ok(eventosPartido);
    }

    @GetMapping("/encuentro/{encuentroId}/tipo/{tipoEvento}")
    public ResponseEntity<List<EventoPartido>> findByEncuentroIdAndTipoEvento(@PathVariable Long encuentroId, @PathVariable TipoEvento tipoEvento) {
        List<EventoPartido> eventosPartido = eventoPartidoService.findByEncuentroIdAndTipoEvento(encuentroId, tipoEvento);
        return ResponseEntity.ok(eventosPartido);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/goles")
    public ResponseEntity<Integer> countGolesPorJugador(@PathVariable Long jugadorEquipoTorneoId) {
        Integer goles = eventoPartidoService.countGolesPorJugador(jugadorEquipoTorneoId);
        return ResponseEntity.ok(goles);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/amarillas")
    public ResponseEntity<Integer> countAmarillasPorJugador(@PathVariable Long jugadorEquipoTorneoId) {
        Integer amarillas = eventoPartidoService.countAmarillasPorJugador(jugadorEquipoTorneoId);
        return ResponseEntity.ok(amarillas);
    }

    @GetMapping("/jugador-equipo-torneo/{jugadorEquipoTorneoId}/rojas")
    public ResponseEntity<Integer> countRojasPorJugador(@PathVariable Long jugadorEquipoTorneoId) {
        Integer rojas = eventoPartidoService.countRojasPorJugador(jugadorEquipoTorneoId);
        return ResponseEntity.ok(rojas);
    }

    @GetMapping("/equipo-torneo/{equipoTorneoId}")
    public ResponseEntity<List<EventoPartido>> findEventosPorEquipo(@PathVariable Long equipoTorneoId) {
        List<EventoPartido> eventosPartido = eventoPartidoService.findEventosPorEquipo(equipoTorneoId);
        return ResponseEntity.ok(eventosPartido);
    }

    @PostMapping
    public ResponseEntity<EventoPartido> save(@RequestBody EventoPartido eventoPartido) {
        try {
            EventoPartido savedEventoPartido = eventoPartidoService.save(eventoPartido);
            return ResponseEntity.ok(savedEventoPartido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoPartido> update(@PathVariable Long id, @RequestBody EventoPartido eventoPartido) {
        try {
            EventoPartido updatedEventoPartido = eventoPartidoService.update(id, eventoPartido);
            return ResponseEntity.ok(updatedEventoPartido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar-gol")
    public ResponseEntity<EventoPartido> registrarGol(
            @RequestParam Long encuentroId,
            @RequestParam Long jugadorEquipoTorneoId,
            @RequestParam Long equipoTorneoId,
            @RequestParam Integer minuto) {
        try {
            EventoPartido evento = eventoPartidoService.registrarGol(encuentroId, jugadorEquipoTorneoId, equipoTorneoId, minuto);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar-amarilla")
    public ResponseEntity<EventoPartido> registrarTarjetaAmarilla(
            @RequestParam Long encuentroId,
            @RequestParam Long jugadorEquipoTorneoId,
            @RequestParam Long equipoTorneoId,
            @RequestParam Integer minuto) {
        try {
            EventoPartido evento = eventoPartidoService.registrarTarjetaAmarilla(encuentroId, jugadorEquipoTorneoId, equipoTorneoId, minuto);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar-roja")
    public ResponseEntity<EventoPartido> registrarTarjetaRoja(
            @RequestParam Long encuentroId,
            @RequestParam Long jugadorEquipoTorneoId,
            @RequestParam Long equipoTorneoId,
            @RequestParam Integer minuto) {
        try {
            EventoPartido evento = eventoPartidoService.registrarTarjetaRoja(encuentroId, jugadorEquipoTorneoId, equipoTorneoId, minuto);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/encuentro/{encuentroId}/validar-registro")
    public ResponseEntity<Boolean> validarRegistroEvento(@PathVariable Long encuentroId) {
        boolean puedeRegistrar = eventoPartidoService.validarRegistroEvento(encuentroId);
        return ResponseEntity.ok(puedeRegistrar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            eventoPartidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
