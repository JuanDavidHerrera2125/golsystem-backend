package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.entity.EquipoTorneo;
import com.GolsystemV2.Backend.entity.Grupo;
import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.repository.EquipoTorneoRepository;
import com.GolsystemV2.Backend.repository.GrupoRepository;
import com.GolsystemV2.Backend.repository.TablaPosicionesRepository;
import com.GolsystemV2.Backend.service.TablaPosicionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TablaPosicionesServiceImpl implements TablaPosicionesService {

    @Autowired
    private TablaPosicionesRepository tablaPosicionesRepository;
    
    @Autowired
    private EncuentroRepository encuentroRepository;
    
    @Autowired
    private EquipoTorneoRepository equipoTorneoRepository;
    
    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TablaPosiciones> findAll() {
        return tablaPosicionesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TablaPosiciones> findById(Long id) {
        return tablaPosicionesRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TablaPosiciones> findByGrupoIdAndEquipoTorneoId(Long grupoId, Long equipoTorneoId) {
        return tablaPosicionesRepository.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId);
    }

    @Override
    public TablaPosiciones save(TablaPosiciones tablaPosiciones) {
        return tablaPosicionesRepository.save(tablaPosiciones);
    }

    @Override
    public TablaPosiciones update(Long id, TablaPosiciones tablaPosiciones) {
        TablaPosiciones existingTablaPosiciones = tablaPosicionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TablaPosiciones no encontrada con ID: " + id));
        
        existingTablaPosiciones.setPj(tablaPosiciones.getPj());
        existingTablaPosiciones.setPg(tablaPosiciones.getPg());
        existingTablaPosiciones.setPe(tablaPosiciones.getPe());
        existingTablaPosiciones.setPp(tablaPosiciones.getPp());
        existingTablaPosiciones.setGf(tablaPosiciones.getGf());
        existingTablaPosiciones.setGc(tablaPosiciones.getGc());
        existingTablaPosiciones.setDg(tablaPosiciones.getDg());
        existingTablaPosiciones.setPts(tablaPosiciones.getPts());
        existingTablaPosiciones.setAmarillas(tablaPosiciones.getAmarillas());
        existingTablaPosiciones.setRojas(tablaPosiciones.getRojas());
        existingTablaPosiciones.setEstado(tablaPosiciones.getEstado());
        
        return tablaPosicionesRepository.save(existingTablaPosiciones);
    }

    @Override
    public void deleteById(Long id) {
        if (!tablaPosicionesRepository.existsById(id)) {
            throw new RuntimeException("TablaPosiciones no encontrada con ID: " + id);
        }
        tablaPosicionesRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TablaPosiciones> findByGrupoIdOrderByIdDesc(Long grupoId) {
        return tablaPosicionesRepository.findByGrupoIdOrderByIdDesc(grupoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TablaPosiciones> findTablaPosicionesOrdenada(Long grupoId) {
        return tablaPosicionesRepository.findTablaPosicionesOrdenada(grupoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TablaPosiciones> findByGrupoIdAndEstado(Long grupoId, EstadoTablaPosiciones estado) {
        return tablaPosicionesRepository.findByGrupoIdAndEstado(grupoId, estado);
    }

    @Override
    public TablaPosiciones cerrarTabla(Long id) {
        TablaPosiciones tablaPosiciones = tablaPosicionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TablaPosiciones no encontrada con ID: " + id));
        tablaPosiciones.setEstado(EstadoTablaPosiciones.CERRADA);
        return tablaPosicionesRepository.save(tablaPosiciones);
    }

    @Override
    public TablaPosiciones abrirTabla(Long id) {
        TablaPosiciones tablaPosiciones = tablaPosicionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TablaPosiciones no encontrada con ID: " + id));
        tablaPosiciones.setEstado(EstadoTablaPosiciones.ABIERTA);
        return tablaPosicionesRepository.save(tablaPosiciones);
    }

    @Override
    public void actualizarEstadisticas(Long grupoId, Long equipoTorneoId, Integer pg, Integer pe, Integer pp, Integer gf, Integer gc, Integer amarillas, Integer rojas) {
        System.out.println("[DEBUG TABLA] Actualizando estadísticas - Grupo: " + grupoId + ", EquipoTorneo: " + equipoTorneoId);
        
        Optional<TablaPosiciones> tablaOpt = tablaPosicionesRepository.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId);
        
        TablaPosiciones tabla;
        if (tablaOpt.isPresent()) {
            tabla = tablaOpt.get();
            System.out.println("[DEBUG TABLA] Tabla encontrada - PJ actual: " + tabla.getPj());
        } else {
            System.out.println("[DEBUG TABLA] Tabla NO encontrada - Inicializando nueva tabla");
            tabla = inicializarTablaParaEquipo(grupoId, equipoTorneoId);
            System.out.println("[DEBUG TABLA] Nueva tabla creada - ID: " + tabla.getId());
        }
        
        tabla.setPj(tabla.getPj() + 1);
        tabla.setPg(tabla.getPg() + pg);
        tabla.setPe(tabla.getPe() + pe);
        tabla.setPp(tabla.getPp() + pp);
        tabla.setGf(tabla.getGf() + gf);
        tabla.setGc(tabla.getGc() + gc);
        tabla.setDg(tabla.getGf() - tabla.getGc());
        tabla.setPts(tabla.getPts() + (pg * 3) + (pe * 1));
        tabla.setAmarillas(tabla.getAmarillas() + amarillas);
        tabla.setRojas(tabla.getRojas() + rojas);
        
        tablaPosicionesRepository.save(tabla);
        System.out.println("[DEBUG TABLA] Estadísticas actualizadas - PJ: " + tabla.getPj() + ", PG: " + tabla.getPg() + ", PE: " + tabla.getPe() + ", PP: " + tabla.getPp() + ", GF: " + tabla.getGf() + ", GC: " + tabla.getGc() + ", PTS: " + tabla.getPts());
    }
    
    @Override
    @Transactional
    public void actualizarTablaTrasResultado(Long encuentroId) {
        System.out.println("[DEBUG TABLA] ===== actualizarTablaTrasResultado - Encuentro: " + encuentroId + " =====");
        
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado con ID: " + encuentroId));
        
        System.out.println("[DEBUG TABLA] Encuentro - Grupo: " + (encuentro.getGrupo() != null ? encuentro.getGrupo().getId() : "NULL") + ", Resultado: " + encuentro.getGolesLocal() + "-" + encuentro.getGolesVisitante());
        
        if (encuentro.getGrupo() == null) {
            System.out.println("[DEBUG TABLA] SIN GRUPO - Saliendo");
            return; // No hay tabla de posiciones para fases eliminatorias
        }
        
        Long grupoId = encuentro.getGrupo().getId();
        EquipoTorneo equipoLocal = encuentro.getEquipoLocal();
        EquipoTorneo equipoVisitante = encuentro.getEquipoVisitante();
        Integer golesLocal = encuentro.getGolesLocal();
        Integer golesVisitante = encuentro.getGolesVisitante();
        
        System.out.println("[DEBUG TABLA] Procesando - Grupo: " + grupoId + ", Local: " + equipoLocal.getId() + ", Visitante: " + equipoVisitante.getId());
        
        // Determinar resultado para equipo local
        Integer pgLocal = 0, peLocal = 0, ppLocal = 0;
        if (golesLocal > golesVisitante) {
            pgLocal = 1;
        } else if (golesLocal.equals(golesVisitante)) {
            peLocal = 1;
        } else {
            ppLocal = 1;
        }
        
        // Determinar resultado para equipo visitante
        Integer pgVisitante = 0, peVisitante = 0, ppVisitante = 0;
        if (golesVisitante > golesLocal) {
            pgVisitante = 1;
        } else if (golesVisitante.equals(golesLocal)) {
            peVisitante = 1;
        } else {
            ppVisitante = 1;
        }
        
        System.out.println("[DEBUG TABLA] Local - PG: " + pgLocal + ", PE: " + peLocal + ", PP: " + ppLocal + ", GF: " + golesLocal + ", GC: " + golesVisitante);
        System.out.println("[DEBUG TABLA] Visitante - PG: " + pgVisitante + ", PE: " + peVisitante + ", PP: " + ppVisitante + ", GF: " + golesVisitante + ", GC: " + golesLocal);
        
        // Actualizar estadísticas del equipo local
        actualizarEstadisticas(grupoId, equipoLocal.getId(), pgLocal, peLocal, ppLocal, golesLocal, golesVisitante, 0, 0);
        
        // Actualizar estadísticas del equipo visitante
        actualizarEstadisticas(grupoId, equipoVisitante.getId(), pgVisitante, peVisitante, ppVisitante, golesVisitante, golesLocal, 0, 0);
        
        System.out.println("[DEBUG TABLA] ===== PROCESO TABLA COMPLETADO =====");
    }
    
    @Override
    @Transactional
    public TablaPosiciones inicializarTablaParaEquipo(Long grupoId, Long equipoTorneoId) {
        // Verificar si ya existe
        Optional<TablaPosiciones> tablaOpt = tablaPosicionesRepository.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId);
        if (tablaOpt.isPresent()) {
            return tablaOpt.get();
        }
        
        // Obtener el grupo y el equipo
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + grupoId));
        EquipoTorneo equipoTorneo = equipoTorneoRepository.findById(equipoTorneoId)
                .orElseThrow(() -> new RuntimeException("EquipoTorneo no encontrado con ID: " + equipoTorneoId));
        
        // Crear nueva tabla de posiciones
        TablaPosiciones tabla = new TablaPosiciones();
        tabla.setGrupo(grupo);
        tabla.setEquipoTorneo(equipoTorneo);
        tabla.setPj(0);
        tabla.setPg(0);
        tabla.setPe(0);
        tabla.setPp(0);
        tabla.setGf(0);
        tabla.setGc(0);
        tabla.setDg(0);
        tabla.setPts(0);
        tabla.setAmarillas(0);
        tabla.setRojas(0);
        tabla.setEstado(EstadoTablaPosiciones.ABIERTA);
        
        return tablaPosicionesRepository.save(tabla);
    }
}
