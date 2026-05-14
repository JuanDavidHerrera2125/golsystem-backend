package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
import java.util.List;
import java.util.Optional;

public interface TablaPosicionesService {
    
    List<TablaPosiciones> findAll();
    
    Optional<TablaPosiciones> findById(Long id);
    
    Optional<TablaPosiciones> findByGrupoIdAndEquipoTorneoId(Long grupoId, Long equipoTorneoId);
    
    TablaPosiciones save(TablaPosiciones tablaPosiciones);
    
    TablaPosiciones update(Long id, TablaPosiciones tablaPosiciones);
    
    void deleteById(Long id);
    
    List<TablaPosiciones> findByGrupoIdOrderByIdDesc(Long grupoId);
    
    List<TablaPosiciones> findTablaPosicionesOrdenada(Long grupoId);
    
    List<TablaPosiciones> findByGrupoIdAndEstado(Long grupoId, EstadoTablaPosiciones estado);
    
    TablaPosiciones cerrarTabla(Long id);
    
    TablaPosiciones abrirTabla(Long id);
    
    void actualizarEstadisticas(Long grupoId, Long equipoTorneoId, Integer pg, Integer pe, Integer pp, Integer gf, Integer gc, Integer amarillas, Integer rojas);
    
    /**
     * Actualiza la tabla de posiciones automáticamente tras registrar un resultado.
     * Recalcula todos los valores (PJ, PG, PE, PP, GF, GC, DG, PTS) para ambos equipos.
     * 
     * @param encuentroId ID del encuentro con resultado registrado
     */
    void actualizarTablaTrasResultado(Long encuentroId);
    
    /**
     * Inicializa la tabla de posiciones para un equipo en un grupo.
     * Se llama al crear un grupo o al inscribir un equipo.
     * 
     * @param grupoId ID del grupo
     * @param equipoTorneoId ID de la relación equipo-torneo
     * @return TablaPosiciones creada
     */
    TablaPosiciones inicializarTablaParaEquipo(Long grupoId, Long equipoTorneoId);
}
