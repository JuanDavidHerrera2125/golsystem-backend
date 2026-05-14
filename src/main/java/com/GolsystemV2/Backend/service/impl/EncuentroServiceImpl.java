package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.repository.FaseRepository;
import com.GolsystemV2.Backend.entity.EventoPartido;
import com.GolsystemV2.Backend.repository.EventoPartidoRepository;
import com.GolsystemV2.Backend.service.EncuentroService;
import com.GolsystemV2.Backend.service.TablaPosicionesService;
import com.GolsystemV2.Backend.service.GoleadorHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class EncuentroServiceImpl implements EncuentroService {

    @Autowired
    private EncuentroRepository encuentroRepository;
    
    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;
    
    @Autowired
    private GrupoRepository grupoRepository;
    
    @Autowired
    private FaseRepository faseRepository;
    
    @Autowired
    private TablaPosicionesService tablaPosicionesService;

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findAll() {
        return encuentroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Encuentro> findById(Long id) {
        return encuentroRepository.findById(id);
    }

    @Override
    public Encuentro save(Encuentro encuentro) {
        return encuentroRepository.save(encuentro);
    }

    @Override
    public Encuentro update(Long id, Encuentro encuentro) {
        if (!validarModificacionPartido(id)) {
            throw new RuntimeException("No se puede modificar un partido que está finalizado");
        }
        
        Encuentro existingEncuentro = encuentroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + id));
        
        existingEncuentro.setFase(encuentro.getFase());
        existingEncuentro.setGrupo(encuentro.getGrupo());
        existingEncuentro.setEquipoLocal(encuentro.getEquipoLocal());
        existingEncuentro.setEquipoVisitante(encuentro.getEquipoVisitante());
        existingEncuentro.setFecha(encuentro.getFecha());
        existingEncuentro.setGolesLocal(encuentro.getGolesLocal());
        existingEncuentro.setGolesVisitante(encuentro.getGolesVisitante());
        existingEncuentro.setEstado(encuentro.getEstado());
        
        return encuentroRepository.save(existingEncuentro);
    }

    @Override
    public void deleteById(Long id) {
        if (!validarModificacionPartido(id)) {
            throw new RuntimeException("No se puede eliminar un partido que está finalizado");
        }
        
        if (!encuentroRepository.existsById(id)) {
            throw new RuntimeException("Encuentro no encontrado con ID: " + id);
        }
        encuentroRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFaseIdOrderByFecha(Long faseId) {
        return encuentroRepository.findByFaseIdOrderByFecha(faseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByGrupoIdOrderByFecha(Long grupoId) {
        return encuentroRepository.findByGrupoIdOrderByFecha(grupoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByEquipoId(Long equipoId) {
        return encuentroRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFaseIdAndEstado(Long faseId, EstadoEncuentro estado) {
        return encuentroRepository.findByFaseIdAndEstado(faseId, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encuentro> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return encuentroRepository.findByFechaBetween(inicio, fin);
    }

    @Override
    public Encuentro iniciarPartido(Long encuentroId) {
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        if (!EstadoEncuentro.PROGRAMADO.equals(encuentro.getEstado())) {
            throw new RuntimeException("Solo se pueden iniciar partidos programados");
        }
        
        encuentro.setEstado(EstadoEncuentro.EN_JUEGO);
        encuentro.setFecha(LocalDateTime.now());
        
        return encuentroRepository.save(encuentro);
    }

    @Override
    public Encuentro finalizarPartido(Long encuentroId, Integer golesLocal, Integer golesVisitante) {
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        if (!EstadoEncuentro.EN_JUEGO.equals(encuentro.getEstado())) {
            throw new RuntimeException("Solo se pueden finalizar partidos que están en juego");
        }
        
        encuentro.setGolesLocal(golesLocal);
        encuentro.setGolesVisitante(golesVisitante);
        encuentro.setEstado(EstadoEncuentro.FINALIZADO);
        
        return encuentroRepository.save(encuentro);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarModificacionPartido(Long encuentroId) {
        Optional<Encuentro> encuentroOpt = encuentroRepository.findById(encuentroId);
        
        if (encuentroOpt.isEmpty()) {
            return false;
        }
        
        Encuentro encuentro = encuentroOpt.get();
        return !EstadoEncuentro.FINALIZADO.equals(encuentro.getEstado());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarFinalizacionPartido(Long encuentroId) {
        Optional<Encuentro> encuentroOpt = encuentroRepository.findById(encuentroId);
        
        if (encuentroOpt.isEmpty()) {
            return false;
        }
        
        Encuentro encuentro = encuentroOpt.get();
        return EstadoEncuentro.EN_JUEGO.equals(encuentro.getEstado());
    }
    
    @Override
    @Transactional
    public List<Encuentro> generarFixtureGrupo(Long grupoId, String formatoEncuentro) {
        // 1. Obtener el grupo
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + grupoId));
        
        // 2. Obtener equipos del grupo
        List<EquipoTorneo> equipos = equipoTorneoRepository.findByGrupoIdAndEliminadoFalse(grupoId);
        
        if (equipos.size() < 2) {
            throw new RuntimeException("Se necesitan al menos 2 equipos para generar el fixture");
        }
        
        // 3. Eliminar encuentros existentes del grupo (para regenerar)
        List<Encuentro> encuentrosExistentes = encuentroRepository.findByGrupoIdOrderByFecha(grupoId);
        encuentroRepository.deleteAll(encuentrosExistentes);
        
        // 3.5. Inicializar tablas de posiciones para cada equipo si no existen
        for (EquipoTorneo equipo : equipos) {
            tablaPosicionesService.inicializarTablaParaEquipo(grupoId, equipo.getId());
        }
        
        // 4. Generar todos contra todos
        List<Encuentro> encuentrosGenerados = new ArrayList<>();
        int n = equipos.size();
        
        // Algoritmo round-robin para generar fixture
        // SOLO_IDA: cada equipo juega 1 vez contra cada rival
        // IDA_Y_VUELTA: cada equipo juega 2 veces (ida y vuelta)
        
        boolean idaYVuelta = "IDA_Y_VUELTA".equalsIgnoreCase(formatoEncuentro);
        
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                EquipoTorneo equipoLocal = equipos.get(i);
                EquipoTorneo equipoVisitante = equipos.get(j);
                
                // Partido de ida (i vs j)
                Encuentro encuentroIda = new Encuentro();
                encuentroIda.setFase(grupo.getFase());
                encuentroIda.setGrupo(grupo);
                encuentroIda.setEquipoLocal(equipoLocal);
                encuentroIda.setEquipoVisitante(equipoVisitante);
                encuentroIda.setEstado(EstadoEncuentro.PROGRAMADO);
                encuentrosGenerados.add(encuentroRepository.save(encuentroIda));
                
                if (idaYVuelta) {
                    // Partido de vuelta (j vs i)
                    Encuentro encuentroVuelta = new Encuentro();
                    encuentroVuelta.setFase(grupo.getFase());
                    encuentroVuelta.setGrupo(grupo);
                    encuentroVuelta.setEquipoLocal(equipoVisitante);
                    encuentroVuelta.setEquipoVisitante(equipoLocal);
                    encuentroVuelta.setEstado(EstadoEncuentro.PROGRAMADO);
                    encuentrosGenerados.add(encuentroRepository.save(encuentroVuelta));
                }
            }
        }
        
        return encuentrosGenerados;
    }
    
    @Override
    @Transactional
    public Map<String, Object> generarFixtureFase(Long faseId, String formatoEncuentro) {
        Fase fase = faseRepository.findById(faseId)
                .orElseThrow(() -> new RuntimeException("Fase no encontrada con ID: " + faseId));
        
        List<Grupo> grupos = grupoRepository.findByFaseId(faseId);
        Map<String, Object> resultado = new HashMap<>();
        Map<String, List<Encuentro>> encuentrosPorGrupo = new HashMap<>();
        int totalEncuentros = 0;
        
        for (Grupo grupo : grupos) {
            List<Encuentro> encuentros = generarFixtureGrupo(grupo.getId(), formatoEncuentro);
            encuentrosPorGrupo.put(grupo.getNombreGrupo(), encuentros);
            totalEncuentros += encuentros.size();
        }
        
        resultado.put("fase", fase);
        resultado.put("encuentrosPorGrupo", encuentrosPorGrupo);
        resultado.put("totalEncuentros", totalEncuentros);
        resultado.put("totalGrupos", grupos.size());
        resultado.put("mensaje", "Fixture generado exitosamente");
        
        return resultado;
    }
    
    @Autowired
    private GoleadorHistoricoService goleadorHistoricoService;

    @Autowired
    private EventoPartidoRepository eventoPartidoRepository;

    @Override
    @Transactional
    public Encuentro registrarResultado(Long encuentroId, Integer golesLocal, Integer golesVisitante) {
        if (golesLocal == null || golesVisitante == null || golesLocal < 0 || golesVisitante < 0) {
            throw new RuntimeException("Los goles deben ser números positivos");
        }
        
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        // Actualizar resultado
        encuentro.setGolesLocal(golesLocal);
        encuentro.setGolesVisitante(golesVisitante);
        encuentro.setEstado(EstadoEncuentro.FINALIZADO);
        encuentro.setFecha(LocalDateTime.now());
        
        Encuentro guardado = encuentroRepository.save(encuentro);
        
        // 1. Actualizar tablas de posiciones
        if (encuentro.getGrupo() != null) {
            tablaPosicionesService.actualizarTablaTrasResultado(encuentroId);
        }

        // 2. Actualizar Goleadores Históricos basados en Eventos
        List<EventoPartido> goles = eventoPartidoRepository.findByEncuentroIdAndTipoEvento(encuentroId, com.GolsystemV2.Backend.enums.TipoEvento.GOL);
        for (EventoPartido gol : goles) {
            if (gol.getJugadorEquipoTorneo() != null) {
                Long jugadorId = gol.getJugadorEquipoTorneo().getJugador().getId();
                Long equipoId = gol.getEquipoTorneo().getEquipo().getId();
                Long torneoId = encuentro.getFase().getTorneo().getId();
                
                // Contar todos los goles del jugador en este torneo
                Integer totalGolesTorneo = eventoPartidoRepository.countGolesPorJugadorEnTorneo(jugadorId, torneoId);
                
                goleadorHistoricoService.actualizarGoles(jugadorId, equipoId, torneoId, totalGolesTorneo);
            }
        }
        
        return guardado;
    }
}
