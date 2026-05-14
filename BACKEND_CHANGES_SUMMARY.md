# Resumen de Cambios en el Backend - Integración Fases y Equipos

## Fecha: 15 de Abril 2026
## Objetivo: Permitir asignar equipos a fases desde el frontend

---

## Archivos Modificados

### 1. Entidad Fase (`entity/Fase.java`)
**Cambios:**
- Agregados imports: `java.util.ArrayList`, `java.util.List`
- Agregada relación ManyToMany con Equipo:
```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "fase_equipos",
    joinColumns = @JoinColumn(name = "fase_id"),
    inverseJoinColumns = @JoinColumn(name = "equipo_id")
)
private List<Equipo> equipos = new ArrayList<>();
```

**Impacto:** Permite asociar múltiples equipos a una fase mediante tabla intermedia `fase_equipos`.

---

### 2. DTO FaseConfigDto (`dto/FaseConfigDto.java`)
**Cambios:**
- Agregado import: `java.util.List`
- Agregado campo y getters/setters:
```java
private List<EquipoRefDto> equipos;
public List<EquipoRefDto> getEquipos() { return equipos; }
public void setEquipos(List<EquipoRefDto> equipos) { this.equipos = equipos; }
```

**Impacto:** Permite recibir lista de equipos seleccionados al configurar una fase.

---

### 3. DTO FaseEstadoDto (`dto/FaseEstadoDto.java`)
**Cambios:**
- Agregado import: `java.util.List`
- Agregado campo y getters/setters:
```java
private List<EquipoInfoDto> equipos;
public List<EquipoInfoDto> getEquipos() { return equipos; }
public void setEquipos(List<EquipoInfoDto> equipos) { this.equipos = equipos; }
```

**Impacto:** Permite retornar lista de equipos asignados al consultar el estado de una fase.

---

### 4. PhaseManagerServiceImpl (`service/impl/PhaseManagerServiceImpl.java`)
**Cambios:**
1. Agregados imports:
   - `EquipoInfoDto`
   - `Equipo`
   - `EquipoRepository`

2. Agregada inyección:
```java
@Autowired
private EquipoRepository equipoRepository;
```

3. Modificado método `configurarFase()`:
   - Agregada asignación de equipos después de crear la fase:
```java
// 8. Asignar equipos seleccionados a la fase (si se proporcionaron)
if (dto.getEquipos() != null && !dto.getEquipos().isEmpty()) {
    asignarEquiposAFase(guardada, dto.getEquipos());
}
```

4. Modificado método `construirFaseEstadoDto()`:
   - Agregado mapeo de equipos al DTO:
```java
// Equipos asignados a la fase
if (fase.getEquipos() != null && !fase.getEquipos().isEmpty()) {
    List<EquipoInfoDto> equiposDto = fase.getEquipos().stream()
        .map(e -> new EquipoInfoDto(e.getId(), e.getNombre(), e.getLogoUrl(), e.getCodigoEquipo()))
        .toList();
    dto.setEquipos(equiposDto);
}
```

5. Agregado nuevo método `asignarEquiposAFase()`:
```java
private void asignarEquiposAFase(Fase fase, List<EquipoRefDto> equiposRef) {
    // Valida que equipos existan y pertenezcan al torneo
    // Asigna equipos a la fase y guarda
}
```

**Impacto:** Procesa y valida los equipos enviados desde el frontend, los asocia a la fase y los incluye en las respuestas.

---

### 5. FaseRepository (`repository/FaseRepository.java`)
**Cambios:**
- Agregados métodos con JOIN FETCH:
```java
/** Carga una fase con sus equipos usando JOIN FETCH */
@Query("SELECT f FROM Fase f LEFT JOIN FETCH f.equipos WHERE f.id = :faseId")
Optional<Fase> findByIdWithEquipos(Long faseId);

/** Carga todas las fases de un torneo con sus equipos */
@Query("SELECT f FROM Fase f LEFT JOIN FETCH f.equipos WHERE f.torneo.id = :torneoId ORDER BY f.numeroFase")
List<Fase> findByTorneoIdWithEquipos(Long torneoId);
```

**Impacto:** Permite cargar fases junto con sus equipos en una sola consulta (evita LazyInitializationException).

---

### 6. EquipoTorneoRepository (`repository/EquipoTorneoRepository.java`)
**Cambios:**
- Agregado método:
```java
boolean existsByTorneoIdAndEquipoIdAndEliminadoFalse(Long torneoId, Long equipoId);
```

**Impacto:** Valida que un equipo esté inscrito y activo en un torneo antes de asignarlo a una fase.

---

### 7. TorneoServiceImpl (`service/impl/TorneoServiceImpl.java`)
**Cambios:**
- Modificado método `obtenerFases()`:
  - Usa `findByTorneoIdWithEquipos()` en lugar de `findByTorneoIdOrderByNumeroFase()`
  - Agregados campos adicionales: `cantidadGrupos`, `equiposClasificadosPorGrupo`
  - Agregada lista de equipos con: `id`, `nombre`, `codigoEquipo`, `logoUrl`
  - Agregado contador: `totalEquipos`

