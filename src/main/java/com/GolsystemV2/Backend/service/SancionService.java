package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Sancion;
import java.util.List;
import java.util.Optional;

public interface SancionService {
    
    List<Sancion> findAll();
    
    Optional<Sancion> findById(Long id);
    
    Sancion save(Sancion sancion);
    
    Sancion update(Long id, Sancion sancion);
    
    void deleteById(Long id);
    
    List<Sancion> findByTorneoIdAndActivaTrue(Long torneoId);
    
    List<Sancion> findByJugadorEquipoTorneoIdAndActivaTrue(Long jugadorEquipoTorneoId);
    
    List<Sancion> findByJugadorEquipoTorneoId(Long jugadorEquipoTorneoId);
    
    Integer countSancionesActivasPorJugadorTorneo(Long jugadorEquipoTorneoId, Long torneoId);
    
    Sancion crearSancion(Long torneoId, Long jugadorEquipoTorneoId, Long encuentroOrigenId, Integer cantidadFechas);
    
    Sancion incrementarFechasCumplidas(Long sancionId);
    
    Sancion desactivarSancion(Long sancionId);
    
    boolean jugadorEstaSancionado(Long jugadorEquipoTorneoId, Long torneoId);
}
