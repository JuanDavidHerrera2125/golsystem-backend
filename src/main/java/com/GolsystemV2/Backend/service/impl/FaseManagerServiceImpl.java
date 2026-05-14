package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.dto.EquipoInfoDto;
import com.GolsystemV2.Backend.dto.EquipoRefDto;
import com.GolsystemV2.Backend.dto.FaseConfigDto;
import com.GolsystemV2.Backend.dto.FaseEstadoDto;
import com.GolsystemV2.Backend.entity.Equipo;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoFase;
import com.GolsystemV2.Backend.enums.TipoFase;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.EquipoRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.repository.TablaPosicionesRepository;
import com.GolsystemV2.Backend.repository.TorneoRepository;
import com.GolsystemV2.Backend.service.CampeonHistoricoService;
import com.GolsystemV2.Backend.service.FaseManagerService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import com.GolsystemV2.Backend.service.TablaPosicionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FaseManagerServiceImpl — Motor de ciclo de vida de la Fase.
 *
 * Implementa las 6 reglas de negocio del módulo de Fases:
 *  1. Estado de Bloqueo: EN_CURSO y FINALIZADA bloquean cambios estructurales.
 *  2. Configuración de Grupos: 1 a 15 grupos, validación de equipos disponibles.
 *  3. Progresión Lineal: N solo se activa si N-1 está FINALIZADA.
 *  4. Distribución Equitativa: Snake Draft — 22/4 → 6,6,5,5.
 *  5. Disparador de Históricos: cerrarFase() → GoleadorHistorico + CampeonHistorico.
 *  6. Integridad Referencial: un equipo no puede estar en 2 grupos de la misma fase.
 */
@Service
@Transactional
public class FaseManagerServiceImpl implements FaseManagerService {

    private static final int MIN_GRUPOS = 1;
    private static final int MAX_GRUPOS = 15;

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;

    @Autowired
    private EncuentroRepository encuentroRepository;

    @Autowired
    private TablaPosicionesService tablaPosicionesService;

    @Autowired
    private TablaPosicionesRepository tablaPosicionesRepository;

    @Autowired
    private GoleadorHistoricoService goleadorHistoricoService;

    @Autowired
    private CampeonHistoricoService campeonHistoricoService;

    @Autowired
    private EquipoRepository equipoRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // CONFIGURAR FASE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FaseEstadoDto configurarFase(FaseConfigDto dto) {
        // 1. Obtener torneo
        Torneo torneo = torneoRepository.findById(dto.getTorneoId())
                .orElseThrow(() -> new RuntimeException(
                        "Torneo no encontrado con ID: " + dto.getTorneoId()));

        // 2. Validar progresión lineal
        validarSecuenciaLineal(dto.getTorneoId(), dto.getNumeroFase());

        // 3. Verificar si ya existe una fase con ese número
        Optional<Fase> faseExistenteOpt = faseRepository
                .findByTorneoIdAndNumeroFase(dto.getTorneoId(), dto.getNumeroFase());
        
        if (faseExistenteOpt.isPresent()) {
            Fase faseExistente = faseExistenteOpt.get();
            
            // Si la fase existe pero está en CONFIGURACION, permitir actualización
            if (EstadoFase.CONFIGURACION.equals(faseExistente.getEstadoFase())) {
                System.out.println("[FaseManager] Actualizando Fase " + dto.getNumeroFase() + 
                                  " existente en CONFIGURACION");
                return actualizarFaseExistente(faseExistente, dto);
            }
            
            // Si la fase está en EN_CURSO o FINALIZADA, no permitir modificaciones
            throw new RuntimeException(
                    "Ya existe la Fase " + dto.getNumeroFase() + " en estado " + 
                    faseExistente.getEstadoFase() + ". No se puede reconfigurar.");
        }

        // 4. Validar cantidad de grupos (1–15)
        // Para Fase 1: respetar configuración del usuario
        // Para Fase N>1: usar cantidad del torneo como fuente de verdad
        Integer cantidadGrupos;
        if (dto.getNumeroFase() == 1) {
            cantidadGrupos = dto.getCantidadGrupos();
        } else {
            cantidadGrupos = torneo.getCantidadDeGrupos() != null && torneo.getCantidadDeGrupos() > 0
                ? torneo.getCantidadDeGrupos()
                : dto.getCantidadGrupos();
        }
        if (cantidadGrupos != null) {
            validarCantidadGrupos(cantidadGrupos);
        }

        // 5. Validar equipos seleccionados (solo para Fase 1)
        // Para Fase N>1, los equipos se obtienen automáticamente de la fase anterior
        List<EquipoRefDto> equiposParaFase = dto.getEquipos();
        
