package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.dto.InscripcionJugadorDTO;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JugadorEquipoTorneoService {
    
    List<JugadorEquipoTorneo> findAll();
    
    Optional<JugadorEquipoTorneo> findById(Long id);
    
    Optional<JugadorEquipoTorneo> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    JugadorEquipoTorneo save(JugadorEquipoTorneo jugadorEquipoTorneo);
    
    JugadorEquipoTorneo update(Long id, JugadorEquipoTorneo jugadorEquipoTorneo);
    
    void deleteById(Long id);
    
    boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoIdAndActivoTrue(Long equipoTorneoId);
    
    List<JugadorEquipoTorneo> findByEquipoTorneoId(Long equipoTorneoId);
    
    Integer countJugadoresActivosPorEquipoTorneo(Long equipoTorneoId);
    
    JugadorEquipoTorneo desvincularJugador(Long id);
    
    JugadorEquipoTorneo vincularJugador(Long id);
    
    boolean validarInscripcionJugador(Long jugadorId, Long torneoId, Long equipoTorneoId);
    
    boolean validarLimiteJugadores(Long equipoTorneoId, Long torneoId);
    
    /**
     * Inscribe un jugador a un equipo en un torneo.
     * Si el jugador no existe, lo crea en el registro global.
     * Valida que el jugador no esté en otro equipo del mismo torneo.
     * 
     * @param dto Datos de inscripción
     * @return Map con la relación creada, el jugador y mensaje de confirmación
     */
    Map<String, Object> inscribirJugadorCompleto(InscripcionJugadorDTO dto);
    
    /**
     * Busca un jugador por documento de identidad.
     * 
     * @param documentoIdentidad Documento a buscar
     * @return Optional con el jugador si existe
     */
    Optional<Map<String, Object>> buscarJugadorPorDocumento(String documentoIdentidad);
    
    /**
     * Busca la relación JugadorEquipoTorneo por el ID del jugador y el ID del equipo-torneo.
     * Usado principalmente para el Match Center cuando el frontend envía jugadorId en lugar de jugadorEquipoTorneoId.
     * 
     * @param jugadorId ID del jugador
     * @param equipoTorneoId ID de la relación equipo-torneo
     * @return Optional con la relación si existe
     */
    Optional<JugadorEquipoTorneo> findByJugadorIdAndEquipoTorneoId(Long jugadorId, Long equipoTorneoId);
}