**Impacto:** El endpoint `GET /api/torneos/{id}/fases` ahora retorna fases con sus equipos incluidos.

---

## Archivos Nuevos

### 1. EquipoRefDto (`dto/EquipoRefDto.java`)
**Propósito:** DTO ligero para recibir referencias de equipo (solo ID) desde el frontend.
```java
public class EquipoRefDto {
    private Long id;
    // constructor + getter/setter
}
```

### 2. EquipoInfoDto (`dto/EquipoInfoDto.java`)
**Propósito:** DTO para retornar información resumida de equipos en respuestas.
```java
public class EquipoInfoDto {
    private Long id;
    private String nombre;
    private String logoUrl;
    private String codigoEquipo;
    // constructor + getters/setters
}
```

---

## Flujo de Datos Corregido

```
FRONTEND                                         BACKEND
─────────────────────────────────────────────────────────────────

PhaseConfig.jsx
├─ Selecciona equipos de grupos
├─ Crea payload:
│  {
│    torneoId: 1,
│    numeroFase: 1,
│    tipoFase: "GRUPOS",
│    cantidadGrupos: 2,
│    equipos: [{id: 1}, {id: 2}, {id: 3}, {id: 4}]
│  }
│
└─ POST /api/fases/configurar ───────────────────► FaseController
                                                   └─► PhaseManagerServiceImpl
                                                       ├─ Crea fase
                                                       ├─ Valida equipos
                                                       ├─ Asigna equipos a fase
                                                       └─ Retorna FaseEstadoDto
                                                          con equipos incluidos

TorneoDetail.jsx
├─ GET /api/torneos/{id}/fases ──────────────────► TorneoController
                                                   └─► TorneoServiceImpl
                                                       ├─ findByTorneoIdWithEquipos()
                                                       └─ Mapea fases + equipos
                                                   ◄─ Retorna:
                                                      [{
                                                        id: 1,
                                                        numeroFase: 1,
                                                        estadoFase: "CONFIGURACION",
                                                        equipos: [
                                                          {id: 1, nombre: "Eq A", ...},
                                                          {id: 2, nombre: "Eq B", ...}
                                                        ],
                                                        totalEquipos: 4
                                                      }]
```

---

## Endpoints Actualizados

### POST /api/fases/configurar
**Request Body:**
```json
{
  "torneoId": 1,
  "numeroFase": 1,
  "tipoFase": "GRUPOS",
  "cantidadGrupos": 2,
  "equiposClasificadosPorGrupo": 2,
  "formatoEncuentro": "SOLO_IDA",
  "metodoDesempatePlayoff": null,
  "equipos": [
    {"id": 1},
    {"id": 2},
    {"id": 3},
    {"id": 4}
  ]
}
```

**Response:** `FaseEstadoDto` con lista de equipos incluida.

---

### GET /api/torneos/{id}/fases
**Response:**
```json
[
  {
    "id": 1,
    "numeroFase": 1,
    "tipoFase": "GRUPOS",
    "estadoFase": "CONFIGURACION",
    "formatoEncuentro": "SOLO_IDA",
    "bloqueada": false,
    "cantidadGrupos": 2,
    "equiposClasificadosPorGrupo": 2,
    "equipos": [
      {"id": 1, "nombre": "Equipo A", "codigoEquipo": "EQA001", "logoUrl": "..."},
      {"id": 2, "nombre": "Equipo B", "codigoEquipo": "EQB002", "logoUrl": "..."},
      {"id": 3, "nombre": "Equipo C", "codigoEquipo": "EQC003", "logoUrl": "..."},
      {"id": 4, "nombre": "Equipo D", "codigoEquipo": "EQD004", "logoUrl": "..."}
    ],
    "totalEquipos": 4
  }
]
```

---

## Validaciones Implementadas

1. **Equipo existe:** Valida que cada equipo ID corresponda a un equipo existente.
2. **Equipo inscrito:** Valida que el equipo esté inscrito en el torneo (`eliminado = false`).
3. **Duplicados:** No permite duplicar equipos en la misma fase (ManyToMany maneja esto automáticamente).

---

## Tabla de Base de Datos Nueva

```sql
CREATE TABLE fase_equipos (
    fase_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    PRIMARY KEY (fase_id, equipo_id),
    FOREIGN KEY (fase_id) REFERENCES fase(id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id)
);
```

Hibernate creará esta tabla automáticamente al iniciar la aplicación (si `ddl-auto` está configurado).

---

## Próximos Pasos

1. **Reiniciar backend** para que Hibernate cree la tabla `fase_equipos`
2. **Probar integración** creando una fase con equipos desde el frontend
3. **Verificar** que los equipos se muestren correctamente en PhaseCard

---

## Estado Final

✅ Backend completamente actualizado para soportar equipos en fases
✅ Todos los DTOs creados/modificados
✅ Validaciones implementadas
✅ Repositorios actualizados con JOIN FETCH
✅ Servicios modificados para procesar equipos
✅ Endpoint /torneos/{id}/fases retorna fases con equipos

**Listo para integración con frontend!**
