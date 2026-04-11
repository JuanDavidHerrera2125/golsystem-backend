package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.service.EquipoTorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoTorneoServiceImpl implements EquipoTorneoService {

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EquipoTorneo> findAll() {
        return equipoTorneoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipoTorneo> findById(Long id) {
        return equipoTorneoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipoTorneo> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId) {
        return equipoTorneoRepository.findByTorneoIdAndEquipoId(torneoId, equipoId);
    }

    @Override
    public EquipoTorneo save(EquipoTorneo equipoTorneo) {
        if (existsByTorneoIdAndEquipoId(equipoTorneo.getTorneo().getId(), equipoTorneo.getEquipo().getId())) {
            throw new RuntimeException("El equipo ya está inscrito en este torneo");
        }
        
        if (!validarInscripcionEquipo(equipoTorneo.getTorneo().getId(), equipoTorneo.getEquipo().getId())) {
            throw new RuntimeException("No se puede inscribir el equipo en este torneo");
        }
        
        return equipoTorneoRepository.save(equipoTorneo);
    }

    @Override
    public EquipoTorneo update(Long id, EquipoTorneo equipoTorneo) {
        EquipoTorneo existingEquipoTorneo = equipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + id));
        
        existingEquipoTorneo.setEliminado(equipoTorneo.getEliminado());
        
        return equipoTorneoRepository.save(existingEquipoTorneo);
    }

    @Override
    public void deleteById(Long id) {
        if (!equipoTorneoRepository.existsById(id)) {
            throw new RuntimeException("EquipoTorneo no encontrado con ID: " + id);
        }
        equipoTorneoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId) {
        return equipoTorneoRepository.existsByTorneoIdAndEquipoId(torneoId, equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoTorneo> findByTorneoIdAndEliminadoFalse(Long torneoId) {
        return equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoTorneo> findByTorneoId(Long torneoId) {
        return equipoTorneoRepository.findByTorneoId(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countEquiposActivosPorTorneo(Long torneoId) {
        return equipoTorneoRepository.countEquiposActivosPorTorneo(torneoId);
    }

    @Override
    public EquipoTorneo eliminarEquipoDelTorneo(Long id) {
        EquipoTorneo equipoTorneo = equipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + id));
        equipoTorneo.setEliminado(true);
        return equipoTorneoRepository.save(equipoTorneo);
    }

    @Override
    public EquipoTorneo reactivarEquipoEnTorneo(Long id) {
        EquipoTorneo equipoTorneo = equipoTorneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + id));
        equipoTorneo.setEliminado(false);
        return equipoTorneoRepository.save(equipoTorneo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarInscripcionEquipo(Long torneoId, Long equipoId) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        Optional<Equipo> equipoOpt = equipoRepository.findById(equipoId);
        
        if (torneoOpt.isEmpty() || equipoOpt.isEmpty()) {
            return false;
        }
        
        Torneo torneo = torneoOpt.get();
        Equipo equipo = equipoOpt.get();
        
        if (!equipo.getActivo()) {
            throw new RuntimeException("El equipo no está activo");
        }
        
        if (!EstadoTorneo.CONFIGURACION.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden inscribir equipos en torneos en estado CONFIGURACION");
        }
        
        return true;
    }
}
