package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.dto.FaseConfigDto;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoFase;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.service.FaseService;
import com.GolsystemV2.Backend.service.FaseManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * FaseServiceImpl — Adaptador CRUD sobre FaseRepository.
 *
 * Las operaciones de ciclo de vida (activar, cerrar, avanzar) delegan
 * al FaseManagerService que concentra toda la lógica de negocio.
 */
@Service
@Transactional
public class FaseServiceImpl implements FaseService {

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private FaseManagerService faseManagerService;

    // ─── CRUD BÁSICO ──────────────────────────────────────────────────────────

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

        // REGLA DE BLOQUEO: no se permiten cambios estructurales si está bloqueada
        if (existingFase.estaBloqueadaParaEdicion()) {
            throw new RuntimeException(
                    "La fase " + existingFase.getNumeroFase() + " está bloqueada para edición. " +
                    "Estado actual: " + existingFase.getEstadoFase() +
                    ". Solo se pueden editar fases en estado CONFIGURACION.");
        }

        existingFase.setTipoFase(fase.getTipoFase());
        existingFase.setFormatoEncuentro(fase.getFormatoEncuentro());
        existingFase.setClasificadosSiguienteRonda(fase.getClasificadosSiguienteRonda());
        existingFase.setMetodoDesempatePlayoff(fase.getMetodoDesempatePlayoff());
        existingFase.setCantidadGrupos(fase.getCantidadGrupos());
        existingFase.setEquiposClasificadosPorGrupo(fase.getEquiposClasificadosPorGrupo());
        // El estadoFase NO se puede cambiar directamente desde el update,
        // solo se modifica via activarFase / cerrarFase / avanzarSiguienteFase.

        return faseRepository.save(existingFase);
    }

    @Override
    public void deleteById(Long id) {
        Fase fase = faseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + id));

        if (fase.estaEnCurso() || fase.estaFinalizada()) {
            throw new RuntimeException(
                    "No se puede eliminar la Fase " + fase.getNumeroFase() +
                    " porque está en estado " + fase.getEstadoFase() + ". " +
                    "Solo se pueden eliminar fases en CONFIGURACION.");
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
    public List<Fase> findByTorneoIdAndEstadoFase(Long torneoId, EstadoFase estadoFase) {
        return faseRepository.findByTorneoIdAndEstadoFase(torneoId, estadoFase);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findMaxNumeroFaseByTorneoId(Long torneoId) {
        return faseRepository.findMaxNumeroFaseByTorneoId(torneoId);
    }

    // ─── DELEGACIÓN A FaseManagerService ────────────────────────────────────

    @Override
    public FaseEstadoDto configurarFase(FaseConfigDto dto) {
        return faseManagerService.configurarFase(dto);
    }

    @Override
    public FaseEstadoDto activarFase(Long faseId) {
        return faseManagerService.activarFase(faseId);
    }

    @Override
    public FaseEstadoDto cerrarFase(Long faseId) {
        return faseManagerService.cerrarFase(faseId);
    }

    @Override
    public FaseEstadoDto avanzarSiguienteFase(Long torneoId) {
        return faseManagerService.avanzarSiguienteFase(torneoId);
    }

    @Override
    public FaseEstadoDto obtenerEstadoFase(Long faseId) {
        return faseManagerService.obtenerEstadoFase(faseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeActivarFase(Long faseId) {
        return faseManagerService.puedeActivarFase(faseId);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public boolean validarCierreFase(Long faseId) {
        // Mantener compatibilidad: devuelve true si todos los partidos están finalizados
        // El FaseManagerService hace la validación real en cerrarFase()
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + faseId));
        return fase.estaEnCurso(); // Al menos debe estar EN_CURSO para poder cerrarla
    }
}
