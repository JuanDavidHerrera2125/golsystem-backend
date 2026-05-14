# 🏆 GolSystem V2 - Backend Logic Architecture

> **Documento Técnico Interno para Backend Development Team**  
> **Última Actualización:** 2026-04-13  
> **Versión:** 1.0

---

## 📋 Índice

1. [Arquitectura General](#1-arquitectura-general)
2. [Entidades Core del Sistema](#2-entidades-core-del-sistema)
3. [Modelo de Datos - Torneo como Mundo Independiente](#3-modelo-de-datos---torneo-como-mundo-independiente)
4. [Registro Global de Identidades Únicas](#4-registro-global-de-identidades-únicas)
5. [Lo Que Ya Está Implementado ✅](#5-lo-que-ya-está-implementado-)
6. [Funcionalidades Pendientes 🔧](#6-funcionalidades-pendientes-)
7. [Reglas de Negocio Críticas](#7-reglas-de-negocio-críticas)
8. [Endpoints y Servicios Existentes](#8-endpoints-y-servicios-existentes)
9. [Roadmap de Implementación](#9-roadmap-de-implementación)
10. [Decisiones Técnicas y Arquitectura](#10-decisiones-técnicas-y-arquitectura)

---

## 1. Arquitectura General

### Concepto Central: "Mundos Independientes vs. El Corazón de la App"

El sistema está diseñado bajo una filosofía de arquitectura híbrida, diviendo responsabilidades entre el **Registro General (Corazón)** y los **Mundos Independientes (Torneos)**.

1. **El Corazón de la App (Registro General):**
   - Aquí viven de manera PERMANENTE los **Jugadores** y los **Equipos**.
   - Su objetivo principal es **evitar redundancia** en la base de datos a largo plazo.
   - Las validaciones y unificación en toda la historia de la aplicación, campeonatos y estrellas se relacionan usando los **Identificadores Únicos Absolutos**:
     - Para un Jugador: El **Número de Identidad** (`documentoIdentidad`).
     - Para un Equipo: El **Código Único de Equipo** (`codigoEquipo`, el cual se genera automáticamente).

2. **Mundos Independientes (Torneos):**
   - Cada Torneo es un mundo totalmente diferente.
   - Tiene normativas, métricas, dashboards y reglas aisladas del resto de competiciones.
   - **Regla Estricta Torneo-Equipo:** Un equipo se puede inscribir a varios torneos a lo largo del tiempo o de manera simultánea, pero **nunca dos veces al mismo torneo**.
   - **Regla Estricta Torneo-Jugador:** Un jugador se puede inscribir a múltiples equipos durante su carrera, pero **nunca puede estar en dos equipos dentro del mismo torneo**. Si se inscribe y ya existe globalmente, el sistema lo identifica mediante su `# de documento`, carga sus datos históricos y valida su inscripción local en ese mundo.

```
┌─────────────────────────────────────────────────────────────────┐
│              CORAZÓN DE LA APP - REGISTRO GLOBAL                 │
│  (Identidades Únicas Absolutas: # Documento y Código Equipo)    │
└─────────────────────────────────────────────────────────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
    ┌────▼────┐           ┌────▼────┐           ┌────▼────┐
    │Torneo 1 │           │Torneo 2 │           │Torneo N │
    │ (Mundo) │           │ (Mundo) │           │ (Mundo) │
    └────┬────┘           └────┬────┘           └────┬────┘
         │                     │                     │
    ┌────▼────┐           ┌────▼────┐           ┌────▼────┐
    │Equipos  │           │Equipos  │           │Equipos  │
    │Jugadores│           │Jugadores│           │Jugadores│
    │Grupos   │           │Grupos   │           │Grupos   │
    │Fases    │           │Fases    │           │Fases    │
    │Encuentros           │Encuentros           │Encuentros│
    │Dashboard            │Dashboard            │Dashboard │
    └─────────┘           └─────────┘           └─────────┘
```

---

## 2. Entidades Core del Sistema

### 2.1 Registro Global (Identidades Únicas)

| Entidad | Identificador Único | Propósito |
|---------|---------------------|-----------|
| **Jugador** | `documentoIdentidad` | Registro único de jugador en toda la app |
| **Equipo** | `codigoEquipo` | Registro único de equipo en toda la app |

**Nota:** Estas entidades viven en el "corazón" de la aplicación y se reutilizan entre torneos para evitar redundancia.

### 2.2 Entidades por Torneo (Mundo Independiente)

| Entidad | Descripción | Relación con Torneo |
|---------|-------------|---------------------|
| **Torneo** | Contenedor del mundo independiente | - |
| **EquipoTorneo** | Relación Equipo↔Torneo (inscripción) | `@ManyToOne` Torneo |
| **JugadorEquipoTorneo** | Relación Jugador↔Equipo↔Torneo | `@ManyToOne` EquipoTorneo |
| **Fase** | Etapas del torneo (grupos, octavos, etc.) | `@ManyToOne` Torneo |
| **Grupo** | Grupos dentro de una fase | `@ManyToOne` Fase |
| **Encuentro** | Partidos entre equipos | `@ManyToOne` Fase, Grupo |
| **TablaPosiciones** | Estadísticas de equipo en grupo | `@ManyToOne` Grupo, EquipoTorneo |
| **Sancion** | Sanciones de jugadores en torneo | `@ManyToOne` Torneo, JugadorEquipoTorneo |
| **EventoPartido** | Goles, tarjetas, etc. de un encuentro | `@ManyToOne` Encuentro |

### 2.3 Histórico Global (Alimentado por torneos)

| Entidad | Propósito | Datos Agregados |
|---------|-----------|-----------------|
| **CampeonHistorico** | Registro de campeones por torneo | Torneo finalizado → Campeón |
| **GoleadorHistorico** | Goleadores por torneo | Goles anotados en torneo |

---

## 3. Modelo de Datos - Torneo como Mundo Independiente

### 3.1 Diagrama de Relaciones

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            TORNEO (Mundo Independiente)                     │
│                                                                             │
│  ┌──────────┐       ┌──────────────┐       ┌──────────────┐                  │
│  │  Torneo  │◄──────┤ EquipoTorneo │◄──────┤   Equipo   │ (Global)          │
│  │          │ 1:N   │              │ N:1   │              │                  │
│  └────┬─────┘       └──────┬───────┘       └──────────────┘                  │
│       │                    │                                                │
│       │ 1:N               │ 1:N                                            │
│       ▼                    ▼                                                │
│  ┌─────────┐       ┌──────────────────┐                                     │
│  │  Fase   │       │JugadorEquipoTorneo│◄────┐                             │
│  │         │       └──────────────────┘     │                               │
│  └────┬────┘                               │                               │
│       │ 1:N                               │                               │
│       ▼                                   │                               │
│  ┌─────────┐       ┌──────────┐          │                               │
│  │  Grupo  │◄──────┤ TablaPos │          │                               │
│  │         │ 1:N   │          │          │                               │
│  └────┬────┘       └──────────┘          │                               │
│       │                                  │                               │
│       │ 1:N                              │                               │
│       ▼                                  │                               │
│  ┌───────────┐                          │                               │
│  │ Encuentro │◄─────────────────────────┘                               │
│  │           │ (Jugadores participan vía JugadorEquipoTorneo)           │
│  └─────┬─────┘                                                           │
│        │ 1:N                                                             │
│        ▼                                                                 │
│  ┌──────────────┐                                                       │
│  │ EventoPartido │ (Goles, tarjetas)                                      │
│  │              │                                                       │
│  └──────────────┘                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              REGISTRO GLOBAL                                │
│                                                                             │
│  ┌──────────┐      ┌──────────┐                                            │
│  │ Jugador  │      │  Equipo  │                                            │
│  │documento │      │  código  │                                            │
│  │identidad │      │  equipo  │                                            │
│  └──────────┘      └──────────┘                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              HISTÓRICO GLOBAL                                 │
│                                                                             │
│  ┌──────────────────┐      ┌──────────────────┐                              │
│  │CampeonHistorico  │      │GoleadorHistorico │                              │
│  │  - Equipo        │      │  - Jugador       │                              │
│  │  - Torneo        │      │  - Equipo        │                              │
│  │  - Fecha logro   │      │  - Torneo        │                              │
│  └──────────────────┘      │  - Total goles   │                              │
│                            └──────────────────┘                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Jerarquía de Dependencias

```
Torneo (Nivel 0 - Root)
├── Fase (Nivel 1)
│   ├── Grupo (Nivel 2)
│   │   ├── TablaPosiciones (Nivel 3)
│   │   └── Encuentro (Nivel 3)
│   │       └── EventoPartido (Nivel 4)
│   └── Encuentro (Nivel 2 - Playoffs)
│       └── EventoPartido (Nivel 3)
│
├── EquipoTorneo (Nivel 1 - Inscripción)
│   ├── JugadorEquipoTorneo (Nivel 2)
│   └── TablaPosiciones (Nivel 2)
│
└── Sancion (Nivel 1)

Registro Global (Independiente de torneos)
├── Jugador (referenciado por JugadorEquipoTorneo)
└── Equipo (referenciado por EquipoTorneo)

Histórico (Alimentado por torneos finalizados)
├── CampeonHistorico
└── GoleadorHistorico
```

---

## 4. Registro Global de Identidades Únicas

### 4.1 Jugador - Identificación por Documento

```java
@Entity
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID interno de BD
    
    @Column(unique = true, nullable = false, length = 50)
    private String documentoIdentidad;  // IDENTIFICADOR ÚNICO REAL
    
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String fotoUrl;
}
```

**Flujo de registro de jugador:**
1. Equipo intenta agregar jugador a torneo
2. Sistema busca jugador por `documentoIdentidad`
3. **Si existe:** Cargar datos automáticamente, validar que no esté en otro equipo del mismo torneo
4. **Si no existe:** Crear nuevo registro global, luego inscribir en equipo

### 4.2 Equipo - Identificación por Código

```java
@Entity
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID interno de BD
    
    @Column(unique = true, nullable = false, length = 6)
    private String codigoEquipo;  // IDENTIFICADOR ÚNICO REAL
    
    private String nombre;
    private String logoUrl;
}
```

**Flujo de inscripción de equipo a torneo:**
1. Buscar equipo por `codigoEquipo` en registro global
2. Validar que no esté ya inscrito en el mismo torneo (único constraint en `EquipoTorneo`)
3. Crear relación `EquipoTorneo` con asignación automática a grupo

---

## 5. Lo Que Ya Está Implementado ✅

### 5.1 Entidades Completas (13 entidades)

| Entidad | Estado | Notas |
|---------|--------|-------|
| Torneo | ✅ | Incluye campos para sincronización frontend (tipo, fase, fechas, etc.) |
| Equipo | ✅ | Identificación por `codigoEquipo` |
| Jugador | ✅ | Identificación por `documentoIdentidad` |
| EquipoTorneo | ✅ | Con asignación automática a grupos |
| JugadorEquipoTorneo | ✅ | Validación de unicidad por torneo |
| Fase | ✅ | Tipos: GRUPOS, OCTAVOS, CUARTOS, SEMIFINAL, FINAL |
| Grupo | ✅ | Vinculado a fase |
| Encuentro | ✅ | Vinculado a fase y grupo |
| TablaPosiciones | ✅ | Estadísticas completas (PJ, PG, PE, PP, GF, GC, DG, PTS) |
| Sancion | ✅ | Control de fechas cumplidas/activas |
| EventoPartido | ✅ | Eventos de partido (goles, tarjetas) |
| CampeonHistorico | ✅ | Registro de campeones |
| GoleadorHistorico | ✅ | Registro de goleadores |

### 5.2 Servicios Implementados

| Servicio | Métodos Clave | Estado |
|----------|---------------|--------|
| TorneoService | CRUD + distribuirEquiposEnGrupos + obtenerGruposConEquipos + obtenerFases + obtenerEstadisticas | ✅ |
| EquipoService | CRUD + findByCodigoEquipo | ✅ |
| JugadorService | CRUD + findByDocumentoIdentidad | ✅ |
| EquipoTorneoService | inscribirEquipoEnTorneo + asignación automática a grupos | ✅ |
| JugadorEquipoTorneoService | CRUD + validaciones | ✅ |

### 5.3 Endpoints Funcionales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/torneos` | Listar torneos |
| POST | `/api/torneos` | Crear torneo |
| GET | `/api/torneos/{id}` | Obtener torneo |
| PUT | `/api/torneos/{id}` | Actualizar torneo |
| DELETE | `/api/torneos/{id}` | Eliminar torneo |
| POST | `/api/torneos/{id}/iniciar` | Iniciar torneo |
| POST | `/api/torneos/{id}/finalizar` | Finalizar torneo |
| POST | `/api/torneos/{id}/generar-grupos` | Distribuir equipos en grupos (Snake Draft) |
| GET | `/api/torneos/{id}/grupos` | Obtener grupos con equipos |
| GET | `/api/torneos/{id}/fases` | Obtener fases del torneo |
| GET | `/api/torneos/{id}/estadisticas` | Obtener estadísticas (total equipos, fases, grupos) |
| GET | `/api/torneos/{id}/encuentros` | Obtener encuentros (placeholder) |
| POST | `/api/equipos-torneo/inscribir` | Inscribir equipo con asignación automática a grupo |
| CRUD | `/api/equipos` | Gestión de equipos globales |
| CRUD | `/api/jugadores` | Gestión de jugadores globales |

### 5.4 Lógicas Implementadas

- ✅ **Asignación automática a grupos:** Al inscribir equipo, calcula `(equiposInscritos % cantidadDeGrupos)`
- ✅ **Distribución Snake Draft:** Al generar grupos, distribuye equipos equitativamente
- ✅ **Validación de unicidad:** Equipo no puede inscribirse 2 veces al mismo torneo
- ✅ **Cascada de eliminación lógica:** Eliminación suave con flag `eliminado`

---

## 6. Funcionalidades Pendientes 🔧

### 6.1 Lógica de Negocio Crítica (Alta Prioridad)

| Funcionalidad | Descripción | Complejidad |
|---------------|-------------|-------------|
| **Validación jugador en múltiples equipos del mismo torneo** | Un jugador NO puede estar en 2 equipos del mismo torneo | Media |
| **Inscripción de jugador a equipo en torneo** | Flujo: buscar por documento → validar → inscribir | Media |
| **Generación automática de encuentros** | Crear partidos de grupo (todos contra todos) | Alta |
| **Registro de resultado de encuentro** | Actualizar goles, calcular puntos | Media |
| **Actualización automática de tabla de posiciones** | Al registrar resultado, recalcular estadísticas | Alta |
| **Cálculo de clasificados** | Determinar qué equipos pasan de fase | Media |
| **Cierre de fase y apertura de siguiente** | Transición entre fases | Media |
| **Registro de sanciones** | Crear y cumplir sanciones | Media |

### 6.2 Histórico y Estadísticas (Media Prioridad)

| Funcionalidad | Descripción |
|---------------|-------------|
| **Alimentar CampeonHistorico** | Al finalizar torneo, registrar campeón |
| **Alimentar GoleadorHistorico** | Al finalizar torneo, registrar goleadores |
| **Dashboard de torneo** | API con resumen completo de torneo |
| **Estadísticas de jugador por torneo** | Goles, partidos jugados, tarjetas |
| **Estadísticas de equipo por torneo** | Rendimiento histórico en torneos |

### 6.3 Funcionalidades Avanzadas (Baja Prioridad)

| Funcionalidad | Descripción |
|---------------|-------------|
| **Métodos de desempate configurables** | Penales, tiempo extra, gol visitante |
| **Sistema de plantillas** | Templates de torneos preconfigurados |
| **Notificaciones** | Eventos importantes del torneo |
| **Exportación de datos** | PDF/Excel de tablas, fixture |

---

## 7. Reglas de Negocio Críticas

### 7.1 Reglas de Torneo

```
R1: Torneo en CONFIGURACION puede recibir inscripciones de equipos
R2: Torneo en CONFIGURACION puede modificar sus normas
R3: Una vez iniciado (EN_CURSO), no se pueden inscribir más equipos
R4: Torneo EN_CURSO permite registrar resultados de encuentros
R5: Torneo FINALIZADO alimenta histórico (CampeonHistorico, GoleadorHistorico)
R6: Cada torneo es independiente - no comparte datos con otros torneos
R7: Un torneo debe tener mínimo 2 equipos para iniciarse
```

### 7.2 Reglas de Equipo

```
R8: Código de equipo es ÚNICO a nivel global
R9: Un equipo puede inscribirse en múltiples torneos
R10: Un equipo NO puede inscribirse 2 veces en el mismo torneo
R11: Un equipo puede tener jugadores diferentes en cada torneo
R12: Un equipo debe cumplir min/max de jugadores del torneo
```

### 7.3 Reglas de Jugador

```
R13: Documento de identidad es ÚNICO a nivel global
R14: Un jugador puede inscribirse en múltiples equipos
R15: Un jugador NO puede estar en 2 equipos del MISMO torneo
R16: Si jugador existe globalmente, se reutilizan sus datos
R17: Si jugador no existe, se crea registro global al inscribir
```

### 7.4 Reglas de Inscripción

```
R18: Inscripción de equipo a torneo asigna grupo automáticamente
R19: Inscripción de jugador a equipo valida registro global por documento
R20: Inscripción de jugador valida que no esté en otro equipo del torneo
R21: Fecha de inscripción se registra automáticamente
```

### 7.5 Reglas de Encuentros

```
R22: Encuentros se generan automáticamente al configurar fase de grupos
R23: Un equipo no puede jugar 2 partidos el mismo día en el mismo torneo
R24: Resultado solo registrable por admin o delegado de equipo
R25: Al registrar resultado, se actualiza tabla de posiciones automáticamente
R26: Sistema debe permitir editar resultado (con autorización)
```

### 7.6 Reglas de Tabla de Posiciones

```
R27: Puntos: PG=3, PE=1, PP=0
R28: Diferencia de gol = GF - GC
R29: Ordenamiento: PTS > DG > GF > (sorteo/directo según config)
R30: Tabla se actualiza automáticamente tras cada resultado
R31: Tabla histórica se mantiene incluso si torneo se borra
```

### 7.7 Reglas de Fases

```
32: Fase de GRUPOS permite múltiples grupos
R33: Fases eliminatorias (octavos, cuartos, etc.) son de ida o ida/vuelta
R34: Para avanzar fase, todos los encuentros de fase anterior deben estar cerrados
R35: Equipos clasificados determinados por posición en tabla de grupos
```

---

## 8. Endpoints y Servicios Existentes

### 8.1 TorneoController (`/api/torneos`)

```java
// CRUD básico
GET    /                    -> List<Torneo>
POST   /                    -> Torneo
GET    /{id}               -> Torneo
PUT    /{id}               -> Torneo
DELETE /{id}               -> void

// Operaciones de estado
POST   /{id}/iniciar       -> Torneo (cambia estado a EN_CURSO)
POST   /{id}/finalizar     -> Torneo (cambia estado a FINALIZADO)

// Gestión de grupos y fases
POST   /{id}/generar-grupos?formato=SOLO_IDA -> Distribuye equipos
GET    /{id}/grupos        -> List<Map> grupos con equipos
GET    /{id}/fases         -> List<Map> fases del torneo
GET    /{id}/encuentros    -> List<Map> encuentros (placeholder)
GET    /{id}/estadisticas  -> Map totalEquipos, totalFases, totalGrupos

// Validaciones
GET    /exists/nombre/{nombre} -> boolean
```

### 8.2 EquipoTorneoController (`/api/equipos-torneo`)

```java
// Inscripción con asignación automática
POST   /inscribir?torneoId=X&equipoId=Y -> EquipoTorneo

// CRUD estándar
GET    /              -> List<EquipoTorneo>
GET    /{id}         -> EquipoTorneo
GET    /torneo/{torneoId} -> List<EquipoTorneo>
GET    /equipo/{equipoId} -> List<EquipoTorneo>
PUT    /{id}         -> EquipoTorneo
DELETE /{id}         -> void (eliminación lógica)
```

### 8.3 Servicios a Implementar (Prioridad)

```java
// JugadorEquipoTorneoService - Métodos pendientes
- inscribirJugadorEnEquipo(Long equipoTorneoId, String documentoIdentidad)
- validarJugadorNoEnOtroEquipoDelTorneo(Long torneoId, String documento)
- obtenerJugadoresPorEquipoTorneo(Long equipoTorneoId)
- obtenerEstadisticasJugadorEnTorneo(Long jugadorId, Long torneoId)

// EncuentroService - Pendiente completo
- generarEncuentrosDeGrupo(Long grupoId, FormatoEncuentro formato)
- registrarResultado(Long encuentroId, Integer golesLocal, Integer golesVisitante)
- obtenerEncuentrosPorGrupo(Long grupoId)
- obtenerEncuentrosPorFase(Long faseId)

// TablaPosicionesService - Pendiente completo
- inicializarTablaParaEquipo(Long equipoTorneoId, Long grupoId)
- recalcularTablaTrasResultado(Long encuentroId)
- obtenerTablaPorGrupo(Long grupoId)
- obtenerPosicionEquipo(Long equipoTorneoId, Long grupoId)

// SancionService - Pendiente completo
- crearSancion(Long jugadorEquipoTorneoId, Integer cantidadFechas)
- cumplirFechaSancion(Long sancionId)
- obtenerSancionesActivas(Long torneoId)
- validarJugadorHabilitado(Long jugadorEquipoTorneoId, Long encuentroId)

// FaseService - Pendiente
- crearSiguienteFase(Long torneoId, TipoFase tipo)
- cerrarFase(Long faseId)
- calcularClasificados(Long faseId)

// CampeonHistoricoService - Pendiente
- registrarCampeon(Long torneoId, Long equipoId)
- obtenerCampeonesPorEquipo(Long equipoId)
- obtenerCampeonesPorTorneo(Long torneoId)

// GoleadorHistoricoService - Pendiente
- registrarGoleadoresDelTorneo(Long torneoId)
- obtenerGoleadoresPorTorneo(Long torneoId)
- obtenerGoleadoresPorJugador(Long jugadorId)
```

---

## 9. Roadmap de Implementación

### Fase 1: Inscripción de Jugadores (Semana 1)
- [ ] Endpoint: Inscribir jugador a equipo en torneo
- [ ] Validación: Jugador no en otro equipo del mismo torneo
- [ ] Búsqueda por documento de identidad
- [ ] Creación automática de jugador global si no existe

### Fase 2: Gestión de Encuentros (Semana 2)
- [ ] Generación automática de fixture de grupos
- [ ] CRUD de encuentros
- [ ] Registro de resultado
- [ ] Cálculo automático de tabla de posiciones

### Fase 3: Tabla de Posiciones (Semana 3)
- [ ] Inicialización de tablas al crear grupo
- [ ] Recálculo automático tras cada resultado
- [ ] Endpoint para consultar tabla por grupo
- [ ] Ordenamiento según reglas de desempate

### Fase 4: Fases y Clasificación (Semana 4)
- [ ] Transición entre fases
- [ ] Cálculo de clasificados
- [ ] Generación de fases eliminatorias
- [ ] Cierre de fase anterior

### Fase 5: Sanciones (Semana 5)
- [ ] Registro de sanciones
- [ ] Control de fechas cumplidas
- [ ] Validación de jugadores habilitados
- [ ] Gestión de sanciones activas

### Fase 6: Histórico (Semana 6)
- [ ] Alimentar CampeonHistorico al finalizar torneo
- [ ] Alimentar GoleadorHistorico al finalizar torneo
- [ ] Dashboard con estadísticas históricas

---

## 10. Decisiones Técnicas y Arquitectura

### 10.1 Estrategia de Identificadores

```
┌────────────────────────────────────────────────────────────────┐
│  ID (PK)         vs        Identificador de Negocio             │
├────────────────────────────────────────────────────────────────┤
│  - ID: Auto-generado por BD                                    │
│  - Usado internamente para relaciones JPA                     │
│  - NO expuesto al usuario                                     │
├────────────────────────────────────────────────────────────────┤
│  - Identificador de Negocio: Definido por dominio             │
│  - documentoIdentidad para Jugador                            │
│  - codigoEquipo para Equipo                                   │
│  - USADO para búsquedas y validaciones                        │
│  - EXPUESTO al usuario/frontend                               │
└────────────────────────────────────────────────────────────────┘
```

### 10.2 Eliminación Lógica vs Física

```
┌────────────────────────────────────────────────────────────────┐
│              ELIMINACIÓN LÓGICA (Soft Delete)                   │
├────────────────────────────────────────────────────────────────┤
│  ✅ Preserva integridad histórica                              │
│  ✅ Permite recuperación                                       │
│  ✅ Mantiene relaciones en encuentros pasados                  │
│  ❌ Requiere filtro en todas las queries (eliminado = false)   │
├────────────────────────────────────────────────────────────────┤
│  Implementación: Campo 'eliminado' booleano en entidades       │
│  Torneo, Equipo, Jugador usan eliminación lógica               │
└────────────────────────────────────────────────────────────────┘
```

### 10.3 Transacciones y Consistencia

```java
// Operaciones que DEBEN ser @Transactional:
1. Inscripción de equipo a torneo (crea EquipoTorneo + asigna grupo)
2. Inscripción de jugador a equipo (validaciones + creación)
3. Registro de resultado (actualiza encuentro + tabla de posiciones)
4. Cierre de fase (marca fase + crea siguiente fase)
5. Finalización de torneo (marca torneo + alimenta histórico)
```

### 10.4 Convenciones de Nomenclatura

| Elemento | Convención | Ejemplo |
|----------|------------|---------|
| Entidades | PascalCase singular | `EquipoTorneo`, `JugadorEquipoTorneo` |
| Tablas | snake_case | `equipo_torneo`, `jugador_equipo_torneo` |
| Campos | camelCase | `documentoIdentidad`, `codigoEquipo` |
| Endpoints | kebab-case plural | `/api/equipos-torneo`, `/api/jugadores` |
| Métodos | camelCase descriptivo | `inscribirEquipoEnTorneo()` |

### 10.5 Estructura de Paquetes

```
com.GolsystemV2.Backend/
├── config/          # Configuraciones (CORS, Security, etc.)
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # Entidades JPA
├── enums/           # Enumeraciones
├── exception/       # Excepciones custom
├── repository/      # Repositorios Spring Data JPA
├── service/         # Interfaces de servicio
│   └── impl/        # Implementaciones de servicio
└── BackendApplication.java
```

---

## 11. Notas para el Frontend

### 11.1 Dashboard de Torneo - Requerimientos

El frontend debe solicitar un endpoint que retorne:

```json
{
  "torneo": { /* info del torneo */ },
  "estadisticas": {
    "totalEquipos": 8,
    "totalJugadores": 120,
    "totalEncuentros": 28,
    "encuentrosJugados": 14,
    "encuentrosPendientes": 14
  },
  "faseActual": "GRUPOS",
  "grupos": [ /* grupos con equipos y posiciones */ ],
  "proximosEncuentros": [ /* próximos partidos */ ],
  "ultimosResultados": [ /* resultados recientes */ ],
  "tablaGoleadores": [ /* goleadores del torneo */ ],
  "sancionesActivas": [ /* sanciones vigentes */ ]
}
```

### 11.2 Flujo de Inscripción de Jugador

```
1. Frontend: Usuario ingresa documento de identidad
2. Backend: GET /api/jugadores/buscar?documento=12345
   ├─ Si existe: Retorna datos del jugador
   └─ Si no existe: Retorna 404
3. Frontend: Muestra datos o formulario de registro
4. Backend: POST /api/jugador-equipo-torneo/inscribir
   {
     "equipoTorneoId": 1,
     "documentoIdentidad": "12345",
     "numeroCamiseta": 10
   }
5. Backend: Valida que jugador no esté en otro equipo del torneo
6. Backend: Crea relación JugadorEquipoTorneo
7. Frontend: Confirma inscripción exitosa
```

---

## 12. Próximos Pasos Inmediatos

### Prioridad 1: Inscripción de Jugadores
1. Crear `JugadorEquipoTorneoController` con endpoint de inscripción
2. Implementar validación: jugador no en otro equipo del mismo torneo
3. Implementar búsqueda de jugador por documento
4. Crear flujo de registro de nuevo jugador global

### Prioridad 2: Fixture y Encuentros
1. Implementar generación automática de encuentros de grupo
2. Crear endpoint para consultar fixture
3. Implementar registro de resultado

### Prioridad 3: Tabla de Posiciones
1. Implementar inicialización de tablas
2. Implementar recálculo automático
3. Crear endpoint de consulta

---

**Documento mantenido por:** Backend Team  
**Actualización:** Cada vez que se implemente lógica nueva o se detecten errores de diseño
