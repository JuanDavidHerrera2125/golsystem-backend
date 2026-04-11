package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    
    Optional<Equipo> findByCodigoEquipo(String codigoEquipo);
    
    boolean existsByCodigoEquipo(String codigoEquipo);
    
    boolean existsByIdAndActivoTrue(Long id);
}
