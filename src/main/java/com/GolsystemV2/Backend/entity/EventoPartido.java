package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "evento_partido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoPartido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encuentro_id", nullable = false)
    private Encuentro encuentro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_equipo_torneo_id", nullable = false)
    private JugadorEquipoTorneo jugadorEquipoTorneo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_torneo_id", nullable = false)
    private EquipoTorneo equipoTorneo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;
    
    @Column(nullable = false)
    private Integer minuto;
    
    @Column(name = "tipo_gol")
    private String tipoGol; // NORMAL, PENAL, TIRO_LIBRE, CABEZA, AUTOGOL
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asistencia_id")
    private JugadorEquipoTorneo asistencia; // Jugador que asistió el gol
    
    @Column(name = "motivo_tarjeta")
    private String motivoTarjeta; // Falta, protesta, etc.
    
    @Column(name = "descripcion")
    private String descripcion; // Descripción del evento
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
