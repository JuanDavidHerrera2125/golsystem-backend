package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.JugadorEquipoTorneo;
import com.GolsystemV2.Backend.entity.CampeonHistorico;
import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import com.GolsystemV2.Backend.enums.TipoFase;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.JugadorEquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.CampeonHistoricoRepository;
import com.GolsystemV2.Backend.repository.GoleadorHistoricoRepository;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.repository.TablaPosicionesRepository;
import com.GolsystemV2.Backend.service.TorneoService;
import com.GolsystemV2.Backend.service.CampeonHistoricoService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import com.GolsystemV2.Backend.service.FaseManagerService;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;
import com.GolsystemV2.Backend.enums.EstadoFase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TorneoServiceImpl implements TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private JugadorEquipoTorneoRepository jugadorEquipoTorneoRepository;

    @Autowired
    private CampeonHistoricoRepository campeonHistoricoRepository;

    @Autowired
    private GoleadorHistoricoRepository goleadorHistoricoRepository;

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EncuentroRepository encuentroRepository;
    
    @Autowired
    private TablaPosicionesRepository tablaPosicionesRepository;

    @Autowired
    private FaseManagerService faseManagerService;

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torneo> findByNombre(String nombre) {
        return torneoRepository.findByNombre(nombre);
    }

    @Override
    public Torneo save(Torneo torneo) {
        if (existsByNombre(torneo.getNombre())) {
            throw new RuntimeException("Ya existe un torneo con el nombre: " + torneo.getNombre());
        }
        try {
            return torneoRepository.save(torneo);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Error al guardar el torneo: verifique que todos los campos obligatorios estén completos. " + e.getMessage());
        }
    }

    @Override
    public Torneo update(Long id, Torneo torneo) {
        Torneo existingTorneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + id));
        
        // Solo permitir edición si el torneo está en CONFIGURACION
        if (!EstadoTorneo.CONFIGURACION.equals(existingTorneo.getEstado())) {
            throw new RuntimeException(
                    "No se puede editar el torneo porque está en estado: " + existingTorneo.getEstado() +
                    ". Solo se pueden editar torneos en estado CONFIGURACION.");
        }
        
        if (!existingTorneo.getNombre().equals(torneo.getNombre()) 
                && existsByNombre(torneo.getNombre())) {
            throw new RuntimeException("Ya existe un torneo con el nombre: " + torneo.getNombre());
        }
        
        // Guardar el estado actual para protegerlo (solo cambia via iniciarTorneo/finalizarTorneo)
        EstadoTorneo estadoActual = existingTorneo.getEstado();
        
        existingTorneo.setNombre(torneo.getNombre());
        existingTorneo.setLogoUrl(torneo.getLogoUrl());
        existingTorneo.setCategoria(torneo.getCategoria());
        existingTorneo.setMinJugadores(torneo.getMinJugadores());
        existingTorneo.setMaxJugadores(torneo.getMaxJugadores());
        
        // Nuevos campos agregados para sincronización con frontend
        existingTorneo.setTipo(torneo.getTipo());
        existingTorneo.setFase(torneo.getFase());
        existingTorneo.setFechaInicio(torneo.getFechaInicio());
        existingTorneo.setFechaFin(torneo.getFechaFin());
        existingTorneo.setDescripcion(torneo.getDescripcion());
        
        // Validar y actualizar cantidad de grupos (1-15)
        Integer nuevaCantidadGrupos = null;
        if (torneo.getCantidadDeGrupos() != null) {
            if (torneo.getCantidadDeGrupos() < 1 || torneo.getCantidadDeGrupos() > 15) {
                throw new RuntimeException(
                        "La cantidad de grupos debe estar entre 1 y 15. " +
                        "Valor recibido: " + torneo.getCantidadDeGrupos());
            }
            
            // Validar que cantidad de grupos no supere equipos inscritos
            List<EquipoTorneo> equiposInscritos = equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(id);
            int totalEquipos = equiposInscritos.size();
            
            if (torneo.getCantidadDeGrupos() > totalEquipos) {
                throw new RuntimeException(
                        "La cantidad de grupos (" + torneo.getCantidadDeGrupos() + ") " +
                        "no puede superar la cantidad de equipos inscritos (" + totalEquipos + "). " +
                        "Se necesita al menos 1 equipo por grupo.");
            }
            
            // Detectar si cambió la cantidad de grupos
            Integer cantidadActual = existingTorneo.getCantidadDeGrupos();
            if (cantidadActual == null || !cantidadActual.equals(torneo.getCantidadDeGrupos())) {
                nuevaCantidadGrupos = torneo.getCantidadDeGrupos();
            }
            
            existingTorneo.setCantidadDeGrupos(torneo.getCantidadDeGrupos());
        }
        
        // Restaurar el estado original - no permitir cambio directo de estado
        existingTorneo.setEstado(estadoActual);
        
        Torneo torneoGuardado = torneoRepository.save(existingTorneo);
        
        // Si cambió la cantidad de grupos, redistribuir equipos en Fase 1 (si existe y está en CONFIGURACION)
        if (nuevaCantidadGrupos != null) {
            redistribuirEquiposEnFase1(torneoGuardado, nuevaCantidadGrupos);
        }
        
        return torneoGuardado;
    }
    
    /**
     * Redistribuye los equipos de Fase 1 en los nuevos grupos cuando cambia cantidadDeGrupos.
     * Solo funciona si la Fase 1 está en estado CONFIGURACION.
     */
    private void redistribuirEquiposEnFase1(Torneo torneo, int nuevaCantidadGrupos) {
        // Buscar Fase 1 del torneo
        Fase fase1 = faseRepository.findByTorneoIdAndNumeroFase(torneo.getId(), 1).orElse(null);
        
        if (fase1 == null) {
            return; // No hay Fase 1, nada que redistribuir
        }
        
        // Solo redistribuir si la fase está en CONFIGURACION
        if (!EstadoFase.CONFIGURACION.equals(fase1.getEstadoFase())) {
            System.out.println("[TorneoService] Fase 1 no está en CONFIGURACION, no se redistribuyen grupos");
            return;
        }
        
        // Obtener equipos asignados a la fase
        List<Equipo> equiposFase = fase1.getEquipos();
        if (equiposFase == null || equiposFase.isEmpty()) {
            System.out.println("[TorneoService] Fase 1 sin equipos asignados");
            return;
        }
        
        // Validar que hay suficientes equipos para los nuevos grupos
        if (equiposFase.size() < nuevaCantidadGrupos) {
            throw new RuntimeException(
                    "No se puede cambiar a " + nuevaCantidadGrupos + " grupos porque " +
                    "la Fase 1 solo tiene " + equiposFase.size() + " equipos asignados. " +
                    "Se necesita al menos 1 equipo por grupo.");
        }
        
        // Eliminar grupos existentes de la fase
        List<Grupo> gruposExistentes = grupoRepository.findByFaseIdOrderByNombreGrupo(fase1.getId());
        if (!gruposExistentes.isEmpty()) {
            System.out.println("[TorneoService] Eliminando " + gruposExistentes.size() + " grupos existentes de Fase 1");
            for (Grupo grupo : gruposExistentes) {
                // Desasociar equipos del grupo (pero mantener en fase)
                List<EquipoTorneo> equiposEnGrupo = equipoTorneoRepository.findByGrupoIdAndEliminadoFalse(grupo.getId());
                for (EquipoTorneo et : equiposEnGrupo) {
                    et.setGrupo(null);
                    equipoTorneoRepository.save(et);
                }
                grupoRepository.delete(grupo);
            }
        }
        
        // Actualizar cantidad de grupos en la fase
        fase1.setCantidadGrupos(nuevaCantidadGrupos);
        faseRepository.save(fase1);
        
        // Crear nuevos grupos vacíos
        List<Grupo> nuevosGrupos = new ArrayList<>();
        for (int i = 0; i < nuevaCantidadGrupos; i++) {
            String nombreGrupo = "ABCDEFGHIJKLMNO".charAt(i) + "";
            Grupo grupo = new Grupo();
            grupo.setNombreGrupo(nombreGrupo);
            grupo.setFase(fase1);
            nuevosGrupos.add(grupoRepository.save(grupo));
        }
        
        // Redistribuir equipos usando Snake Draft
        List<EquipoTorneo> equiposTorneo = new ArrayList<>();
        for (Equipo equipo : equiposFase) {
            EquipoTorneo et = equipoTorneoRepository
                    .findByTorneoIdAndEquipoId(torneo.getId(), equipo.getId())
                    .orElse(null);
            if (et != null) {
                equiposTorneo.add(et);
            }
        }
        
        // Distribución Snake Draft: 4 equipos, 2 grupos → [2,2]
        int totalEquipos = equiposTorneo.size();
        int equiposPorGrupoBase = totalEquipos / nuevaCantidadGrupos;
        int gruposConEquipoExtra = totalEquipos % nuevaCantidadGrupos;
        
        int equipoIndex = 0;
        for (int i = 0; i < nuevaCantidadGrupos; i++) {
            int equiposEnEsteGrupo = equiposPorGrupoBase + (i < gruposConEquipoExtra ? 1 : 0);
            Grupo grupo = nuevosGrupos.get(i);
            
            for (int j = 0; j < equiposEnEsteGrupo && equipoIndex < totalEquipos; j++) {
                EquipoTorneo et = equiposTorneo.get(equipoIndex++);
                et.setGrupo(grupo);
                equipoTorneoRepository.save(et);
            }
        }
        
        System.out.println("[TorneoService] Redistribución completada: " + totalEquipos + 
                          " equipos en " + nuevaCantidadGrupos + " grupos");
    }

    @Override
    public void deleteById(Long id) {
        if (!torneoRepository.existsById(id)) {
            throw new RuntimeException("Torneo no encontrado con ID: " + id);
        }
        torneoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return torneoRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findByEstado(EstadoTorneo estado) {
        return torneoRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findTorneosEnConfiguracion() {
        return torneoRepository.findTorneosEnConfiguracion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> findTorneosEnCurso() {
        return torneoRepository.findTorneosEnCurso();
    }

    @Override
    public Torneo iniciarTorneo(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        if (!EstadoTorneo.CONFIGURACION.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden iniciar torneos en estado CONFIGURACION");
        }
        
        if (!validarInicioTorneo(torneoId)) {
            throw new RuntimeException("No se puede iniciar el torneo. Algunos equipos no cumplen con el mínimo de jugadores requerido");
        }
        
        torneo.setEstado(EstadoTorneo.EN_CURSO);
        return torneoRepository.save(torneo);
    }

    @Autowired
    private CampeonHistoricoService campeonHistoricoService;

    @Override
    public Torneo finalizarTorneo(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        if (!EstadoTorneo.EN_CURSO.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden finalizar torneos en estado EN_CURSO");
        }
        
        // Determinar el campeón automáticamente
        try {
            List<Fase> fases = faseRepository.findByTorneoIdOrderByNumeroFaseDesc(torneoId);
            if (!fases.isEmpty()) {
                Fase ultimaFase = fases.get(0);
                com.GolsystemV2.Backend.entity.Equipo equipoCampeon = null;

                if (TipoFase.ELIMINACION_DIRECTA.equals(ultimaFase.getTipoFase())) {
                    // Si es eliminación directa, el ganador del último partido es el campeón
                    List<Encuentro> encuentros = encuentroRepository.findByFaseIdOrderByFechaDesc(ultimaFase.getId());
                    if (!encuentros.isEmpty()) {
                        Encuentro granFinal = encuentros.get(0);
                        if (granFinal.getGolesLocal() > granFinal.getGolesVisitante()) {
                            equipoCampeon = granFinal.getEquipoLocal().getEquipo();
                        } else if (granFinal.getGolesVisitante() > granFinal.getGolesLocal()) {
                            equipoCampeon = granFinal.getEquipoVisitante().getEquipo();
                        }
                    }
                } else {
                    // Si es grupos/liga, el equipo con más puntos en la tabla de posiciones es el campeón
                    List<Grupo> grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(ultimaFase.getId());
                    if (!grupos.isEmpty()) {
                        // Tomamos el primer grupo (aplica para ligas de un solo grupo)
                        List<com.GolsystemV2.Backend.entity.TablaPosiciones> tabla = tablaPosicionesRepository.findTablaPosicionesOrdenada(grupos.get(0).getId());
                        if (!tabla.isEmpty()) {
                            equipoCampeon = tabla.get(0).getEquipoTorneo().getEquipo();
                        }
                    }
                }

                if (equipoCampeon != null) {
                    campeonHistoricoService.registrarCampeon(torneoId, equipoCampeon.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al registrar campeón automático: " + e.getMessage());
        }

        torneo.setEstado(EstadoTorneo.FINALIZADO);
        return torneoRepository.save(torneo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarInicioTorneo(Long torneoId) {
        List<EquipoTorneo> equiposActivos = equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        
        for (EquipoTorneo equipoTorneo : equiposActivos) {
            Integer jugadoresActivos = jugadorEquipoTorneoRepository.countJugadoresActivosPorEquipoTorneo(equipoTorneo.getId());
            Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
            
            if (torneo != null && jugadoresActivos < torneo.getMinJugadores()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Torneo distribuirEquiposEnGrupos(Long torneoId, FormatoEncuentro formatoEncuentro) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));

        // Solo permitir generar grupos en estado CONFIGURACION
        if (!EstadoTorneo.CONFIGURACION.equals(torneo.getEstado())) {
            throw new RuntimeException("Solo se pueden generar grupos cuando el torneo está en configuración");
        }

        // Verificar que el torneo tenga configurada cantidad de grupos
        if (torneo.getCantidadDeGrupos() == null || torneo.getCantidadDeGrupos() <= 0) {
            throw new RuntimeException("El torneo no tiene configurada la cantidad de grupos");
        }

        // Obtener o crear la Fase 1 en estado CONFIGURACION
        Fase fase = faseRepository.findByTorneoIdAndNumeroFase(torneoId, 1)
                .orElseGet(() -> {
                    Fase nuevaFase = new Fase();
                    nuevaFase.setTorneo(torneo);
                    nuevaFase.setNumeroFase(1);
                    nuevaFase.setTipoFase(TipoFase.GRUPOS);
                    nuevaFase.setFormatoEncuentro(formatoEncuentro);
                    nuevaFase.setEstadoFase(com.GolsystemV2.Backend.enums.EstadoFase.CONFIGURACION);
                    nuevaFase.setCantidadGrupos(torneo.getCantidadDeGrupos());
                    return faseRepository.save(nuevaFase);
                });

        // Si la fase ya tiene estado EN_CURSO o FINALIZADA, no permitir redistribución
        if (fase.estaBloqueadaParaEdicion()) {
            throw new RuntimeException(
                    "La Fase 1 ya fue activada (estado: " + fase.getEstadoFase() +
                    "). No se puede redistribuir equipos.");
        }

        // Actualizar formato si cambió
        fase.setFormatoEncuentro(formatoEncuentro);
        fase.setCantidadGrupos(torneo.getCantidadDeGrupos());
        faseRepository.save(fase);

        // Delegar al FaseManagerService para distribución y activación
        faseManagerService.activarFase(fase.getId());

        return torneo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerGruposConEquipos(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        // Obtener fase de grupos si existe
        Optional<Fase> faseOpt = faseRepository.findByTorneoIdAndNumeroFase(torneoId, 1);
        if (faseOpt.isEmpty()) {
            return resultado; // Retornar lista vacía si no hay fase
        }
        
        Fase fase = faseOpt.get();
        List<Grupo> grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(fase.getId());
        
        for (Grupo grupo : grupos) {
            Map<String, Object> grupoMap = new HashMap<>();
            grupoMap.put("id", grupo.getId());
            grupoMap.put("nombre", grupo.getNombreGrupo());
            
            // Obtener equipos de este grupo
            List<EquipoTorneo> equiposEnGrupo = equipoTorneoRepository.findByGrupoIdAndEliminadoFalse(grupo.getId());
            List<Map<String, Object>> equiposList = new ArrayList<>();
            
            for (EquipoTorneo et : equiposEnGrupo) {
                Map<String, Object> equipoMap = new HashMap<>();
                equipoMap.put("id", et.getEquipo().getId());
                equipoMap.put("nombre", et.getEquipo().getNombre());
                equipoMap.put("codigo", et.getEquipo().getCodigoEquipo());
                equipoMap.put("logoUrl", et.getEquipo().getLogoUrl());
                equiposList.add(equipoMap);
            }
            
            grupoMap.put("equipos", equiposList);
            grupoMap.put("cantidadEquipos", equiposList.size());
            resultado.add(grupoMap);
        }
        
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerFases(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));

        List<Map<String, Object>> resultado = new ArrayList<>();

        // Obtener todas las fases del torneo CON sus equipos (JOIN FETCH)
        List<Fase> fases = faseRepository.findByTorneoIdWithEquipos(torneoId);

        for (Fase fase : fases) {
            Map<String, Object> faseMap = new HashMap<>();
            faseMap.put("id", fase.getId());
            faseMap.put("numeroFase", fase.getNumeroFase());
            faseMap.put("tipoFase", fase.getTipoFase());
            faseMap.put("formatoEncuentro", fase.getFormatoEncuentro());
            faseMap.put("estadoFase", fase.getEstadoFase() != null ? fase.getEstadoFase().name() : "CONFIGURACION");
            faseMap.put("bloqueada", fase.estaBloqueadaParaEdicion());
            faseMap.put("cantidadGrupos", fase.getCantidadGrupos());
            faseMap.put("equiposClasificadosPorGrupo", fase.getEquiposClasificadosPorGrupo());

            // Agregar equipos asignados a la fase
            if (fase.getEquipos() != null && !fase.getEquipos().isEmpty()) {
                List<Map<String, Object>> equiposList = new ArrayList<>();
                for (com.GolsystemV2.Backend.entity.Equipo equipo : fase.getEquipos()) {
                    Map<String, Object> equipoMap = new HashMap<>();
                    equipoMap.put("id", equipo.getId());
                    equipoMap.put("nombre", equipo.getNombre());
                    equipoMap.put("codigoEquipo", equipo.getCodigoEquipo());
                    equipoMap.put("logoUrl", equipo.getLogoUrl());
                    equiposList.add(equipoMap);
                }
                faseMap.put("equipos", equiposList);
                faseMap.put("totalEquipos", equiposList.size());
            } else {
                faseMap.put("equipos", new ArrayList<>());
                faseMap.put("totalEquipos", 0);
            }

            resultado.add(faseMap);
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerEncuentros(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        List<Fase> fases = faseRepository.findByTorneoIdOrderByNumeroFase(torneoId);
        
        for (Fase fase : fases) {
            List<Encuentro> encuentros = encuentroRepository.findByFaseIdOrderByFecha(fase.getId());
            for (Encuentro encuentro : encuentros) {
                Map<String, Object> encuentroMap = new HashMap<>();
                encuentroMap.put("id", encuentro.getId());
                encuentroMap.put("fechaProgramada", encuentro.getFecha());
                encuentroMap.put("estado", encuentro.getEstado());
                
                Map<String, Object> faseMap = new HashMap<>();
                faseMap.put("id", fase.getId());
                faseMap.put("nombre", "Fase " + fase.getNumeroFase());
                encuentroMap.put("fase", faseMap);
                
                Map<String, Object> localMap = new HashMap<>();
                localMap.put("id", encuentro.getEquipoLocal().getId());
                localMap.put("nombre", encuentro.getEquipoLocal().getEquipo().getNombre());
                localMap.put("logoUrl", encuentro.getEquipoLocal().getEquipo().getLogoUrl());
                encuentroMap.put("equipoLocal", localMap);
                
                Map<String, Object> visitanteMap = new HashMap<>();
                visitanteMap.put("id", encuentro.getEquipoVisitante().getId());
                visitanteMap.put("nombre", encuentro.getEquipoVisitante().getEquipo().getNombre());
                visitanteMap.put("logoUrl", encuentro.getEquipoVisitante().getEquipo().getLogoUrl());
                encuentroMap.put("equipoVisitante", visitanteMap);
                
                encuentroMap.put("golesLocal", encuentro.getGolesLocal());
                encuentroMap.put("golesVisitante", encuentro.getGolesVisitante());
                
                resultado.add(encuentroMap);
            }
        }
        
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerEncuentrosActivos(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));

        List<Map<String, Object>> resultado = new ArrayList<>();
        List<Fase> fases = faseRepository.findByTorneoIdOrderByNumeroFase(torneoId);

        for (Fase fase : fases) {
            List<Encuentro> encuentros = encuentroRepository.findByFaseIdOrderByFecha(fase.getId());
            for (Encuentro encuentro : encuentros) {
                // Solo incluir encuentros que no estén finalizados
                if (encuentro.getEstado() == com.GolsystemV2.Backend.enums.EstadoEncuentro.FINALIZADO) {
                    continue;
                }

                Map<String, Object> encuentroMap = new HashMap<>();
                encuentroMap.put("id", encuentro.getId());
                encuentroMap.put("fechaProgramada", encuentro.getFecha());
                encuentroMap.put("estado", encuentro.getEstado());

                Map<String, Object> faseMap = new HashMap<>();
                faseMap.put("id", fase.getId());
                faseMap.put("nombre", "Fase " + fase.getNumeroFase());
                encuentroMap.put("fase", faseMap);

                Map<String, Object> localMap = new HashMap<>();
                localMap.put("id", encuentro.getEquipoLocal().getId());
                localMap.put("nombre", encuentro.getEquipoLocal().getEquipo().getNombre());
                localMap.put("logoUrl", encuentro.getEquipoLocal().getEquipo().getLogoUrl());
                encuentroMap.put("equipoLocal", localMap);

                Map<String, Object> visitanteMap = new HashMap<>();
                visitanteMap.put("id", encuentro.getEquipoVisitante().getId());
                visitanteMap.put("nombre", encuentro.getEquipoVisitante().getEquipo().getNombre());
                visitanteMap.put("logoUrl", encuentro.getEquipoVisitante().getEquipo().getLogoUrl());
                encuentroMap.put("equipoVisitante", visitanteMap);

                encuentroMap.put("golesLocal", encuentro.getGolesLocal());
                encuentroMap.put("golesVisitante", encuentro.getGolesVisitante());

                resultado.add(encuentroMap);
            }
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Contar equipos inscritos
        List<EquipoTorneo> equipos = equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        estadisticas.put("totalEquipos", equipos.size());
        
        // Contar fases
        List<Fase> fases = faseRepository.findByTorneoIdOrderByNumeroFase(torneoId);
        estadisticas.put("totalFases", fases.size());
        
        // Contar grupos
        int totalGrupos = 0;
        for (Fase fase : fases) {
            totalGrupos += grupoRepository.countByFaseId(fase.getId());
        }
        estadisticas.put("totalGrupos", totalGrupos);
        
        return estadisticas;
    }
}
