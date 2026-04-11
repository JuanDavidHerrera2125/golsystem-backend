package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.TipoFase;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.enums.MetodoDesempatePlayoff;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "fase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;
    
    @Column(name = "numero_fase", nullable = false)
    private Integer numeroFase;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fase", nullable = false)
    private TipoFase tipoFase;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "formato_encuentro", nullable = false)
    private FormatoEncuentro formatoEncuentro;
    
    @Column(name = "clasificados_siguiente_ronda")
    private Integer clasificadosSiguienteRonda;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_desempate_playoff")
    private MetodoDesempatePlayoff metodoDesempatePlayoff;
    
    @Column(name = "esta_cerrada", nullable = false)
    private Boolean estaCerrada = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
