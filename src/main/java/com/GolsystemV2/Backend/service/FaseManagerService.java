package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.dto.FaseConfigDto;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;

/**
 * FaseManagerService — Motor de ciclo de vida de la Fase.
 *
 * Controla la progresión lineal obligatoria del torneo:
 *   CONFIGURACION → EN_CURSO → FINALIZADA
 *
 * Reglas centrales:
 *  1. Una fase en EN_CURSO o FINALIZADA está BLOQUEADA para edición estructural.
 *  2. La cantidad de grupos debe estar entre 1 y 15.
 *  3. No se puede activar la Fase N si la Fase N-1 no está FINALIZADA.
 *  4. Al cerrar una fase, se disparan los eventos de histórico.
 *  5. Un equipo no puede pertenecer a dos grupos de la misma fase.
 */
public interface FaseManagerService {

    /**
     * Crea y configura una nueva fase para un torneo.
     * Valida la progresión lineal: si numeroFase > 1, la fase N-1 debe existir y estar FINALIZADA.
     * Valida que cantidadGrupos esté entre 1 y 15.
     *
     * @param dto configuración de la nueva fase
     * @return estado enriquecido de la fase creada
     */
    FaseEstadoDto configurarFase(FaseConfigDto dto);

    /**
     * Activa una fase: distribuye equipos en grupos (snake draft) y cambia el estado a EN_CURSO.
     * Desde este momento la fase está BLOQUEADA para cambios estructurales.
     * Valida integridad: ningún equipo puede estar en dos grupos de la misma fase.
     *
     * @param faseId ID de la fase a activar
     * @return estado enriquecido de la fase activada
     */
    FaseEstadoDto activarFase(Long faseId);

    /**
     * Cierra una fase:
     *  1. Valida que TODOS los encuentros están FINALIZADOS.
     *  2. Dispara el evento de histórico (goleadores, estadísticas).
     *  3. Marca la fase como FINALIZADA.
     *
     * @param faseId ID de la fase a cerrar
     * @return estado enriquecido de la fase cerrada
     */
    FaseEstadoDto cerrarFase(Long faseId);

    /**
     * Avanza a la siguiente fase automáticamente.
     * Crea la Fase N+1 en CONFIGURACION si la Fase N está FINALIZADA.
     *
     * @param torneoId ID del torneo
     * @return estado enriquecido de la nueva fase
     */
    FaseEstadoDto avanzarSiguienteFase(Long torneoId);

    /**
     * Obtiene el estado detallado de una fase incluyendo métricas calculadas.
     *
     * @param faseId ID de la fase
     * @return estado enriquecido
     */
    FaseEstadoDto obtenerEstadoFase(Long faseId);

    /**
     * Verifica si una fase puede ser activada (está en CONFIGURACION y tiene grupos).
     *
     * @param faseId ID de la fase
     * @return true si puede activarse
     */
    boolean puedeActivarFase(Long faseId);

    /**
     * Valida que se respete la secuencia lineal de fases.
     * Lanza excepción si se intenta saltar fases.
     *
     * @param torneoId ID del torneo
     * @param numeroFaseObjetivo número de fase que se intenta crear/activar
     */
    void validarSecuenciaLineal(Long torneoId, Integer numeroFaseObjetivo);

    /**
     * Dispara manualmente el evento de histórico para una fase finalizada.
     * Útil para recalcular estadísticas o corregir datos.
     *
     * @param faseId ID de la fase
     */
    void dispararEventoHistorico(Long faseId);
}
