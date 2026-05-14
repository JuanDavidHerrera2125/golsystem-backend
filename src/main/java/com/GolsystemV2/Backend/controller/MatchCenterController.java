package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.EventoPartidoRequest;
import com.GolsystemV2.Backend.dto.CronometroResponse;
import com.GolsystemV2.Backend.entity.CronometroPartido;
import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.enums.EstadoCronometro;
import com.GolsystemV2.Backend.service.CronometroPartidoService;
import com.GolsystemV2.Backend.service.EventoPartidoService;
import com.GolsystemV2.Backend.service.SancionService;
import com.GolsystemV2.Backend.service.JugadorEquipoTorneoService;
import com.GolsystemV2.Backend.service.TablaPosicionesService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import com.GolsystemV2.Backend.enums.TipoEvento;
import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.JugadorRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para el Match Center - Gestión de partidos en tiempo real
 */
@RestController
@RequestMapping("/api/partidos")
public class MatchCenterController {

    @Autowired
    private CronometroPartidoService cronometroService;
    
    @Autowired
    private EventoPartidoService eventoService;
    
    @Autowired
    private SancionService sancionService;
    
    @Autowired
    private JugadorEquipoTorneoService jugadorEquipoTorneoService;
    
    @Autowired
    private EncuentroRepository encuentroRepository;
    
    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;
    
    @Autowired
    private TablaPosicionesService tablaPosicionesService;
    
    @Autowired
    private GoleadorHistoricoService goleadorHistoricoService;
    
    @Autowired
    private com.GolsystemV2.Backend.repository.EventoPartidoRepository eventoPartidoRepository;

