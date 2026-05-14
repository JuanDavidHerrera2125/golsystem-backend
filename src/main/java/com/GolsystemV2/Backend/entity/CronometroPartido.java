package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.EstadoCronometro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para persistir el estado del cronometro de un partido
 * Permite pausar, reanudar y mantener el tiempo exacto
 */
@Entity
@Table(name = "cronometro_partido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronometroPartido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encuentro_id", nullable = false, unique = true)
    private Encuentro encuentro;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCronometro estado = EstadoCronometro.SIN_INICIAR;
    
    @Column(name = "tiempo_segundos", nullable = false)
    private Integer tiempoSegundos = 0;
    
    @Column(name = "tiempo_extra_segundos")
    private Integer tiempoExtraSegundos = 0;
    
    @Column(name = "tiempo_pausa_total")
    private Integer tiempoPausaTotal = 0;
    
    @Column(name = "inicio_timestamp")
    private LocalDateTime inicioTimestamp;
    
    @Column(name = "pausa_timestamp")
    private LocalDateTime pausaTimestamp;
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        ultimaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }
}
