package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Fase;
import com.GolsystemV2.Backend.enums.EstadoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {

    List<Fase> findByTorneoIdOrderByNumeroFase(Long torneoId);

    List<Fase> findByTorneoIdOrderByNumeroFaseDesc(Long torneoId);

    Optional<Fase> findByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);

    /** Buscar fases por estado dentro de un torneo (reemplaza findByEstaCerradaFalse). */
    List<Fase> findByTorneoIdAndEstadoFase(Long torneoId, EstadoFase estadoFase);

    /** Obtener la fase activa (EN_CURSO) de un torneo — debe haber solo una. */
    Optional<Fase> findByTorneoIdAndEstadoFaseEquals(Long torneoId, EstadoFase estadoFase);

    @Query("SELECT MAX(f.numeroFase) FROM Fase f WHERE f.torneo.id = :torneoId")
    Integer findMaxNumeroFaseByTorneoId(Long torneoId);

    boolean existsByTorneoIdAndNumeroFase(Long torneoId, Integer numeroFase);

    /** Verifica si existe una fase anterior en estado FINALIZADA (para validar progresión lineal). */
    @Query("SELECT COUNT(f) > 0 FROM Fase f WHERE f.torneo.id = :torneoId AND f.numeroFase = :numeroFaseAnterior AND f.estadoFase = 'FINALIZADA'")
    boolean existsFaseAnteriorFinalizada(Long torneoId, Integer numeroFaseAnterior);

    /** Cuenta cuántas fases de un torneo NO están FINALIZADAS (para saber si hay fases activas). */
    @Query("SELECT COUNT(f) FROM Fase f WHERE f.torneo.id = :torneoId AND f.estadoFase != 'FINALIZADA'")
    Long countFasesNoFinalizadas(Long torneoId);

    /** Carga una fase con sus equipos usando JOIN FETCH (evita LazyInitializationException). */
    @Query("SELECT f FROM Fase f LEFT JOIN FETCH f.equipos WHERE f.id = :faseId")
    Optional<Fase> findByIdWithEquipos(Long faseId);

    /** Carga todas las fases de un torneo con sus equipos. */
    @Query("SELECT f FROM Fase f LEFT JOIN FETCH f.equipos WHERE f.torneo.id = :torneoId ORDER BY f.numeroFase")
    List<Fase> findByTorneoIdWithEquipos(Long torneoId);
}
