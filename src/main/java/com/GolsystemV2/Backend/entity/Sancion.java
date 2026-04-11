package com.GolsystemV2.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sancion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sancion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_equipo_torneo_id", nullable = false)
    private JugadorEquipoTorneo jugadorEquipoTorneo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encuentro_origen_id")
    private Encuentro encuentroOrigen;
    
    @Column(name = "cantidad_fechas", nullable = false)
    private Integer cantidadFechas;
    
    @Column(name = "fechas_cumplidas", nullable = false)
    private Integer fechasCumplidas = 0;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
