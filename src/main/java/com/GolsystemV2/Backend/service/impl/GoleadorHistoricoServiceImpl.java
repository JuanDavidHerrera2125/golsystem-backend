package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import com.GolsystemV2.Backend.entity.Jugador;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.repository.GoleadorHistoricoRepository;
import com.GolsystemV2.Backend.repository.JugadorRepository;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GoleadorHistoricoServiceImpl implements GoleadorHistoricoService {

    @Autowired
    private GoleadorHistoricoRepository goleadorHistoricoRepository;
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private TorneoRepository torneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GoleadorHistorico> findAll() {
        return goleadorHistoricoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GoleadorHistorico> findById(Long id) {
        return goleadorHistoricoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GoleadorHistorico> findByJugadorIdAndTorneoId(Long jugadorId, Long torneoId) {
        return goleadorHistoricoRepository.findByJugadorIdAndTorneoId(jugadorId, torneoId);
    }

    @Override
    public GoleadorHistorico save(GoleadorHistorico goleadorHistorico) {
        return goleadorHistoricoRepository.save(goleadorHistorico);
    }

    @Override
    public GoleadorHistorico update(Long id, GoleadorHistorico goleadorHistorico) {
        GoleadorHistorico existingGoleador = goleadorHistoricoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GoleadorHistorico no encontrado con ID: " + id));
        
        existingGoleador.setJugador(goleadorHistorico.getJugador());
        existingGoleador.setEquipo(goleadorHistorico.getEquipo());
        existingGoleador.setTorneo(goleadorHistorico.getTorneo());
        existingGoleador.setTotalGoles(goleadorHistorico.getTotalGoles());
        
        return goleadorHistoricoRepository.save(existingGoleador);
    }

    @Override
    public void deleteById(Long id) {
        if (!goleadorHistoricoRepository.existsById(id)) {
            throw new RuntimeException("GoleadorHistorico no encontrado con ID: " + id);
        }
        goleadorHistoricoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoleadorHistorico> findByTorneoIdOrderByTotalGolesDesc(Long torneoId) {
        return goleadorHistoricoRepository.findByTorneoIdOrderByTotalGolesDesc(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoleadorHistorico> findByJugadorIdOrderByTotalGolesDesc(Long jugadorId) {
        return goleadorHistoricoRepository.findByJugadorIdOrderByTotalGolesDesc(jugadorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoleadorHistorico> findByEquipoIdOrderByTotalGolesDesc(Long equipoId) {
        return goleadorHistoricoRepository.findByEquipoIdOrderByTotalGolesDesc(equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoleadorHistorico> findTablaGoleadoresPorTorneo(Long torneoId) {
        return goleadorHistoricoRepository.findTablaGoleadoresPorTorneo(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countTorneosGoleador(Long jugadorId) {
        return goleadorHistoricoRepository.countTorneosGoleador(jugadorId);
    }

    @Override
    public GoleadorHistorico actualizarGoles(Long jugadorId, Long equipoId, Long torneoId, Integer totalGoles) {
        Optional<GoleadorHistorico> goleadorOpt = findByJugadorIdAndTorneoId(jugadorId, torneoId);
        
        if (goleadorOpt.isPresent()) {
            GoleadorHistorico goleador = goleadorOpt.get();
            goleador.setTotalGoles(totalGoles);
            return goleadorHistoricoRepository.save(goleador);
        } else {
            GoleadorHistorico nuevoGoleador = new GoleadorHistorico();
            nuevoGoleador.setJugador(jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + jugadorId)));
            nuevoGoleador.setEquipo(equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + equipoId)));
            nuevoGoleador.setTorneo(torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId)));
            nuevoGoleador.setTotalGoles(totalGoles);
            return goleadorHistoricoRepository.save(nuevoGoleador);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByJugadorIdAndTorneoId(Long jugadorId, Long torneoId) {
        return goleadorHistoricoRepository.existsByJugadorIdAndTorneoId(jugadorId, torneoId);
    }
}
