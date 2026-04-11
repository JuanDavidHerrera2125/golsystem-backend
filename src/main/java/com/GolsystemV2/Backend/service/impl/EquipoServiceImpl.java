package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> findById(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> findByCodigoEquipo(String codigoEquipo) {
        return equipoRepository.findByCodigoEquipo(codigoEquipo);
    }

    @Override
    public Equipo save(Equipo equipo) {
        if (existsByCodigoEquipo(equipo.getCodigoEquipo())) {
            throw new RuntimeException("Ya existe un equipo con el código: " + equipo.getCodigoEquipo());
        }
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo update(Long id, Equipo equipo) {
        Equipo existingEquipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + id));
        
        if (!existingEquipo.getCodigoEquipo().equals(equipo.getCodigoEquipo()) 
                && existsByCodigoEquipo(equipo.getCodigoEquipo())) {
            throw new RuntimeException("Ya existe un equipo con el código: " + equipo.getCodigoEquipo());
        }
        
        existingEquipo.setNombre(equipo.getNombre());
        existingEquipo.setCodigoEquipo(equipo.getCodigoEquipo());
        existingEquipo.setLogoUrl(equipo.getLogoUrl());
        existingEquipo.setActivo(equipo.getActivo());
        
        return equipoRepository.save(existingEquipo);
    }

    @Override
    public void deleteById(Long id) {
        if (!equipoRepository.existsById(id)) {
            throw new RuntimeException("Equipo no encontrado con ID: " + id);
        }
        equipoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigoEquipo(String codigoEquipo) {
        return equipoRepository.existsByCodigoEquipo(codigoEquipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> findByActivoTrue() {
        return equipoRepository.findAll().stream()
                .filter(Equipo::getActivo)
                .toList();
    }

    @Override
    public Equipo desactivarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + id));
        equipo.setActivo(false);
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo activarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + id));
        equipo.setActivo(true);
        return equipoRepository.save(equipo);
    }
}
