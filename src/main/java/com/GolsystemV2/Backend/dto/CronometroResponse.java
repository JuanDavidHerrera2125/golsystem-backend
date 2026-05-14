package com.GolsystemV2.Backend.dto;

import com.GolsystemV2.Backend.enums.EstadoCronometro;
import lombok.Data;

/**
 * DTO para respuesta del estado del cronometro
 */
@Data
public class CronometroResponse {
    
    private EstadoCronometro estado;
    private Integer tiempoSegundos;
    private Integer tiempoExtraSegundos;
    private Integer tiempoPausaTotal;
    private Integer minutos;
    private Integer segundos;
    private Boolean puedeIniciar;
    private Boolean puedePausar;
    private Boolean puedeReanudar;
    private Boolean puedeFinalizar;
}
