package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.service.EncuentroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EncuentroServiceImpl implements EncuentroService {

    @Autowired
    private EncuentroRepository encuentroRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findAll() {
        return encuentroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Encuentro> findById(Long id) {
        return encuentroRepository.findById(id);
    }

    @Override
    public Encuentro save(Encuentro encuentro) {
        return encuentroRepository.save(encuentro);
    }

    @Override
    public Encuentro update(Long id, Encuentro encuentro) {
        if (!validarModificacionPartido(id)) {
            throw new RuntimeException("No se puede modificar un partido que está finalizado");
        }
        
        Encuentro existingEncuentro = encuentroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + id));
        
        existingEncuentro.setFase(encuentro.getFase());
        existingEncuentro.setGrupo(encuentro.getGrupo());
        existingEncuentro.setEquipoLocal(encuentro.getEquipoLocal());
        existingEncuentro.setEquipoVisitante(encuentro.getEquipoVisitante());
        existingEncuentro.setFecha(encuentro.getFecha());
        existingEncuentro.setGolesLocal(encuentro.getGolesLocal());
        existingEncuentro.setGolesVisitante(encuentro.getGolesVisitante());
        existingEncuentro.setEstado(encuentro.getEstado());
        
        return encuentroRepository.save(existingEncuentro);
    }

    @Override
    public void deleteById(Long id) {
        if (!validarModificacionPartido(id)) {
            throw new RuntimeException("No se puede eliminar un partido que está finalizado");
        }
        
        if (!encuentroRepository.existsById(id)) {
            throw new RuntimeException("Encuentro no encontrado con ID: " + id);
        }
        encuentroRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFaseIdOrderByFecha(Long faseId) {
        return encuentroRepository.findByFaseIdOrderByFecha(faseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByGrupoIdOrderByFecha(Long grupoId) {
        return encuentroRepository.findByGrupoIdOrderByFecha(grupoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByEquipoId(Long equipoId) {
        return encuentroRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFaseIdAndEstado(Long faseId, EstadoEncuentro estado) {
        return encuentroRepository.findByFaseIdAndEstado(faseId, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return encuentroRepository.findByFechaBetween(inicio, fin);
    }

    @Override
    public Encuentro iniciarPartido(Long encuentroId) {
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        if (!EstadoEncuentro.PROGRAMADO.equals(encuentro.getEstado())) {
            throw new RuntimeException("Solo se pueden iniciar partidos programados");
        }
        
        encuentro.setEstado(EstadoEncuentro.EN_JUEGO);
        encuentro.setFecha(LocalDateTime.now());
        
        return encuentroRepository.save(encuentro);
    }

    @Override
    public Encuentro finalizarPartido(Long encuentroId, Integer golesLocal, Integer golesVisitante) {
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        if (!EstadoEncuentro.EN_JUEGO.equals(encuentro.getEstado())) {
            throw new RuntimeException("Solo se pueden finalizar partidos que están en juego");
        }
        
        encuentro.setGolesLocal(golesLocal);
        encuentro.setGolesVisitante(golesVisitante);
        encuentro.setEstado(EstadoEncuentro.FINALIZADO);
        
        return encuentroRepository.save(encuentro);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarModificacionPartido(Long encuentroId) {
        Optional<Encuentro> encuentroOpt = encuentroRepository.findById(encuentroId);
        
        if (encuentroOpt.isEmpty()) {
            return false;
        }
        
        Encuentro encuentro = encuentroOpt.get();
        return !EstadoEncuentro.FINALIZADO.equals(encuentro.getEstado());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarFinalizacionPartido(Long encuentroId) {
        Optional<Encuentro> encuentroOpt = encuentroRepository.findById(encuentroId);
        
        if (encuentroOpt.isEmpty()) {
            return false;
        }
        
        Encuentro encuentro = encuentroOpt.get();
        return EstadoEncuentro.EN_JUEGO.equals(encuentro.getEstado());
    }
}
