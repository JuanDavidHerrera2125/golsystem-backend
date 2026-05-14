package com.GolsystemV2.Backend.service.impl;

import com.GolsystemV2.Backend.entity.CronometroPartido;
import com.GolsystemV2.Backend.entity.Encuentro;
import com.GolsystemV2.Backend.enums.EstadoCronometro;
import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import com.GolsystemV2.Backend.repository.CronometroPartidoRepository;
import com.GolsystemV2.Backend.repository.EncuentroRepository;
import com.GolsystemV2.Backend.service.CronometroPartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CronometroPartidoServiceImpl implements CronometroPartidoService {

    @Autowired
    private CronometroPartidoRepository cronometroRepository;
    
    @Autowired
    private EncuentroRepository encuentroRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CronometroPartido> findByEncuentroId(Long encuentroId) {
        return cronometroRepository.findByEncuentroId(encuentroId);
    }

    @Override
    public CronometroPartido iniciarPartido(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() != EstadoCronometro.SIN_INICIAR && 
            cronometro.getEstado() != EstadoCronometro.PAUSADO) {
            throw new RuntimeException("El partido ya fue iniciado o finalizado");
        }
        
        cronometro.setEstado(EstadoCronometro.EN_JUEGO);
        cronometro.setInicioTimestamp(LocalDateTime.now());
        
        // Actualizar estado del encuentro
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
            .orElseThrow(() -> new RuntimeException("Encuentro no encontrado"));
        encuentro.setEstado(EstadoEncuentro.EN_JUEGO);
        encuentroRepository.save(encuentro);
        
        return cronometroRepository.save(cronometro);
    }

    @Override
    public CronometroPartido pausarPartido(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() != EstadoCronometro.EN_JUEGO && 
            cronometro.getEstado() != EstadoCronometro.TIEMPO_EXTRA) {
            throw new RuntimeException("El partido no está en juego");
        }
        
        // Calcular tiempo transcurrido desde el inicio o última reanudación
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime referencia = cronometro.getPausaTimestamp() != null ? 
            cronometro.getPausaTimestamp() : cronometro.getInicioTimestamp();
        
        if (referencia != null) {
            int segundosTranscurridos = (int) Duration.between(referencia, ahora).getSeconds();
            cronometro.setTiempoSegundos(cronometro.getTiempoSegundos() + segundosTranscurridos);
        }
        
        cronometro.setEstado(EstadoCronometro.PAUSADO);
        cronometro.setPausaTimestamp(ahora);
        
        return cronometroRepository.save(cronometro);
    }

    @Override
    public CronometroPartido reanudarPartido(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() != EstadoCronometro.PAUSADO && 
            cronometro.getEstado() != EstadoCronometro.ENTRETIEMPO) {
            throw new RuntimeException("El partido no está pausado");
        }
        
        // Calcular tiempo de pausa
        if (cronometro.getPausaTimestamp() != null) {
            int segundosPausa = (int) Duration.between(cronometro.getPausaTimestamp(), LocalDateTime.now()).getSeconds();
            cronometro.setTiempoPausaTotal(cronometro.getTiempoPausaTotal() + segundosPausa);
        }
        
        cronometro.setEstado(EstadoCronometro.EN_JUEGO);
        cronometro.setPausaTimestamp(null);
        
        return cronometroRepository.save(cronometro);
    }

    @Override
    public CronometroPartido entretiempo(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() != EstadoCronometro.EN_JUEGO) {
            throw new RuntimeException("El partido no está en juego");
        }
        
        // Guardar tiempo transcurrido del primer tiempo
        LocalDateTime ahora = LocalDateTime.now();
        if (cronometro.getInicioTimestamp() != null) {
            int segundosTranscurridos = (int) Duration.between(cronometro.getInicioTimestamp(), ahora).getSeconds();
            cronometro.setTiempoSegundos(segundosTranscurridos);
        }
        
        cronometro.setEstado(EstadoCronometro.ENTRETIEMPO);
        cronometro.setPausaTimestamp(ahora);
        
        return cronometroRepository.save(cronometro);
    }

    @Override
    public CronometroPartido finalizarPartido(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() == EstadoCronometro.FINALIZADO) {
            throw new RuntimeException("El partido ya está finalizado");
        }
        
        // Calcular tiempo final
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime referencia = cronometro.getPausaTimestamp() != null ? 
            cronometro.getPausaTimestamp() : cronometro.getInicioTimestamp();
        
        if (referencia != null && cronometro.getEstado() == EstadoCronometro.EN_JUEGO) {
            int segundosTranscurridos = (int) Duration.between(referencia, ahora).getSeconds();
            cronometro.setTiempoSegundos(cronometro.getTiempoSegundos() + segundosTranscurridos);
        }
        
        cronometro.setEstado(EstadoCronometro.FINALIZADO);
        
        // Actualizar estado del encuentro
        Encuentro encuentro = encuentroRepository.findById(encuentroId)
            .orElseThrow(() -> new RuntimeException("Encuentro no encontrado"));
        encuentro.setEstado(EstadoEncuentro.FINALIZADO);
        encuentroRepository.save(encuentro);
        
        return cronometroRepository.save(cronometro);
    }

    @Override
    public CronometroPartido obtenerOCrearCronometro(Long encuentroId) {
        return cronometroRepository.findByEncuentroId(encuentroId)
            .orElseGet(() -> {
                Encuentro encuentro = encuentroRepository.findById(encuentroId)
                    .orElseThrow(() -> new RuntimeException("Encuentro no encontrado"));
                
                CronometroPartido nuevo = new CronometroPartido();
                nuevo.setEncuentro(encuentro);
                nuevo.setEstado(EstadoCronometro.SIN_INICIAR);
                nuevo.setTiempoSegundos(0);
                nuevo.setTiempoExtraSegundos(0);
                nuevo.setTiempoPausaTotal(0);
                
                return cronometroRepository.save(nuevo);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCronometro getEstadoActual(Long encuentroId) {
        return cronometroRepository.findByEncuentroId(encuentroId)
            .map(CronometroPartido::getEstado)
            .orElse(EstadoCronometro.SIN_INICIAR);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTiempoTranscurrido(Long encuentroId) {
        CronometroPartido cronometro = obtenerOCrearCronometro(encuentroId);
        
        if (cronometro.getEstado() == EstadoCronometro.EN_JUEGO && cronometro.getInicioTimestamp() != null) {
            LocalDateTime referencia = cronometro.getPausaTimestamp() != null ? 
                cronometro.getPausaTimestamp() : cronometro.getInicioTimestamp();
            int segundosActuales = (int) Duration.between(referencia, LocalDateTime.now()).getSeconds();
            return cronometro.getTiempoSegundos() + segundosActuales;
        }
        
        return cronometro.getTiempoSegundos();
    }
}
