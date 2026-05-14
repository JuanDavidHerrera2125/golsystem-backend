package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.CronometroPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CronometroPartidoRepository extends JpaRepository<CronometroPartido, Long> {
    
    Optional<CronometroPartido> findByEncuentroId(Long encuentroId);
    
    boolean existsByEncuentroId(Long encuentroId);
}
