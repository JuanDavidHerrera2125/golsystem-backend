package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.CampeonHistorico;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.repository.CampeonHistoricoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.service.CampeonHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampeonHistoricoServiceImpl implements CampeonHistoricoService {

    @Autowired
    private CampeonHistoricoRepository campeonHistoricoRepository;
    
    @Autowired
    private TorneoRepository torneoRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CampeonHistorico> findAll() {
        return campeonHistoricoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampeonHistorico> findById(Long id) {
        return campeonHistoricoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampeonHistorico> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId) {
        return campeonHistoricoRepository.findByTorneoIdAndEquipoId(torneoId, equipoId);
    }

    @Override
    public CampeonHistorico save(CampeonHistorico campeonHistorico) {
        return campeonHistoricoRepository.save(campeonHistorico);
    }

    @Override
    public CampeonHistorico update(Long id, CampeonHistorico campeonHistorico) {
        CampeonHistorico existingCampeon = campeonHistoricoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CampeonHistorico no encontrado con ID: " + id));
        
        existingCampeon.setEquipo(campeonHistorico.getEquipo());
        existingCampeon.setTorneo(campeonHistorico.getTorneo());
        existingCampeon.setFechaLogro(campeonHistorico.getFechaLogro());
        
        return campeonHistoricoRepository.save(existingCampeon);
    }

    @Override
    public void deleteById(Long id) {
        if (!campeonHistoricoRepository.existsById(id)) {
            throw new RuntimeException("CampeonHistorico no encontrado con ID: " + id);
        }
        campeonHistoricoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampeonHistorico> findByTorneoIdOrderByFechaLogroDesc(Long torneoId) {
        return campeonHistoricoRepository.findByTorneoIdOrderByFechaLogroDesc(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampeonHistorico> findByEquipoIdOrderByFechaLogroDesc(Long equipoId) {
        return campeonHistoricoRepository.findByEquipoIdOrderByFechaLogroDesc(equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampeonHistorico> findAllOrderByFechaLogroDesc() {
        return campeonHistoricoRepository.findAllOrderByFechaLogroDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countCampeonatosPorEquipo(Long equipoId) {
        return campeonHistoricoRepository.countCampeonatosPorEquipo(equipoId);
    }

    @Override
    public CampeonHistorico registrarCampeon(Long torneoId, Long equipoId) {
        if (existsByTorneoIdAndEquipoId(torneoId, equipoId)) {
            throw new RuntimeException("Ya existe un registro de campeón para este torneo y equipo");
        }
        
        CampeonHistorico campeonHistorico = new CampeonHistorico();
        campeonHistorico.setTorneo(torneoRepository.findById(torneoId)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId)));
        campeonHistorico.setEquipo(equipoRepository.findById(equipoId)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + equipoId)));
        campeonHistorico.setFechaLogro(LocalDate.now());
        
        return campeonHistoricoRepository.save(campeonHistorico);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId) {
        return campeonHistoricoRepository.existsByTorneoIdAndEquipoId(torneoId, equipoId);
    }
}
