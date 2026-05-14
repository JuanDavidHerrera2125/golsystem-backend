package com.GolsystemV2.Backend.entity;

import com.GolsystemV2.Backend.enums.EstadoFase;
import com.GolsystemV2.Backend.enums.TipoFase;
import com.GolsystemV2.Backend.enums.FormatoEncuentro;
import com.GolsystemV2.Backend.enums.MetodoDesempatePlayoff;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Fase — contenedor maestro que agrupa de 1 a 15 Grupos.
 *
 * Ciclo de vida: CONFIGURACION → EN_CURSO → FINALIZADA
 *
 * Reglas de bloqueo:
 *  - En CONFIGURACION: se puede editar todo.
 *  - En EN_CURSO: los grupos están llenos y hay partidos. Bloquea cambios estructurales.
 *  - En FINALIZADA: fase cerrada, históricos disparados, equipos promovidos.
 */
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

    /** Número secuencial de la fase dentro del torneo. La progresión es lineal (1, 2, 3…). */
    @Column(name = "numero_fase", nullable = false)
    private Integer numeroFase;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fase", nullable = false)
    private TipoFase tipoFase;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_encuentro")
    private FormatoEncuentro formatoEncuentro;

    /**
     * Estado del ciclo de vida de la fase.
     * Reemplaza el antiguo campo booleano estaCerrada.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fase", nullable = false)
    private EstadoFase estadoFase = EstadoFase.CONFIGURACION;

    /** Número de grupos en esta fase. Debe estar entre 1 y 15. */
    @Column(name = "cantidad_grupos")
    private Integer cantidadGrupos;

    /** Cuántos equipos por grupo clasifican a la siguiente fase. */
    @Column(name = "equipos_clasificados_por_grupo")
    private Integer equiposClasificadosPorGrupo;

    /** Total de clasificados que pasan a la ronda siguiente (calculado o manual). */
    @Column(name = "clasificados_siguiente_ronda")
    private Integer clasificadosSiguienteRonda;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_desempate_playoff")
    private MetodoDesempatePlayoff metodoDesempatePlayoff;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (estadoFase == null) {
            estadoFase = EstadoFase.CONFIGURACION;
        }
    }

    // ─── Métodos de conveniencia ──────────────────────────────────────────────

    /** Retorna true si la fase NO permite cambios estructurales. */
    public boolean estaBloqueadaParaEdicion() {
        return EstadoFase.EN_CURSO.equals(estadoFase) || EstadoFase.FINALIZADA.equals(estadoFase);
    }

    /** Retorna true si la fase ya terminó (FINALIZADA). */
    public boolean estaFinalizada() {
        return EstadoFase.FINALIZADA.equals(estadoFase);
    }

    /** Retorna true si la fase está activa (EN_CURSO). */
    public boolean estaEnCurso() {
        return EstadoFase.EN_CURSO.equals(estadoFase);
    }

    /** Retorna true si la fase está en configuración (editable). */
    public boolean estaEnConfiguracion() {
        return EstadoFase.CONFIGURACION.equals(estadoFase);
    }

    // ─── Relación ManyToMany con Equipos ─────────────────────────────────────

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fase_equipos",
        joinColumns = @JoinColumn(name = "fase_id"),
        inverseJoinColumns = @JoinColumn(name = "equipo_id")
    )
    private List<Equipo> equipos = new ArrayList<>();
}
