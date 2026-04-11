package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.EstadoTablaPosiciones;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tabla_posiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TablaPosiciones {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_torneo_id", nullable = false)
    private EquipoTorneo equipoTorneo;
    
    @Column(nullable = false)
    private Integer pj = 0;
    
    @Column(nullable = false)
    private Integer pg = 0;
    
    @Column(nullable = false)
    private Integer pe = 0;
    
    @Column(nullable = false)
    private Integer pp = 0;
    
    @Column(nullable = false)
    private Integer gf = 0;
    
    @Column(nullable = false)
    private Integer gc = 0;
    
    @Column(nullable = false)
    private Integer dg = 0;
    
    @Column(nullable = false)
    private Integer pts = 0;
    
    @Column(nullable = false)
    private Integer amarillas = 0;
    
    @Column(nullable = false)
    private Integer rojas = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTablaPosiciones estado = EstadoTablaPosiciones.ABIERTA;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
