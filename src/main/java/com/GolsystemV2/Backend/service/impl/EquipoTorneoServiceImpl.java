package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.enums.TipoFase;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.service.EquipoTorneoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoTorneoServiceImpl implements EquipoTorneoService {

    private static final Logger logger = LoggerFactory.getLogger(EquipoTorneoServiceImpl.class);

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private FaseRepository faseRepository;

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
            throw new RuntimeException("No se pueden inscribir equipos porque el torneo ya está en curso o finalizado");
        }

        return true;
    }

    @Override
    public EquipoTorneo inscribirEquipoEnTorneo(Long torneoId, Long equipoId) {
        logger.info("[DIAGNOSTICO-ET] Iniciando inscripción: torneoId={}, equipoId={}", torneoId, equipoId);

        // Validar inscripción antes de continuar (verifica estado CONFIGURACION)
        logger.info("[DIAGNOSTICO-ET] Ejecutando validarInscripcionEquipo...");
        validarInscripcionEquipo(torneoId, equipoId);
        logger.info("[DIAGNOSTICO-ET] Validación exitosa");

        // Verificar que existan torneo y equipo
        logger.info("[DIAGNOSTICO-ET] Buscando torneo...");
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        logger.info("[DIAGNOSTICO-ET] Torneo encontrado: id={}, nombre={}", torneo.getId(), torneo.getNombre());

        logger.info("[DIAGNOSTICO-ET] Buscando equipo...");
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + equipoId));
        logger.info("[DIAGNOSTICO-ET] Equipo encontrado: id={}, nombre={}", equipo.getId(), equipo.getNombre());

        // Verificar si ya existe la relación
        logger.info("[DIAGNOSTICO-ET] Verificando relación existente...");
        Optional<EquipoTorneo> existente = equipoTorneoRepository.findByTorneoIdAndEquipoId(torneoId, equipoId);
        if (existente.isPresent()) {
            logger.info("[DIAGNOSTICO-ET] Relación existente encontrada: id={}, eliminado={}", existente.get().getId(), existente.get().getEliminado());
            EquipoTorneo et = existente.get();
            if (et.getEliminado()) {
                // Si estaba eliminado, reactivarlo
                logger.info("[DIAGNOSTICO-ET] Reactivando relación previamente eliminada");
                et.setEliminado(false);
                EquipoTorneo guardado = equipoTorneoRepository.save(et);
                logger.info("[DIAGNOSTICO-ET] Relación reactivada: id={}", guardado.getId());
                return guardado;
            }
            logger.error("[DIAGNOSTICO-ET] Error: El equipo ya está inscrito activamente en este torneo");
            throw new RuntimeException("El equipo ya está inscrito en este torneo");
        }

        // Crear nueva relación
        logger.info("[DIAGNOSTICO-ET] Creando nueva relación EquipoTorneo...");
        EquipoTorneo equipoTorneo = new EquipoTorneo();
        equipoTorneo.setTorneo(torneo);
        equipoTorneo.setEquipo(equipo);
        equipoTorneo.setEliminado(false);

        // Asignar automáticamente a un grupo si el torneo tiene grupos configurados
        if (torneo.getCantidadDeGrupos() != null && torneo.getCantidadDeGrupos() > 0) {
            logger.info("[DIAGNOSTICO-ET] Asignando grupo automáticamente...");
            
            int cantidadGrupos = torneo.getCantidadDeGrupos();
            
            // Contar equipos ya inscritos (activos) para calcular el grupo
            Integer equiposInscritos = equipoTorneoRepository.countEquiposActivosPorTorneo(torneoId);
            if (equiposInscritos == null) equiposInscritos = 0;
            
            // Calcular índice del grupo (0-based): (conteoActual % N)
            int grupoIndex = equiposInscritos % cantidadGrupos;
            char letraGrupo = (char) ('A' + grupoIndex);
            String nombreGrupo = "Grupo " + letraGrupo;
            
            logger.info("[DIAGNOSTICO-ET] Equipo {} de {}, asignando a grupo {} (índice {})", 
                    equiposInscritos + 1, cantidadGrupos, nombreGrupo, grupoIndex);
            
            // Buscar o crear la fase de grupos
            Fase fase = faseRepository.findByTorneoIdAndNumeroFase(torneoId, 1)
                    .orElseGet(() -> {
                        Fase nuevaFase = new Fase();
                        nuevaFase.setTorneo(torneo);
                        nuevaFase.setNumeroFase(1);
                        nuevaFase.setTipoFase(TipoFase.GRUPOS);
                        nuevaFase.setFormatoEncuentro(FormatoEncuentro.SOLO_IDA);
                        return faseRepository.save(nuevaFase);
                    });
            
            // Buscar o crear el grupo
            Grupo grupo = grupoRepository.findByFaseIdAndNombreGrupo(fase.getId(), nombreGrupo)
                    .orElseGet(() -> {
                        Grupo nuevoGrupo = new Grupo();
                        nuevoGrupo.setFase(fase);
                        nuevoGrupo.setNombreGrupo(nombreGrupo);
                        return grupoRepository.save(nuevoGrupo);
                    });
            
            equipoTorneo.setGrupo(grupo);
            logger.info("[DIAGNOSTICO-ET] Grupo asignado: id={}, nombre={}", grupo.getId(), grupo.getNombreGrupo());
        }

        logger.info("[DIAGNOSTICO-ET] Guardando relación en BD...");
        EquipoTorneo guardado = equipoTorneoRepository.save(equipoTorneo);
        logger.info("[DIAGNOSTICO-ET] Relación guardada exitosamente: id={}", guardado.getId());

        return guardado;
    }
}
