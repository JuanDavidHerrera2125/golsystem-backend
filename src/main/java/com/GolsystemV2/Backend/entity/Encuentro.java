package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.EstadoEncuentro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "encuentro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encuentro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fase_id", nullable = false)
    private Fase fase;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_local_id", nullable = false)
    private EquipoTorneo equipoLocal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    private EquipoTorneo equipoVisitante;
    
    @Column
    private java.time.LocalDateTime fecha;
    
    @Column(name = "goles_local")
    private Integer golesLocal;
    
    @Column(name = "goles_visitante")
    private Integer golesVisitante;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEncuentro estado = EstadoEncuentro.PROGRAMADO;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
