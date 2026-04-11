package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.TablaPosiciones;
import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
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
        Optional<TablaPosiciones> tablaOpt = tablaPosicionesRepository.findByGrupoIdAndEquipoTorneoId(grupoId, equipoTorneoId);
        
        if (tablaOpt.isPresent()) {
            TablaPosiciones tabla = tablaOpt.get();
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
        }
    }
}
