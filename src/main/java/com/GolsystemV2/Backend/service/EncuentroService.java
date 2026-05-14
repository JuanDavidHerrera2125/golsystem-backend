package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EncuentroService {
    
    List<Encuentro> findAll();
    
    Optional<Encuentro> findById(Long id);
    
    Encuentro save(Encuentro encuentro);
    
    Encuentro update(Long id, Encuentro encuentro);
    
    void deleteById(Long id);
    
    List<Encuentro> findByFaseIdOrderByFecha(Long faseId);
    
    List<Encuentro> findByGrupoIdOrderByFecha(Long grupoId);
    
    List<Encuentro> findByEquipoId(Long equipoId);
    
    List<Encuentro> findByFaseIdAndEstado(Long faseId, EstadoEncuentro estado);
    
    List<Encuentro> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    Encuentro iniciarPartido(Long encuentroId);
    
    Encuentro finalizarPartido(Long encuentroId, Integer golesLocal, Integer golesVisitante);
    
    boolean validarModificacionPartido(Long encuentroId);
    
    boolean validarFinalizacionPartido(Long encuentroId);
    
    /**
     * Genera el fixture de encuentros para un grupo (todos contra todos).
     * Para SOLO_IDA: cada equipo juega 1 vez contra cada rival (local/visitante balanceado).
     * Para IDA_Y_VUELTA: cada equipo juega 2 veces contra cada rival (ida y vuelta).
     * 
     * @param grupoId ID del grupo
     * @param formatoEncuentro Formato del encuentro (SOLO_IDA o IDA_Y_VUELTA)
     * @return Lista de encuentros generados
     */
    List<Encuentro> generarFixtureGrupo(Long grupoId, String formatoEncuentro);
    
    /**
     * Genera el fixture completo para una fase de grupos.
     * Crea encuentros para todos los grupos de la fase.
     * 
     * @param faseId ID de la fase
     * @param formatoEncuentro Formato del encuentro
     * @return Map con resultados por grupo
     */
    Map<String, Object> generarFixtureFase(Long faseId, String formatoEncuentro);
    
    /**
     * Registra el resultado de un encuentro y actualiza automáticamente
     * la tabla de posiciones.
     * 
     * @param encuentroId ID del encuentro
     * @param golesLocal Goles del equipo local
     * @param golesVisitante Goles del equipo visitante
     * @return Encuentro actualizado
     */
    Encuentro registrarResultado(Long encuentroId, Integer golesLocal, Integer golesVisitante);
}