    // ============ CRONOMETRO ============
    
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciarPartido(@PathVariable Long id) {
        try {
            CronometroPartido cronometro = cronometroService.iniciarPartido(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Partido iniciado",
                "estado", cronometro.getEstado(),
                "tiempo", cronometro.getTiempoSegundos()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/pausar")
    public ResponseEntity<?> pausarPartido(@PathVariable Long id) {
        try {
            CronometroPartido cronometro = cronometroService.pausarPartido(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Partido pausado",
                "estado", cronometro.getEstado(),
                "tiempo", cronometro.getTiempoSegundos()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/reanudar")
    public ResponseEntity<?> reanudarPartido(@PathVariable Long id) {
        try {
            CronometroPartido cronometro = cronometroService.reanudarPartido(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Partido reanudado",
                "estado", cronometro.getEstado(),
                "tiempo", cronometro.getTiempoSegundos()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/entretiempo")
    public ResponseEntity<?> entretiempo(@PathVariable Long id) {
        try {
            CronometroPartido cronometro = cronometroService.entretiempo(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Entretiempo",
                "estado", cronometro.getEstado(),
                "tiempo", cronometro.getTiempoSegundos()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/finalizar")
    @Transactional
    public ResponseEntity<?> finalizarPartido(@PathVariable Long id, @RequestBody Map<String, Integer> resultado) {
        System.out.println("[DEBUG] ===== FINALIZAR PARTIDO ID: " + id + " =====");
        try {
            Integer golesLocal = resultado.get("golesLocal");
            Integer golesVisitante = resultado.get("golesVisitante");
            
            System.out.println("[DEBUG] Goles recibidos - Local: " + golesLocal + ", Visitante: " + golesVisitante);
            
            if (golesLocal == null || golesVisitante == null || golesLocal < 0 || golesVisitante < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los goles deben ser números positivos"));
            }
            
            // 1. Finalizar cronómetro
            CronometroPartido cronometro = cronometroService.finalizarPartido(id);
            System.out.println("[DEBUG] Cronómetro finalizado");
            
            // 2. Obtener y actualizar el encuentro con el resultado
            Encuentro encuentro = encuentroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + id));
            
            System.out.println("[DEBUG] Encuentro encontrado - ID: " + id + ", Grupo: " + (encuentro.getGrupo() != null ? encuentro.getGrupo().getId() : "NULL"));
            System.out.println("[DEBUG] Equipos - Local: " + encuentro.getEquipoLocal().getId() + ", Visitante: " + encuentro.getEquipoVisitante().getId());
            
            encuentro.setGolesLocal(golesLocal);
            encuentro.setGolesVisitante(golesVisitante);
            encuentro.setEstado(com.GolsystemV2.Backend.enums.EstadoEncuentro.FINALIZADO);
            encuentro.setFecha(java.time.LocalDateTime.now());
            encuentroRepository.save(encuentro);
            System.out.println("[DEBUG] Encuentro guardado con resultado");
            
            // 3. Actualizar tablas de posiciones
            boolean tablaActualizada = false;
            if (encuentro.getGrupo() != null) {
                System.out.println("[DEBUG] Actualizando tabla de posiciones para grupo: " + encuentro.getGrupo().getId());
                tablaPosicionesService.actualizarTablaTrasResultado(id);
                tablaActualizada = true;
                System.out.println("[DEBUG] Tabla de posiciones actualizada");
            } else {
                System.out.println("[DEBUG] SIN GRUPO - No se actualiza tabla de posiciones (fase eliminatoria)");
            }
            
            // 4. Actualizar goleadores históricos basados en eventos registrados
            Long torneoId = encuentro.getFase().getTorneo().getId();
            List<EventoPartido> goles = eventoPartidoRepository.findByEncuentroIdAndTipoEvento(id, TipoEvento.GOL);
            System.out.println("[DEBUG] Eventos de gol encontrados: " + goles.size());
            
            int goleadoresActualizados = 0;
            for (EventoPartido gol : goles) {
                System.out.println("[DEBUG GOLES] Procesando gol ID: " + gol.getId() + ", minuto: " + gol.getMinuto());
                
                if (gol.getJugadorEquipoTorneo() == null) {
                    System.out.println("[DEBUG GOLES] ERROR: gol.getJugadorEquipoTorneo() es NULL");
                    continue;
                }
                
                if (gol.getEquipoTorneo() == null) {
                    System.out.println("[DEBUG GOLES] ERROR: gol.getEquipoTorneo() es NULL");
                    continue;
                }
                
                if (gol.getJugadorEquipoTorneo().getJugador() == null) {
                    System.out.println("[DEBUG GOLES] ERROR: gol.getJugadorEquipoTorneo().getJugador() es NULL");
                    continue;
                }
                
                if (gol.getEquipoTorneo().getEquipo() == null) {
                    System.out.println("[DEBUG GOLES] ERROR: gol.getEquipoTorneo().getEquipo() es NULL");
                    continue;
                }
                
                Long jugadorId = gol.getJugadorEquipoTorneo().getJugador().getId();
                Long equipoId = gol.getEquipoTorneo().getEquipo().getId();
                
                System.out.println("[DEBUG GOLES] jugadorId=" + jugadorId + ", equipoId=" + equipoId + ", torneoId=" + torneoId);
                
                Integer totalGolesTorneo = eventoPartidoRepository.countGolesPorJugadorEnTorneo(jugadorId, torneoId);
                System.out.println("[DEBUG GOLES] totalGolesTorneo calculado: " + totalGolesTorneo);
                
                goleadorHistoricoService.actualizarGoles(jugadorId, equipoId, torneoId, totalGolesTorneo);
                goleadoresActualizados++;
                System.out.println("[DEBUG GOLES] Goleador actualizado - Jugador: " + jugadorId + ", Goles: " + totalGolesTorneo);
            }
            
            System.out.println("[DEBUG] ===== PROCESO COMPLETADO =====");
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Partido finalizado y estadísticas actualizadas",
                "estado", cronometro.getEstado(),
                "tiempoFinal", cronometro.getTiempoSegundos(),
                "golesLocal", golesLocal,
                "golesVisitante", golesVisitante,
                "tablaActualizada", tablaActualizada,
                "goleadoresActualizados", goleadoresActualizados
            ));
        } catch (Exception e) {
            System.err.println("[ERROR] ===== ERROR AL FINALIZAR PARTIDO =====");
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "type", e.getClass().getSimpleName()
            ));
        }
    }
    
    @GetMapping("/{id}/estado-cronometro")
    public ResponseEntity<?> getEstadoCronometro(@PathVariable Long id) {
        // Obtener o crear el cronometro automáticamente si no existe
        CronometroPartido cronometro = cronometroService.obtenerOCrearCronometro(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("estado", cronometro.getEstado().name());
        response.put("tiempoTranscurrido", cronometro.getTiempoSegundos());
        response.put("minutos", cronometro.getTiempoSegundos() / 60);
        response.put("segundos", cronometro.getTiempoSegundos() % 60);
        response.put("estadoCronometro", cronometro.getEstado());
        response.put("puedeIniciar", cronometro.getEstado().name().equals("SIN_INICIAR"));
        response.put("puedePausar", cronometro.getEstado().name().equals("EN_JUEGO"));
        response.put("puedeReanudar", cronometro.getEstado().name().equals("PAUSADO"));
        
        return ResponseEntity.ok(response);
    }
    
    // ============ EVENTOS ============
    
    @PostMapping("/{id}/eventos")
    public ResponseEntity<?> registrarEvento(@PathVariable Long id, @RequestBody EventoPartidoRequest request) {
        try {
            EventoPartido evento;
            
            // Obtener el encuentro para conocer el torneo
            Encuentro encuentro = encuentroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + id));
            
            Long torneoId = encuentro.getFase().getTorneo().getId();
            
            // Convertir equipoId (ID del equipo) a equipoTorneoId (ID de la relación equipo-torneo)
            Long equipoId = request.getEquipoId();
            EquipoTorneo equipoTorneo = equipoTorneoRepository.findByTorneoIdAndEquipoId(torneoId, equipoId)
                    .orElseThrow(() -> new RuntimeException(
                            "Equipo no encontrado en el torneo. equipoId=" + equipoId + ", torneoId=" + torneoId));
            Long equipoTorneoId = equipoTorneo.getId();
            
            // El frontend envía jugadorId (ID del jugador global) pero el backend necesita jugadorEquipoTorneoId
            // Buscamos la relación JugadorEquipoTorneo para obtener el ID correcto
            Long jugadorEquipoTorneoId = obtenerJugadorEquipoTorneoId(request.getJugadorId(), equipoTorneoId);
            
            switch (request.getTipo().toUpperCase()) {
                case "GOL":
                    evento = eventoService.registrarGol(
                        id, 
                        jugadorEquipoTorneoId, 
                        equipoTorneoId, 
                        request.getMinuto()
                    );
                    // Actualizar goles del partido si es necesario
                    break;
                    
                case "TARJETA_AMARILLA":
                case "AMARILLA":
                    evento = eventoService.registrarTarjetaAmarilla(
                        id,
                        jugadorEquipoTorneoId,
                        equipoTorneoId,
                        request.getMinuto()
                    );
                    break;
                    
                case "TARJETA_ROJA":
                case "ROJA":
                    evento = eventoService.registrarTarjetaRoja(
                        id,
                        jugadorEquipoTorneoId,
                        equipoTorneoId,
                        request.getMinuto()
                    );
                    // Crear sanción automática
                    sancionService.crearSancionPorTarjetaRoja(
                        jugadorEquipoTorneoId,
                        id,
                        equipoTorneoId
                    );
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Tipo de evento no válido"));
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Evento registrado",
                "evento", evento
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Helper method para convertir jugadorId (ID global del jugador) a jugadorEquipoTorneoId.
     * El frontend envía el ID del jugador, pero las tablas de eventos y sanciones usan el ID 
     * de la relación JugadorEquipoTorneo.
     * 
     * Si el jugador no está registrado en el equipo-torneo, se crea la relación automáticamente
     * (comportamiento similar a apps de fútbol donde se puede agregar jugadores a la convocatoria).
     */
    private Long obtenerJugadorEquipoTorneoId(Long jugadorId, Long equipoTorneoId) {
        return jugadorEquipoTorneoService.findByJugadorIdAndEquipoTorneoId(jugadorId, equipoTorneoId)
                .map(JugadorEquipoTorneo::getId)
                .orElseGet(() -> {
                    // Crear la relación automáticamente si no existe
                    JugadorEquipoTorneo nuevaRelacion = crearJugadorEquipoTorneo(jugadorId, equipoTorneoId);
                    return nuevaRelacion.getId();
                });
    }
    
    /**
     * Crea una nueva relación JugadorEquipoTorneo para permitir registrar eventos.
     * Esto permite agregar jugadores a la convocatoria del partido dinámicamente.
     */
    private JugadorEquipoTorneo crearJugadorEquipoTorneo(Long jugadorId, Long equipoTorneoId) {
        try {
            JugadorEquipoTorneo jet = new JugadorEquipoTorneo();
            
            // Obtener entidades relacionadas
            Jugador jugador = jugadorRepository.findById(jugadorId)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + jugadorId));
            EquipoTorneo equipoTorneo = equipoTorneoRepository.findById(equipoTorneoId)
                    .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + equipoTorneoId));
            
            jet.setJugador(jugador);
            jet.setEquipoTorneo(equipoTorneo);
            jet.setActivo(true);
            jet.setNumeroCamiseta(0); // Se asignará después
            
            return jugadorEquipoTorneoRepository.save(jet);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear relación jugador-equipo-torneo: " + e.getMessage(), e);
        }
    }
    
    @GetMapping("/{id}/eventos")
    public ResponseEntity<?> getEventos(@PathVariable Long id) {
        List<EventoPartido> eventos = eventoService.findByEncuentroIdOrderByMinuto(id);
        return ResponseEntity.ok(eventos);
    }
    
    // ============ ESTADISTICAS ============
    
    @GetMapping("/{id}/resumen")
    public ResponseEntity<?> getResumenPartido(@PathVariable Long id) {
        List<EventoPartido> eventos = eventoService.findByEncuentroIdOrderByMinuto(id);
        EstadoCronometro estado = cronometroService.getEstadoActual(id);
        Integer tiempo = cronometroService.getTiempoTranscurrido(id);
        
        long golesLocal = eventos.stream()
            .filter(e -> e.getTipoEvento().toString().equals("GOL"))
            .filter(e -> e.getEquipoTorneo().getId().equals(eventos.get(0).getEncuentro().getEquipoLocal().getId()))
            .count();
            
        long golesVisitante = eventos.stream()
            .filter(e -> e.getTipoEvento().toString().equals("GOL"))
            .filter(e -> e.getEquipoTorneo().getId().equals(eventos.get(0).getEncuentro().getEquipoVisitante().getId()))
            .count();
        
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("estado", estado);
        resumen.put("tiempo", tiempo);
        resumen.put("golesLocal", golesLocal);
        resumen.put("golesVisitante", golesVisitante);
        resumen.put("totalEventos", eventos.size());
        
        return ResponseEntity.ok(resumen);
    }
}