        if (dto.getNumeroFase() == 1) {
            if (equiposParaFase == null || equiposParaFase.isEmpty()) {
                throw new RuntimeException(
                        "Debes seleccionar al menos 1 equipo para la Fase 1.");
            }
        } else {
            // Fase N>1: obtener equipos clasificados de la fase anterior
            Integer numeroFaseAnterior = dto.getNumeroFase() - 1;
            Fase faseAnterior = faseRepository
                    .findByTorneoIdAndNumeroFase(dto.getTorneoId(), numeroFaseAnterior)
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró la Fase " + numeroFaseAnterior + ". " +
                            "Debe existir y estar FINALIZADA para crear la Fase " + dto.getNumeroFase()));
            
            // Verificar que la fase anterior esté finalizada
            if (!EstadoFase.FINALIZADA.equals(faseAnterior.getEstadoFase())) {
                throw new RuntimeException(
                        "La Fase " + numeroFaseAnterior + " debe estar FINALIZADA para crear la Fase " + 
                        dto.getNumeroFase() + ". Estado actual: " + faseAnterior.getEstadoFase());
            }
            
            // Obtener clasificados según tabla de posiciones
            List<EquipoTorneo> clasificados = obtenerClasificadosDeFaseAnteriorPorTabla(faseAnterior);
            
            // Convertir a DTOs
            equiposParaFase = new ArrayList<>();
            for (EquipoTorneo et : clasificados) {
                EquipoRefDto ref = new EquipoRefDto();
                ref.setId(et.getEquipo().getId());
                equiposParaFase.add(ref);
            }
            
            System.out.println("[FaseManager] Fase " + dto.getNumeroFase() + ": " + 
                              equiposParaFase.size() + " equipos clasificados de Fase " + numeroFaseAnterior);
        }

        // 6. Para Fase 1: validar que hay suficientes equipos para los grupos
        if (dto.getNumeroFase() == 1) {
            int totalEquiposSeleccionados = dto.getEquipos().size();
            if (totalEquiposSeleccionados < 2) {
                throw new RuntimeException(
                        "Se necesitan al menos 2 equipos para configurar la Fase 1.");
            }
            if (cantidadGrupos != null && totalEquiposSeleccionados < cantidadGrupos) {
                throw new RuntimeException(
                        "No hay suficientes equipos seleccionados (" + totalEquiposSeleccionados + ") para llenar " +
                        cantidadGrupos + " grupos. Se necesita mínimo 1 equipo por grupo.");
            }
        }

        // 7. Para Fase N>1: validar que equipos clasificados no superen equipos de fase anterior
        if (dto.getNumeroFase() > 1 && dto.getEquiposClasificadosPorGrupo() != null && cantidadGrupos != null) {
            int clasificadosEstaFase = cantidadGrupos * dto.getEquiposClasificadosPorGrupo();
            
            // Buscar fase anterior
            Integer numeroFaseAnterior = dto.getNumeroFase() - 1;
            Optional<Fase> faseAnteriorOpt = faseRepository.findByTorneoIdAndNumeroFase(dto.getTorneoId(), numeroFaseAnterior);
            
            if (faseAnteriorOpt.isPresent()) {
                Fase faseAnterior = faseAnteriorOpt.get();
                int equiposFaseAnterior = faseAnterior.getEquipos() != null ? faseAnterior.getEquipos().size() : 0;
                
                if (clasificadosEstaFase > equiposFaseAnterior) {
                    throw new RuntimeException(
                            "La cantidad de clasificados (" + clasificadosEstaFase + ") " +
                            "no puede superar la cantidad de equipos de la fase anterior (" + equiposFaseAnterior + "). " +
                            "Ajusta 'equiposClasificadosPorGrupo' o 'cantidadGrupos'.");
                }
            }
        }

        // 9. Crear la fase
        Fase nuevaFase = new Fase();
        nuevaFase.setTorneo(torneo);
        nuevaFase.setNumeroFase(dto.getNumeroFase());
        nuevaFase.setTipoFase(dto.getTipoFase() != null ? dto.getTipoFase() : TipoFase.GRUPOS);
        nuevaFase.setFormatoEncuentro(dto.getFormatoEncuentro());
        nuevaFase.setEstadoFase(EstadoFase.CONFIGURACION);
        nuevaFase.setCantidadGrupos(cantidadGrupos);
        nuevaFase.setEquiposClasificadosPorGrupo(dto.getEquiposClasificadosPorGrupo());
        nuevaFase.setMetodoDesempatePlayoff(dto.getMetodoDesempatePlayoff());

        if (cantidadGrupos != null && dto.getEquiposClasificadosPorGrupo() != null) {
            nuevaFase.setClasificadosSiguienteRonda(
                    cantidadGrupos * dto.getEquiposClasificadosPorGrupo());
        }

        Fase guardada = faseRepository.save(nuevaFase);

        // 10. Asignar equipos a la fase (seleccionados para Fase 1, clasificados para Fase N>1)
        asignarEquiposAFase(guardada, equiposParaFase);

        // 11. Pre-crear los grupos vacíos
        if (cantidadGrupos != null && cantidadGrupos > 0) {
            crearGruposVacios(guardada, cantidadGrupos);
        }

        return construirFaseEstadoDto(guardada);
    }

    /**
     * Actualiza una fase existente que está en estado CONFIGURACION.
     * Permite reconfigurar equipos, cantidad de grupos, etc.
     */
    private FaseEstadoDto actualizarFaseExistente(Fase faseExistente, FaseConfigDto dto) {
        System.out.println("[FaseManager] Actualizando fase existente ID=" + faseExistente.getId() + 
                          " Numero=" + dto.getNumeroFase());
        System.out.println("[FaseManager] DTO recibido - cantidadGrupos=" + dto.getCantidadGrupos() + 
                          ", equiposClasificadosPorGrupo=" + dto.getEquiposClasificadosPorGrupo());
        
        // Para Fase 1: respetar configuración del usuario
        // Para Fase N>1: usar cantidad del torneo como fuente de verdad
        Integer cantidadGruposDelTorneo;
        if (dto.getNumeroFase() == 1) {
            cantidadGruposDelTorneo = dto.getCantidadGrupos();
        } else {
            cantidadGruposDelTorneo = faseExistente.getTorneo().getCantidadDeGrupos();
        }
        if (cantidadGruposDelTorneo != null && cantidadGruposDelTorneo > 0) {
            validarCantidadGrupos(cantidadGruposDelTorneo);
        }

        // Para Fase 1: validar equipos seleccionados
        if (dto.getNumeroFase() == 1) {
            if (dto.getEquipos() == null || dto.getEquipos().isEmpty()) {
                throw new RuntimeException(
                        "Debes seleccionar al menos 1 equipo para la Fase 1.");
            }
            
            int totalEquiposSeleccionados = dto.getEquipos().size();
            if (totalEquiposSeleccionados < 2) {
                throw new RuntimeException(
                        "Se necesitan al menos 2 equipos para configurar la Fase 1.");
            }
            // Validar contra la cantidad de grupos del torneo si está disponible
            Integer gruposParaValidar = cantidadGruposDelTorneo != null && cantidadGruposDelTorneo > 0 
                ? cantidadGruposDelTorneo 
                : dto.getCantidadGrupos();
            if (gruposParaValidar != null && totalEquiposSeleccionados < gruposParaValidar) {
                throw new RuntimeException(
                        "No hay suficientes equipos seleccionados (" + totalEquiposSeleccionados + ") para llenar " +
                        gruposParaValidar + " grupos. Se necesita mínimo 1 equipo por grupo.");
            }
        }

        // Actualizar campos de la fase
        if (dto.getTipoFase() != null) {
            faseExistente.setTipoFase(dto.getTipoFase());
        }
        if (dto.getFormatoEncuentro() != null) {
            faseExistente.setFormatoEncuentro(dto.getFormatoEncuentro());
        }
        // USAR cantidad de grupos del TORNEO, no del DTO
        if (cantidadGruposDelTorneo != null && cantidadGruposDelTorneo > 0) {
            faseExistente.setCantidadGrupos(cantidadGruposDelTorneo);
        } else if (dto.getCantidadGrupos() != null) {
            faseExistente.setCantidadGrupos(dto.getCantidadGrupos());
        }
        if (dto.getEquiposClasificadosPorGrupo() != null) {
            faseExistente.setEquiposClasificadosPorGrupo(dto.getEquiposClasificadosPorGrupo());
        }
        if (dto.getMetodoDesempatePlayoff() != null) {
            faseExistente.setMetodoDesempatePlayoff(dto.getMetodoDesempatePlayoff());
        }

        // Recalcular clasificados para siguiente ronda
        Integer cantidadGruposFinal = faseExistente.getCantidadGrupos();
        if (cantidadGruposFinal != null && dto.getEquiposClasificadosPorGrupo() != null) {
            faseExistente.setClasificadosSiguienteRonda(
                    cantidadGruposFinal * dto.getEquiposClasificadosPorGrupo());
        }

        Fase guardada = faseRepository.save(faseExistente);

        // Para Fase 1: actualizar equipos asignados
        if (dto.getNumeroFase() == 1 && dto.getEquipos() != null && !dto.getEquipos().isEmpty()) {
            // Limpiar equipos actuales y asignar nuevos
            guardada.getEquipos().clear();
            asignarEquiposAFase(guardada, dto.getEquipos());
        }

        // Si hay cantidad de grupos configurada, recrear grupos y redistribuir equipos
        Integer cantidadGruposParaRecrear = cantidadGruposDelTorneo != null && cantidadGruposDelTorneo > 0
            ? cantidadGruposDelTorneo
            : dto.getCantidadGrupos();
        if (cantidadGruposParaRecrear != null && cantidadGruposParaRecrear > 0) {
            // Primero desasociar TODOS los equipos de sus grupos actuales
            // para que crearGruposVacios pueda eliminar los grupos sin problemas
            List<EquipoTorneo> equiposEnFase = equipoTorneoRepository.findByEquipoFasesId(guardada.getId());
            for (EquipoTorneo et : equiposEnFase) {
                if (et.getGrupo() != null) {
                    et.setGrupo(null);
                    equipoTorneoRepository.save(et);
                }
            }
            
            // Crear nuevos grupos vacíos (esto elimina los grupos existentes y sus tablas)
            crearGruposVacios(guardada, cantidadGruposParaRecrear);
            
            // Redistribuir equipos en los nuevos grupos
            List<EquipoTorneo> equiposParaDistribuir = obtenerEquiposParaFase(guardada);
            distribuirEquiposEnGruposDeFase(guardada.getId(), equiposParaDistribuir);
            
            System.out.println("[FaseManager] Grupos recreados: " + cantidadGruposParaRecrear + 
                              " grupos, " + equiposParaDistribuir.size() + " equipos distribuidos");
        }

        System.out.println("[FaseManager] Fase " + dto.getNumeroFase() + " actualizada exitosamente");
        System.out.println("[FaseManager] Valores finales guardados - cantidadGrupos=" + guardada.getCantidadGrupos() + 
                          ", equiposClasificadosPorGrupo=" + guardada.getEquiposClasificadosPorGrupo());
        return construirFaseEstadoDto(guardada);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACTIVAR FASE (CONFIGURACION → EN_CURSO)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FaseEstadoDto activarFase(Long faseId) {
        Fase fase = obtenerFaseOExcepcion(faseId);

        // 1. Validar que está en CONFIGURACION
        if (!EstadoFase.CONFIGURACION.equals(fase.getEstadoFase())) {
            throw new RuntimeException(
                    "Solo se puede activar una fase en estado CONFIGURACION. " +
                    "Estado actual: " + fase.getEstadoFase());
        }

        // 2. Validar que tiene grupos configurados
        List<Grupo> grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(faseId);
        if (grupos.isEmpty()) {
            throw new RuntimeException(
                    "La fase no tiene grupos configurados. " +
                    "Configura primero la cantidad de grupos antes de activar.");
        }

        // 3. Obtener equipos a distribuir
        List<EquipoTorneo> equiposADistribuir = obtenerEquiposParaFase(fase);

        if (equiposADistribuir.size() < 2) {
            throw new RuntimeException(
                    "Se necesitan al menos 2 equipos para activar la fase. " +
                    "Equipos disponibles: " + equiposADistribuir.size());
        }

        if (equiposADistribuir.size() < grupos.size()) {
            throw new RuntimeException(
                    "No hay suficientes equipos (" + equiposADistribuir.size() + ") " +
                    "para distribuir en " + grupos.size() + " grupos.");
        }

        // 4. Distribuir equipos en grupos (Snake Draft equitativo)
        distribuirEquiposEnGruposDeFase(faseId, equiposADistribuir);

        // 5. Validar integridad referencial (ningún equipo en 2 grupos)
        List<EquipoTorneo> duplicados = equipoTorneoRepository.findEquiposDuplicadosEnFase(faseId);
        if (!duplicados.isEmpty()) {
            throw new RuntimeException(
                    "Error de integridad: hay equipos asignados a más de un grupo en la misma fase. " +
                    "Cantidad de duplicados: " + duplicados.size());
        }

        // 6. Inicializar tablas de posiciones por equipo por grupo
        grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(faseId);
        for (Grupo grupo : grupos) {
            List<EquipoTorneo> equiposDelGrupo =
                    equipoTorneoRepository.findByGrupoIdAndEliminadoFalse(grupo.getId());
            for (EquipoTorneo et : equiposDelGrupo) {
                tablaPosicionesService.inicializarTablaParaEquipo(grupo.getId(), et.getId());
            }
        }

        // 7. Cambiar estado a EN_CURSO — BLOQUEO ACTIVADO
        fase.setEstadoFase(EstadoFase.EN_CURSO);
        Fase actualizada = faseRepository.save(fase);

        return construirFaseEstadoDto(actualizada);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CERRAR FASE (EN_CURSO → FINALIZADA)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FaseEstadoDto cerrarFase(Long faseId) {
        Fase fase = obtenerFaseOExcepcion(faseId);

        // 1. Solo se puede cerrar una fase EN_CURSO
        if (!EstadoFase.EN_CURSO.equals(fase.getEstadoFase())) {
            throw new RuntimeException(
                    "Solo se puede cerrar una fase EN_CURSO. " +
                    "Estado actual: " + fase.getEstadoFase());
        }

        // 2. Validar que TODOS los encuentros de la fase estén finalizados
        List<com.GolsystemV2.Backend.entity.Encuentro> pendientes =
                encuentroRepository.findPartidosNoFinalizadosPorFase(faseId);

        if (!pendientes.isEmpty()) {
            throw new RuntimeException(
                    "No se puede cerrar la fase. Hay " + pendientes.size() +
                    " partido(s) sin finalizar. Todos los partidos deben estar FINALIZADO.");
        }

        // 3. Disparar evento de histórico ANTES de cerrar
        dispararEventoHistorico(faseId);

        // 4. Marcar como FINALIZADA
        fase.setEstadoFase(EstadoFase.FINALIZADA);
        Fase actualizada = faseRepository.save(fase);

        return construirFaseEstadoDto(actualizada);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AVANZAR SIGUIENTE FASE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FaseEstadoDto avanzarSiguienteFase(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + torneoId));

        // 1. Obtener la fase con mayor número para este torneo
        Integer maxNumero = faseRepository.findMaxNumeroFaseByTorneoId(torneoId);
        if (maxNumero == null) {
            throw new RuntimeException(
                    "No existe ninguna fase para este torneo. " +
                    "Crea la Fase 1 primero con POST /api/fases/configurar.");
        }

        // 2. Verificar que la última fase está FINALIZADA
        Fase ultimaFase = faseRepository.findByTorneoIdAndNumeroFase(torneoId, maxNumero)
                .orElseThrow(() -> new RuntimeException("No se encontró la Fase " + maxNumero));

        if (!EstadoFase.FINALIZADA.equals(ultimaFase.getEstadoFase())) {
            throw new RuntimeException(
                    "No se puede avanzar a la Fase " + (maxNumero + 1) +
                    ". La Fase " + maxNumero + " debe estar FINALIZADA. " +
                    "Estado actual: " + ultimaFase.getEstadoFase());
        }

        // 3. Crear la nueva fase en CONFIGURACION
        Integer siguienteNumero = maxNumero + 1;
        Fase nuevaFase = new Fase();
        nuevaFase.setTorneo(torneo);
        nuevaFase.setNumeroFase(siguienteNumero);
        nuevaFase.setTipoFase(TipoFase.GRUPOS); // Default GRUPOS, admin puede cambiarlo
        nuevaFase.setEstadoFase(EstadoFase.CONFIGURACION);
        // Hereda el formato de la fase anterior como sugerencia
        nuevaFase.setFormatoEncuentro(ultimaFase.getFormatoEncuentro());

        Fase guardada = faseRepository.save(nuevaFase);

        return construirFaseEstadoDto(guardada);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OBTENER ESTADO ENRIQUECIDO
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public FaseEstadoDto obtenerEstadoFase(Long faseId) {
        Fase fase = obtenerFaseOExcepcion(faseId);
        return construirFaseEstadoDto(fase);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUEDE ACTIVARSE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public boolean puedeActivarFase(Long faseId) {
        Fase fase = obtenerFaseOExcepcion(faseId);

        if (!EstadoFase.CONFIGURACION.equals(fase.getEstadoFase())) {
            return false;
        }

        if (grupoRepository.countByFaseId(faseId) == 0) {
            return false;
        }

        List<EquipoTorneo> equipos = obtenerEquiposParaFase(fase);
        return equipos.size() >= 2;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDAR SECUENCIA LINEAL
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public void validarSecuenciaLineal(Long torneoId, Integer numeroFaseObjetivo) {
        if (numeroFaseObjetivo == null || numeroFaseObjetivo < 1) {
            throw new RuntimeException("El número de fase debe ser un valor positivo (mínimo 1).");
        }

        // La Fase 1 siempre se puede crear si no existe
        if (numeroFaseObjetivo == 1) {
            return;
        }

        // Para Fase N > 1: la fase N-1 debe existir y estar FINALIZADA
        Integer numeroFaseAnterior = numeroFaseObjetivo - 1;
        boolean faseAnteriorFinalizada =
                faseRepository.existsFaseAnteriorFinalizada(torneoId, numeroFaseAnterior);

        if (!faseAnteriorFinalizada) {
            throw new RuntimeException(
                    "No se puede crear la Fase " + numeroFaseObjetivo + ". " +
                    "La Fase " + numeroFaseAnterior +
                    " debe existir y estar en estado FINALIZADA. " +
                    "Los saltos entre fases no están permitidos.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DISPARADOR DE HISTÓRICOS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void dispararEventoHistorico(Long faseId) {
        Fase fase = obtenerFaseOExcepcion(faseId);
        Long torneoId = fase.getTorneo().getId();

        try {
            // 1. Actualizar goleadores históricos del torneo
            goleadorHistoricoService.registrarGoleadoresDelTorneo(torneoId);
        } catch (Exception e) {
            System.err.println("[FaseManager] Error al actualizar GoleadorHistorico: " + e.getMessage());
        }

        try {
            // 2. Si es la última fase (no hay siguiente), registrar campeón
            Integer maxNumero = faseRepository.findMaxNumeroFaseByTorneoId(torneoId);
            if (fase.getNumeroFase().equals(maxNumero)) {
                // Intentar determinar el campeón a partir de esta fase
                registrarCampeonSiFinal(fase, torneoId);
            }
        } catch (Exception e) {
            System.err.println("[FaseManager] Error al registrar CampeonHistorico: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DISTRIBUCIÓN EQUITATIVA (SNAKE DRAFT)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Distribuye equipos equitativamente entre los grupos de la fase usando Snake Draft.
     *
     * Ejemplo con 22 equipos y 4 grupos:
     *   baseSize = 22 / 4 = 5
     *   extras   = 22 % 4 = 2  →  los 2 primeros grupos reciben 1 extra
     *   Resultado: G1:6, G2:6, G3:5, G4:5
     *
     * La distribución snake (zigzag) balancea la calidad de los equipos entre grupos.
     */
    private void distribuirEquiposEnGruposDeFase(Long faseId, List<EquipoTorneo> equipos) {
        List<Grupo> grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(faseId);

        if (grupos.isEmpty()) {
            throw new RuntimeException("No hay grupos en la fase. Créalos primero.");
        }

        int totalEquipos = equipos.size();
        int cantGrupos = grupos.size();

        // Ordenar equipos por ID para distribución determinista
        equipos.sort((e1, e2) -> e1.getId().compareTo(e2.getId()));

        // Algoritmo Snake Draft
        int equipoIndex = 0;
        boolean ascendente = true;

        while (equipoIndex < totalEquipos) {
            if (ascendente) {
                for (int i = 0; i < cantGrupos && equipoIndex < totalEquipos; i++) {
                    EquipoTorneo et = equipos.get(equipoIndex);
                    et.setGrupo(grupos.get(i));
                    equipoTorneoRepository.save(et);
                    equipoIndex++;
                }
            } else {
                for (int i = cantGrupos - 1; i >= 0 && equipoIndex < totalEquipos; i--) {
                    EquipoTorneo et = equipos.get(equipoIndex);
                    et.setGrupo(grupos.get(i));
                    equipoTorneoRepository.save(et);
                    equipoIndex++;
                }
            }
            ascendente = !ascendente;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODOS PRIVADOS DE SOPORTE
    // ─────────────────────────────────────────────────────────────────────────

    private Fase obtenerFaseOExcepcion(Long faseId) {
        return faseRepository.findByIdWithEquipos(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + faseId));
    }

    private void validarCantidadGrupos(Integer cantidadGrupos) {
        if (cantidadGrupos < MIN_GRUPOS || cantidadGrupos > MAX_GRUPOS) {
            throw new RuntimeException(
                    "La cantidad de grupos debe estar entre " + MIN_GRUPOS +
                    " y " + MAX_GRUPOS + ". Valor recibido: " + cantidadGrupos);
        }
    }

    /**
     * Asigna equipos a una fase mediante la relación ManyToMany.
     * Crea las relaciones EquipoTorneo si no existen.
     */
    private void asignarEquiposAFase(Fase fase, List<EquipoRefDto> equiposDto) {
        if (equiposDto == null || equiposDto.isEmpty()) {
            return;
        }

        List<Equipo> equipos = new ArrayList<>();
        for (EquipoRefDto dto : equiposDto) {
            Equipo equipo = equipoRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + dto.getId()));
            equipos.add(equipo);

            // Verificar si ya existe EquipoTorneo para este equipo y torneo
            Optional<EquipoTorneo> etOpt = equipoTorneoRepository
                    .findByTorneoIdAndEquipoId(fase.getTorneo().getId(), equipo.getId());
            
            if (etOpt.isEmpty()) {
                // Crear nueva relación EquipoTorneo
                EquipoTorneo nuevoEt = new EquipoTorneo();
                nuevoEt.setEquipo(equipo);
                nuevoEt.setTorneo(fase.getTorneo());
                nuevoEt.setEliminado(false);
                equipoTorneoRepository.save(nuevoEt);
            }
        }

        // Asignar equipos a la fase
        fase.setEquipos(equipos);
        faseRepository.save(fase);
        
        System.out.println("[FaseManager] " + equipos.size() + " equipos asignados a Fase " + fase.getNumeroFase());
    }

    /**
     * Crea grupos vacíos nominados A, B, C… para la fase dada.
     * Elimina todos los grupos existentes primero (asumiendo que equipos ya fueron desasociados).
     */
    private void crearGruposVacios(Fase fase, int cantidad) {
        List<Grupo> existentes = grupoRepository.findByFaseIdOrderByNombreGrupo(fase.getId());

        // Eliminar TODOS los grupos existentes y sus tablas de posiciones
        for (Grupo g : existentes) {
            // Eliminar tablas de posiciones asociadas
            List<com.GolsystemV2.Backend.entity.TablaPosiciones> tablas = 
                    tablaPosicionesRepository.findByGrupoId(g.getId());
            for (com.GolsystemV2.Backend.entity.TablaPosiciones tabla : tablas) {
                tablaPosicionesRepository.delete(tabla);
            }
            // Eliminar el grupo (los equipos deben estar desasociados previamente)
            grupoRepository.delete(g);
        }

        // Crear nuevos grupos
        for (int i = 1; i <= cantidad; i++) {
            char letra = (char) ('A' + i - 1);
            String nombre = "Grupo " + letra;
            Grupo nuevoGrupo = new Grupo();
            nuevoGrupo.setFase(fase);
            nuevoGrupo.setNombreGrupo(nombre);
            grupoRepository.save(nuevoGrupo);
        }
    }

    /**
     * Determina qué equipos deben participar en esta fase:
     *  - Si es Fase 1: equipos asignados a la fase (seleccionados en configuración).
     *  - Si es Fase N>1: equipos que clasificaron de la fase anterior según tabla de posiciones.
     */
    private List<EquipoTorneo> obtenerEquiposParaFase(Fase fase) {
        Long torneoId = fase.getTorneo().getId();
        Integer numeroFase = fase.getNumeroFase();

        if (numeroFase == 1) {
            // Primera fase: equipos asignados a la fase mediante ManyToMany
            if (fase.getEquipos() == null || fase.getEquipos().isEmpty()) {
                throw new RuntimeException(
                        "La fase no tiene equipos asignados. " +
                        "Selecciona equipos antes de activar la fase.");
            }

            // Convertir equipos (Equipo) a EquipoTorneo
            List<EquipoTorneo> equiposTorneo = new ArrayList<>();
            for (Equipo equipo : fase.getEquipos()) {
                EquipoTorneo et = equipoTorneoRepository
                        .findByTorneoIdAndEquipoId(torneoId, equipo.getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Equipo ID " + equipo.getId() + " no está inscrito en el torneo."));
                equiposTorneo.add(et);
            }
            return equiposTorneo;
        } else {
            // Fase N>1: obtener clasificados según tabla de posiciones de la fase anterior
            Integer numeroFaseAnterior = fase.getNumeroFase() - 1;
            Fase faseAnterior = faseRepository
                    .findByTorneoIdAndNumeroFase(torneoId, numeroFaseAnterior)
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró la Fase " + numeroFaseAnterior + " para obtener clasificados."));
            
            // Verificar que la fase anterior esté finalizada
            if (!EstadoFase.FINALIZADA.equals(faseAnterior.getEstadoFase())) {
                throw new RuntimeException(
                        "La Fase " + numeroFaseAnterior + " debe estar FINALIZADA para obtener clasificados. " +
                        "Estado actual: " + faseAnterior.getEstadoFase());
            }
            
            return obtenerClasificadosDeFaseAnteriorPorTabla(faseAnterior);
        }
    }
    
    /**
     * Obtiene los equipos clasificados de una fase específica según la tabla de posiciones.
     * Usado durante la configuración de Fase N>1 para obtener equipos automáticamente.
     */
    private List<EquipoTorneo> obtenerClasificadosDeFaseAnteriorPorTabla(Fase faseAnterior) {
        Integer equiposClasificadosPorGrupo = faseAnterior.getEquiposClasificadosPorGrupo();
        if (equiposClasificadosPorGrupo == null || equiposClasificadosPorGrupo < 1) {
            equiposClasificadosPorGrupo = 2; // Default: 2 por grupo
        }
        
        // Obtener grupos de la fase anterior
        List<Grupo> gruposAnterior = grupoRepository.findByFaseIdOrderByNombreGrupo(faseAnterior.getId());
        
        List<EquipoTorneo> clasificados = new ArrayList<>();
        
        for (Grupo grupo : gruposAnterior) {
            // Obtener tabla de posiciones ordenada por puntos, diferencia de gol, goles a favor
            List<com.GolsystemV2.Backend.entity.TablaPosiciones> tabla = 
                    tablaPosicionesService.findTablaPosicionesOrdenada(grupo.getId());
            
            if (tabla == null || tabla.isEmpty()) {
                System.out.println("[FaseManager] Grupo " + grupo.getNombreGrupo() + " sin tabla de posiciones");
                continue;
            }
            
            // Tomar los top N clasificados de este grupo
            int clasificadosDeEsteGrupo = Math.min(equiposClasificadosPorGrupo, tabla.size());
            
            System.out.println("[FaseManager] Grupo " + grupo.getNombreGrupo() + ": " + 
                              clasificadosDeEsteGrupo + " equipos clasifican de " + tabla.size());
            
            for (int i = 0; i < clasificadosDeEsteGrupo; i++) {
                com.GolsystemV2.Backend.entity.TablaPosiciones posicion = tabla.get(i);
                EquipoTorneo equipoClasificado = posicion.getEquipoTorneo();
                
                // Evitar duplicados
                if (!clasificados.contains(equipoClasificado)) {
                    clasificados.add(equipoClasificado);
                    System.out.println("[FaseManager]   - " + (i+1) + "°: " + 
                                      equipoClasificado.getEquipo().getNombre() + 
                                      " (" + posicion.getPts() + " pts, DG:" + posicion.getDg() + ")");
                }
            }
        }
        
        return clasificados;
    }

    /**
     * Intenta registrar el campeón al finalizar la última fase del torneo.
     */
    private void registrarCampeonSiFinal(Fase fase, Long torneoId) {
        if (TipoFase.ELIMINACION_DIRECTA.equals(fase.getTipoFase())) {
            // Campeón = ganador del último partido
            List<com.GolsystemV2.Backend.entity.Encuentro> encuentros =
                    encuentroRepository.findByFaseIdOrderByFechaDesc(fase.getId());
            if (!encuentros.isEmpty()) {
                com.GolsystemV2.Backend.entity.Encuentro granFinal = encuentros.get(0);
                com.GolsystemV2.Backend.entity.Equipo campeon = null;
                if (granFinal.getGolesLocal() != null && granFinal.getGolesVisitante() != null) {
                    if (granFinal.getGolesLocal() > granFinal.getGolesVisitante()) {
                        campeon = granFinal.getEquipoLocal().getEquipo();
                    } else if (granFinal.getGolesVisitante() > granFinal.getGolesLocal()) {
                        campeon = granFinal.getEquipoVisitante().getEquipo();
                    }
                }
                if (campeon != null) {
                    campeonHistoricoService.registrarCampeon(torneoId, campeon.getId());
                }
            }
        } else {
            // Campeón = equipo con más puntos en la tabla de la fase
            List<Grupo> grupos = grupoRepository.findByFaseIdOrderByNombreGrupo(fase.getId());
            if (!grupos.isEmpty()) {
                List<com.GolsystemV2.Backend.entity.TablaPosiciones> tabla =
                        tablaPosicionesService.findTablaPosicionesOrdenada(grupos.get(0).getId());
                if (!tabla.isEmpty()) {
                    com.GolsystemV2.Backend.entity.Equipo campeon =
                            tabla.get(0).getEquipoTorneo().getEquipo();
                    campeonHistoricoService.registrarCampeon(torneoId, campeon.getId());
                }
            }
        }
    }

    /**
     * Construye el DTO de estado enriquecido con métricas calculadas en tiempo real.
     */
    private FaseEstadoDto construirFaseEstadoDto(Fase fase) {
        FaseEstadoDto dto = new FaseEstadoDto();
        dto.setId(fase.getId());
        dto.setTorneoId(fase.getTorneo().getId());
        dto.setNumeroFase(fase.getNumeroFase());
        dto.setTipoFase(fase.getTipoFase());
        dto.setFormatoEncuentro(fase.getFormatoEncuentro());
        dto.setEstadoFase(fase.getEstadoFase());
        dto.setCantidadGrupos(fase.getCantidadGrupos());
        dto.setEquiposClasificadosPorGrupo(fase.getEquiposClasificadosPorGrupo());

        // Métricas en tiempo real
        Integer totalEquipos = equipoTorneoRepository.countEquiposPorFaseId(fase.getId());
        dto.setTotalEquipos(totalEquipos != null ? totalEquipos : 0);

        Integer totalPartidos = encuentroRepository.countTotalPartidosPorFase(fase.getId());
        Integer finalizados = encuentroRepository.countPartidosFinalizadosPorFase(fase.getId());
        dto.setTotalPartidos(totalPartidos != null ? totalPartidos : 0);
        dto.setPartidosFinalizados(finalizados != null ? finalizados : 0);
        dto.setPartidosPendientes(
                (totalPartidos != null ? totalPartidos : 0) -
                (finalizados != null ? finalizados : 0));

        // Porcentaje de avance
        if (totalPartidos != null && totalPartidos > 0) {
            dto.setPorcentajeAvance((finalizados * 100) / totalPartidos);
        } else {
            dto.setPorcentajeAvance(0);
        }

        // Flags de control
        dto.setBloqueadaParaEdicion(fase.estaBloqueadaParaEdicion());
        dto.setPuedeActivarse(puedeActivarFase(fase.getId()));
        dto.setPuedeCerrarse(
                EstadoFase.EN_CURSO.equals(fase.getEstadoFase()) &&
                (dto.getPartidosPendientes() == 0) &&
                (dto.getTotalPartidos() > 0));

        // Equipos asignados a la fase
        if (fase.getEquipos() != null && !fase.getEquipos().isEmpty()) {
            List<EquipoInfoDto> equiposDto = fase.getEquipos().stream()
                .map(e -> new EquipoInfoDto(e.getId(), e.getNombre(), e.getLogoUrl(), e.getCodigoEquipo()))
                .toList();
            dto.setEquipos(equiposDto);
        }

        return dto;
    }
}
