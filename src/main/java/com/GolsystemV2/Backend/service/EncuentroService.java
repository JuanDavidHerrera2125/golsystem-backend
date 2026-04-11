package com.GolsystemV2.Backend.service;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import java.time.LocalDateTime;
import java.util.List;
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
}
