package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.CategoriaTorneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "torneo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Torneo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaTorneo categoria;
    
    @Column(name = "min_jugadores", nullable = false)
    private Integer minJugadores;
    
    @Column(name = "max_jugadores", nullable = false)
    private Integer maxJugadores;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTorneo estado = EstadoTorneo.CONFIGURACION;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
