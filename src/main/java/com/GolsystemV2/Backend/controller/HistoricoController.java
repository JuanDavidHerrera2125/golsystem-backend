package com.GolsystemV2.Backend.controller;

import com.GolsystemV2.Backend.dto.CampeonHistoricoDTO;
import com.GolsystemV2.Backend.dto.EquipoDTO;
import com.GolsystemV2.Backend.dto.GoleadorHistoricoDTO;
import com.GolsystemV2.Backend.dto.JugadorDTO;
import com.GolsystemV2.Backend.dto.TorneoDTO;
import com.GolsystemV2.Backend.entity.CampeonHistorico;
import com.GolsystemV2.Backend.entity.GoleadorHistorico;
import com.GolsystemV2.Backend.repository.CampeonHistoricoRepository;
import com.GolsystemV2.Backend.repository.GoleadorHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historicos")
public class HistoricoController {

    @Autowired
    private CampeonHistoricoRepository campeonHistoricoRepository;

    @Autowired
    private GoleadorHistoricoRepository goleadorHistoricoRepository;

    @GetMapping("/campeones")
    public ResponseEntity<List<CampeonHistoricoDTO>> getAllCampeones() {
        List<CampeonHistorico> campeones = campeonHistoricoRepository.findAll();
        List<CampeonHistoricoDTO> campeonesDTO = campeones.stream()
                .map(this::convertToCampeonDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(campeonesDTO);
    }

    @GetMapping("/goleadores")
    public ResponseEntity<List<GoleadorHistoricoDTO>> getAllGoleadores() {
        List<GoleadorHistorico> goleadores = goleadorHistoricoRepository.findAll();
        List<GoleadorHistoricoDTO> goleadoresDTO = goleadores.stream()
                .map(this::convertToGoleadorDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(goleadoresDTO);
    }

    private CampeonHistoricoDTO convertToCampeonDTO(CampeonHistorico campeon) {
        CampeonHistoricoDTO dto = new CampeonHistoricoDTO();
        dto.setId(campeon.getId());
        dto.setFechaLogro(campeon.getFechaLogro());
        
        if (campeon.getEquipo() != null) {
            dto.setEquipo(convertToEquipoDTO(campeon.getEquipo()));
        }
        
        if (campeon.getTorneo() != null) {
            dto.setTorneo(convertToTorneoDTO(campeon.getTorneo()));
        }
        
        return dto;
    }

    private GoleadorHistoricoDTO convertToGoleadorDTO(GoleadorHistorico goleador) {
        GoleadorHistoricoDTO dto = new GoleadorHistoricoDTO();
        dto.setId(goleador.getId());
        dto.setTotalGoles(goleador.getTotalGoles());
        
        if (goleador.getJugador() != null) {
            dto.setJugador(convertToJugadorDTO(goleador.getJugador()));
        }
        
        if (goleador.getEquipo() != null) {
            dto.setEquipo(convertToEquipoDTO(goleador.getEquipo()));
        }
        
        if (goleador.getTorneo() != null) {
            dto.setTorneo(convertToTorneoDTO(goleador.getTorneo()));
        }
        
        return dto;
    }

    private EquipoDTO convertToEquipoDTO(com.GolsystemV2.Backend.entity.Equipo equipo) {
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setCodigoEquipo(equipo.getCodigoEquipo());
        dto.setNombre(equipo.getNombre());
        dto.setLogoUrl(equipo.getLogoUrl());
        dto.setActivo(equipo.getActivo());
        return dto;
    }

    private JugadorDTO convertToJugadorDTO(com.GolsystemV2.Backend.entity.Jugador jugador) {
        JugadorDTO dto = new JugadorDTO();
        dto.setId(jugador.getId());
        dto.setDocumentoIdentidad(jugador.getDocumentoIdentidad());
        dto.setNombre(jugador.getNombre());
        dto.setApellido(jugador.getApellido());
        dto.setFechaNacimiento(jugador.getFechaNacimiento());
        dto.setFotoUrl(jugador.getFotoUrl());
        dto.setActivo(jugador.getActivo());
        return dto;
    }

    private TorneoDTO convertToTorneoDTO(com.GolsystemV2.Backend.entity.Torneo torneo) {
        TorneoDTO dto = new TorneoDTO();
        dto.setId(torneo.getId());
        dto.setNombre(torneo.getNombre());
        dto.setLogoUrl(torneo.getLogoUrl());
        dto.setCategoria(torneo.getCategoria());
        dto.setMinJugadores(torneo.getMinJugadores());
        dto.setMaxJugadores(torneo.getMaxJugadores());
        dto.setEstado(torneo.getEstado());
        dto.setFechaCreacion(torneo.getFechaCreacion());
        return dto;
    }
}
