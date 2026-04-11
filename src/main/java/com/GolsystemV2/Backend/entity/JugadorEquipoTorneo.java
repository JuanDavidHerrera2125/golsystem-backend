package com.GolsystemV2.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "jugador_equipo_torneo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"jugador_id", "equipo_torneo_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorEquipoTorneo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_torneo_id", nullable = false)
    private EquipoTorneo equipoTorneo;
    
    @Transient
    private Long torneoId;
    
    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_registro")
    private java.time.LocalDate fechaRegistro;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (fechaRegistro == null) {
            fechaRegistro = java.time.LocalDate.now();
        }
        if (equipoTorneo != null && equipoTorneo.getTorneo() != null) {
            torneoId = equipoTorneo.getTorneo().getId();
        }
    }
}
