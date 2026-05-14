package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.dto.EquipoRequestDTO;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.service.EquipoService;
import com.GolsystemV2.Backend.service.EquipoTorneoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private static final Logger logger = LoggerFactory.getLogger(EquipoServiceImpl.class);

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private EquipoTorneoService equipoTorneoService;

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
        if (equipo.getCodigoEquipo() == null || equipo.getCodigoEquipo().trim().isEmpty()) {
            equipo.setCodigoEquipo(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (existsByCodigoEquipo(equipo.getCodigoEquipo())) {
            throw new RuntimeException("Ya existe un equipo con el código: " + equipo.getCodigoEquipo());
        }
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo saveWithTorneo(EquipoRequestDTO equipoDTO) {
        logger.info("[DIAGNOSTICO] === INICIO saveWithTorneo ===");
        logger.info("[DIAGNOSTICO] DTO recibido: nombre={}, codigo={}, logoUrl={}, activo={}, torneoId={}",
                equipoDTO.getNombre(),
                equipoDTO.getCodigoEquipo(),
                equipoDTO.getLogoUrl(),
                equipoDTO.getActivo(),
                equipoDTO.getTorneoId());

        String codigo = equipoDTO.getCodigoEquipo();
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            equipoDTO.setCodigoEquipo(codigo);
        }

        // Verificar si ya existe un equipo con ese código
        if (existsByCodigoEquipo(equipoDTO.getCodigoEquipo())) {
            throw new RuntimeException("Ya existe un equipo con el código: " + equipoDTO.getCodigoEquipo());
        }

        // Crear y guardar el equipo
        Equipo equipo = new Equipo();
        equipo.setNombre(equipoDTO.getNombre());
        equipo.setCodigoEquipo(equipoDTO.getCodigoEquipo());
        equipo.setLogoUrl(equipoDTO.getLogoUrl());
        equipo.setActivo(equipoDTO.getActivo() != null ? equipoDTO.getActivo() : true);

        Equipo savedEquipo = equipoRepository.save(equipo);

        // Si se proporcionó torneoId, crear la relación Equipo-Torneo
        if (equipoDTO.getTorneoId() != null) {
            logger.info("[DIAGNOSTICO] torneoId proporcionado: {}, equipoId: {}", equipoDTO.getTorneoId(), savedEquipo.getId());
            try {
                // Usar el servicio especializado que maneja validaciones y lógica de negocio
                logger.info("[DIAGNOSTICO] Llamando a equipoTorneoService.inscribirEquipoEnTorneo...");
                EquipoTorneo resultado = equipoTorneoService.inscribirEquipoEnTorneo(equipoDTO.getTorneoId(), savedEquipo.getId());
                logger.info("[DIAGNOSTICO] Relación creada exitosamente: id={}, eliminado={}", resultado.getId(), resultado.getEliminado());
            } catch (RuntimeException e) {
                logger.error("[DIAGNOSTICO] ERROR al inscribir equipo: {}", e.getMessage(), e);
                // Si falla la inscripción, el equipo ya fue creado pero no inscrito
                // Lanzar excepción con mensaje claro que incluye ambos IDs
                throw new RuntimeException("Equipo creado (ID: " + savedEquipo.getId() + ") pero error al inscribir en torneo (ID: " + equipoDTO.getTorneoId() + "): " + e.getMessage());
            }
        } else {
            logger.info("[DIAGNOSTICO] No se proporcionó torneoId, equipo creado sin inscripción");
        }

        return savedEquipo;
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
