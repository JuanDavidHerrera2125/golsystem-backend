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
}
