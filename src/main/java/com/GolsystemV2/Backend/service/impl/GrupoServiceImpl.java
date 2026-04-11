package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grupo> findById(Long id) {
        return grupoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grupo> findByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo) {
        return grupoRepository.findByFaseIdAndNombreGrupo(faseId, nombreGrupo);
    }

    @Override
    public Grupo save(Grupo grupo) {
        if (existsByFaseIdAndNombreGrupo(grupo.getFase().getId(), grupo.getNombreGrupo())) {
            throw new RuntimeException("Ya existe un grupo con ese nombre para esta fase");
        }
        return grupoRepository.save(grupo);
    }

    @Override
    public Grupo update(Long id, Grupo grupo) {
        Grupo existingGrupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));
        
        existingGrupo.setNombreGrupo(grupo.getNombreGrupo());
        
        return grupoRepository.save(existingGrupo);
    }

    @Override
    public void deleteById(Long id) {
        if (!grupoRepository.existsById(id)) {
            throw new RuntimeException("Grupo no encontrado con ID: " + id);
        }
        grupoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grupo> findByFaseIdOrderByNombreGrupo(Long faseId) {
        return grupoRepository.findByFaseIdOrderByNombreGrupo(faseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grupo> findByFaseId(Long faseId) {
        return grupoRepository.findByFaseId(faseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFaseIdAndNombreGrupo(Long faseId, String nombreGrupo) {
        return grupoRepository.existsByFaseIdAndNombreGrupo(faseId, nombreGrupo);
    }
}
