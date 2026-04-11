package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Torneo;
import com.GolsystemV2.Backend.enums.EstadoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    
    List<Torneo> findByEstado(EstadoTorneo estado);
    
    Optional<Torneo> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT t FROM Torneo t WHERE t.estado = 'CONFIGURACION'")
    List<Torneo> findTorneosEnConfiguracion();
    
    @Query("SELECT t FROM Torneo t WHERE t.estado = 'EN_CURSO'")
    List<Torneo> findTorneosEnCurso();
}
