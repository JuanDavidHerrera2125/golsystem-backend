package com.GolsystemV2.Backend.repository;

import com.GolsystemV2.Backend.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    
    Optional<Jugador> findByDocumentoIdentidad(String documentoIdentidad);
    
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    
    boolean existsByIdAndActivoTrue(Long id);
}
