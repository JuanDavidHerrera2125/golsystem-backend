package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.CronometroPartido;
import com.GolsystemV2.Backend.enums.EstadoCronometro;

import java.util.Optional;

/**
 * Servicio para gestionar el cronometro persistente de los partidos
 */
public interface CronometroPartidoService {
    
    Optional<CronometroPartido> findByEncuentroId(Long encuentroId);
    
    CronometroPartido iniciarPartido(Long encuentroId);
    
    CronometroPartido pausarPartido(Long encuentroId);
    
    CronometroPartido reanudarPartido(Long encuentroId);
    
    CronometroPartido entretiempo(Long encuentroId);
    
    CronometroPartido finalizarPartido(Long encuentroId);
    
    CronometroPartido obtenerOCrearCronometro(Long encuentroId);
    
    EstadoCronometro getEstadoActual(Long encuentroId);
    
    Integer getTiempoTranscurrido(Long encuentroId);
}
