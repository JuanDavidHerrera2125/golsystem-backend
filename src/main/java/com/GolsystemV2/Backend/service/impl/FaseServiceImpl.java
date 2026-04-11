package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.service.FaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaseServiceImpl implements FaseService {

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Fase> findAll() {
        return faseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fase> findById(Long id) {
        return faseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fase> findByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase) {
        return faseRepository.findByTorneoIdAndNumeroFase(torneoId, numeroFase);
    }

    @Override
    public Fase save(Fase fase) {
        if (faseRepository.existsByTorneoIdAndNumeroFase(fase.getTorneo().getId(), fase.getNumeroFase())) {
            throw new RuntimeException("Ya existe una fase con ese número para este torneo");
        }
        return faseRepository.save(fase);
    }

    @Override
    public Fase update(Long id, Fase fase) {
        Fase existingFase = faseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + id));
        
        existingFase.setTipoFase(fase.getTipoFase());
        existingFase.setFormatoEncuentro(fase.getFormatoEncuentro());
        existingFase.setClasificadosSiguienteRonda(fase.getClasificadosSiguienteRonda());
        existingFase.setMetodoDesempatePlayoff(fase.getMetodoDesempatePlayoff());
        existingFase.setEstaCerrada(fase.getEstaCerrada());
        
        return faseRepository.save(existingFase);
    }

    @Override
    public void deleteById(Long id) {
        if (!faseRepository.existsById(id)) {
            throw new RuntimeException("Fase no encontrada con ID: " + id);
        }
        faseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fase> findByTorneoIdOrderByNumeroFase(Long torneoId) {
        return faseRepository.findByTorneoIdOrderByNumeroFase(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fase> findByTorneoIdAndEstaCerradaFalse(Long torneoId) {
        return faseRepository.findByTorneoIdAndEstaCerradaFalse(torneoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findMaxNumeroFaseByTorneoId(Long torneoId) {
        return faseRepository.findMaxNumeroFaseByTorneoId(torneoId);
    }

    @Override
    public Fase cerrarFase(Long faseId) {
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + faseId));
        
        if (!validarCierreFase(faseId)) {
            throw new RuntimeException("No se puede cerrar la fase. Hay partidos pendientes de finalizar");
        }
        
        fase.setEstaCerrada(true);
        return faseRepository.save(fase);
    }

    @Override
    public Fase crearSiguienteFase(Long torneoId, Integer numeroFase) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        Fase nuevaFase = new Fase();
        nuevaFase.setTorneo(torneo);
        nuevaFase.setNumeroFase(numeroFase);
        nuevaFase.setEstaCerrada(false);
        
        return faseRepository.save(nuevaFase);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarCierreFase(Long faseId) {
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + faseId));
        
        return true;
    }
}
