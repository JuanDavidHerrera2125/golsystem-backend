package com.GolsystemV2.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "equipo_torneo", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"torneo_id", "equipo_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoTorneo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;
    
    @Column(nullable = false)
    private Boolean eliminado = false;
    
    @Column(name = "fecha_inscripcion")
    private LocalDate fechaInscripcion;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDate.now();
        }
    }
}
