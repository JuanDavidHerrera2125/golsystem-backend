package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.EquipoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoTorneoRepository extends JpaRepository<EquipoTorneo, Long> {

    Optional<EquipoTorneo> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);

    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);

    boolean existsByTorneoIdAndEquipoIdAndEliminadoFalse(Long torneoId, Long equipoId);

    List<EquipoTorneo> findByTorneoIdAndEliminadoFalse(Long torneoId);

    List<EquipoTorneo> findByTorneoId(Long torneoId);

    List<EquipoTorneo> findByGrupoIdAndEliminadoFalse(Long grupoId);

    List<EquipoTorneo> findByGrupoFaseId(Long faseId);

    @Query("SELECT COUNT(et) FROM EquipoTorneo et WHERE et.torneo.id = :torneoId AND et.eliminado = false")
    Integer countEquiposActivosPorTorneo(Long torneoId);

    @Query("SELECT et FROM EquipoTorneo et JOIN JugadorEquipoTorneo jet ON et.id = jet.equipoTorneo.id WHERE jet.jugador.id = :jugadorId AND et.torneo.id = :torneoId AND jet.activo = true")
    Optional<EquipoTorneo> findByJugadorAndTorneo(Long jugadorId, Long torneoId);

    /**
     * Obtiene los equipos que pertenecen a grupos de una fase específica.
     * Usado por PhaseManagerService para saber cuántos equipos hay en una fase.
     */
    @Query("SELECT et FROM EquipoTorneo et WHERE et.grupo.fase.id = :faseId AND et.eliminado = false")
    List<EquipoTorneo> findEquiposPorFaseId(Long faseId);

    /**
     * Cuenta cuántos equipos distintos están asignados a grupos de una fase.
     */
    @Query("SELECT COUNT(et) FROM EquipoTorneo et WHERE et.grupo.fase.id = :faseId AND et.eliminado = false")
    Integer countEquiposPorFaseId(Long faseId);

    /**
     * Detecta equipos duplicados en grupos de la misma fase (integridad referencial).
     * Un equipo no puede estar en 2 grupos distintos de la misma fase.
     */
    @Query("SELECT et FROM EquipoTorneo et WHERE et.grupo.fase.id = :faseId GROUP BY et.equipo.id HAVING COUNT(et.id) > 1")
    List<EquipoTorneo> findEquiposDuplicadosEnFase(Long faseId);

    /**
     * Obtiene los EquipoTorneo cuyos equipos están asignados a una fase específica
     * a través de la relación ManyToMany Fase.equipos.
     */
    @Query("SELECT et FROM EquipoTorneo et WHERE et.equipo.id IN (SELECT e.id FROM Fase f JOIN f.equipos e WHERE f.id = :faseId) AND et.torneo.id = (SELECT f2.torneo.id FROM Fase f2 WHERE f2.id = :faseId)")
    List<EquipoTorneo> findByEquipoFasesId(Long faseId);
}
