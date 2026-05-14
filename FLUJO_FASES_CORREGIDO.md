# Flujo de Fases Corregido - GolSystem V2

## Fecha: 15 de Abril 2026
## Cambio Principal: Fases como Filtros de Equipos

---

## 🎯 Concepto

Las **fases** ahora actúan como **filtros progresivos**:
- Cada fase recibe equipos seleccionados
- Los equipos compiten en grupos
- Al finalizar, ciertos equipos **clasifican** a la siguiente fase
- Los demás quedan **eliminados**
- Esto continúa hasta que solo queda **1 campeón**

---

## 📋 Flujo Completo

### Paso 1: Torneo (Configuración del Mundo)
```
Torneo
├── cantidadDeGrupos: 2      ← Define cuántos grupos tendrán las fases
├── formatoEncuentro: SOLO_IDA
└── estado: CONFIGURACION → EN_CURSO → FINALIZADO
```

### Paso 2: Crear Fase 1
```
Usuario abre "Configurar Fase"
↓
PhaseConfig.jsx carga cantidadDeGrupos del torneo
↓
Muestra: "Cantidad de grupos: 2" (solo lectura)
↓
Usuario selecciona equipos de los disponibles
↓
POST /api/fases/configurar
{
  torneoId: 1,
  numeroFase: 1,
  cantidadGrupos: 2,           ← Del torneo
  equipos: [
    {id: 1}, {id: 2}, {id: 3}, {id: 4}
  ]
}
↓
Backend:
  1. Crea la fase en estado CONFIGURACION
  2. Asigna equipos mediante ManyToMany (fase_equipos)
  3. Crea grupos vacíos: Grupo A, Grupo B
```

### Paso 3: Activar Fase 1
```
Usuario presiona "Activar Fase"
↓
POST /api/fases/{id}/activar
↓
Backend:
  1. Obtiene equipos de fase.getEquipos()
  2. Los convierte a EquipoTorneo
  3. Distribuye equipos en grupos (Snake Draft)
     - Grupo A: Equipo 1, Equipo 4
     - Grupo B: Equipo 2, Equipo 3
  4. Genera fixture automáticamente
  5. Estado cambia a EN_CURSO
```

### Paso 4: Jugar Partidos
```
Fase 1 - EN_CURSO
├── Grupo A          ├── Grupo B
│   ├── Eq 1 vs Eq 4 │   ├── Eq 2 vs Eq 3
│   ├── Eq 4 vs Eq 1 │   ├── Eq 3 vs Eq 2
│   └── Tabla Posiciones    └── Tabla Posiciones
```

### Paso 5: Cerrar Fase 1
```
Todos los partidos finalizados
↓
Usuario presiona "Cerrar Fase"
↓
Backend:
  1. Calcula tabla de posiciones por grupo
  2. Marca fase como FINALIZADA
  3. Determina clasificados según equiposClasificadosPorGrupo
     - Si equiposClasificadosPorGrupo = 1:
       • Grupo A: 1° clasifica
       • Grupo B: 1° clasifica
     - Total: 2 equipos clasifican a Fase 2
```

### Paso 6: Crear Fase 2
```
Usuario abre "Configurar Nueva Fase"
↓
PhaseConfig.jsx:
  • Calcula automáticamente: numeroFase = 2
  • Usa misma cantidadDeGrupos (o permite cambiar)
↓
Usuario selecciona equipos clasificados de Fase 1
↓
POST /api/fases/configurar
{
  torneoId: 1,
  numeroFase: 2,
  cantidadGrupos: 1,           ← Podría ser diferente
  equipos: [
    {id: 1}, {id: 2}         ← Solo los que clasificaron
  ]
}
```

### Paso 7: Continuar hasta Final
```
Fase 2 → Fase 3 → ... → Fase Final
↓
Última fase (ELIMINACION_DIRECTA o GRUPOS único)
↓
Campeón determinado
↓
Registrado en CampeonHistorico
```

---

## 🔧 Cambios Técnicos Realizados

### Backend - PhaseManagerServiceImpl.java

#### 1. `configurarFase()` - Validación de Equipos
```java
// ANTES: Validaba equipos inscritos en el torneo
Integer totalEquipos = equipoTorneoRepository.countEquiposActivosPorTorneo(...)

// AHORA: Valida equipos seleccionados en el DTO
if (dto.getEquipos() == null || dto.getEquipos().isEmpty()) {
    throw new RuntimeException("Debes seleccionar al menos 1 equipo...");
}
```

#### 2. `obtenerEquiposParaFase()` - Lógica Corregida
```java
// ANTES: Buscaba todos los equipos del torneo
List<EquipoTorneo> todos = equipoTorneoRepository.findByTorneoIdAndEliminadoFalse(torneoId);

// AHORA: Usa equipos asignados a la fase mediante ManyToMany
if (fase.getEquipos() == null || fase.getEquipos().isEmpty()) {
    throw new RuntimeException("La fase no tiene equipos asignados...");
}
for (Equipo equipo : fase.getEquipos()) {
    EquipoTorneo et = equipoTorneoRepository.findByTorneoIdAndEquipoId(...);
    equiposTorneo.add(et);
}
```

#### 3. `obtenerFaseOExcepcion()` - Carga con JOIN FETCH
```java
// ANTES: Cargaba fase sin equipos (LazyInitializationException)
return faseRepository.findById(faseId)...

// AHORA: Carga fase con equipos en una sola query
return faseRepository.findByIdWithEquipos(faseId)...
```

#### 4. Orden de Operaciones Corregido
```java
// 7. Crear la fase
Fase guardada = faseRepository.save(nuevaFase);

// 8. Asignar equipos seleccionados (PRIMERO)
asignarEquiposAFase(guardada, dto.getEquipos());

// 9. Crear grupos vacíos (DESPUÉS)
crearGruposVacios(guardada, dto.getCantidadGrupos());
```

### Frontend - PhaseConfig.jsx

#### 1. Sincronización con Torneo
```javascript
useEffect(() => {
  // Prioridad: torneo.cantidadDeGrupos > grupos.length > 1
  let cantidadGrupos = 1
  if (torneo?.cantidadDeGrupos) {
    cantidadGrupos = torneo.cantidadDeGrupos
  } else if (grupos?.length > 0) {
    cantidadGrupos = grupos.length
  }
  
  setFormData(prev => ({
    ...prev,
    configuracionGrupos: {
      ...prev.configuracionGrupos,
      cantidadGrupos: cantidadGrupos
    }
  }))
}, [torneo, grupos])
```

#### 2. Campo de Solo Lectura
```javascript
<Input
  type="number"
  name="configuracionGrupos.cantidadGrupos"
  value={formData.configuracionGrupos.cantidadGrupos}
  disabled={true}              ← ← ← Solo lectura
  className="... cursor-not-allowed"
/>
<p className="text-xs text-white/60">
  {torneo?.cantidadDeGrupos
    ? `Configuración oficial del torneo: ${torneo.cantidadDeGrupos} grupo(s)`
    : `Configure la cantidad de grupos en la edición del torneo`
  }
</p>
```

#### 3. Payload Correcto para Backend
```javascript
// ANTES: Enviaba estructura anidada
const configData = {
  ...formData,                   // Incluía configuracionGrupos: {...}
  equipos: selectedEquipos
}

// AHORA: Mapea a campos raíz esperados por backend
const configData = {
  torneoId: formData.torneoId,
  numeroFase: formData.numeroFase,
  tipoFase: formData.tipoFase,
  cantidadGrupos: formData.configuracionGrupos.cantidadGrupos,  // ← Desanidado
  equiposClasificadosPorGrupo: formData.configuracionGrupos.equiposClasificadosPorGrupo,
  equipos: selectedEquipos.map(e => ({ id: e.id }))
}
```

---

## 📊 Diagrama de Datos

```
┌─────────────────────────────────────────────────────────────┐
│                        TORNEO                               │
│  ┌─────────────────┐                                         │
│  │ cantidadDeGrupos│ ← Define estructura de grupos          │
│  │     = 2         │                                         │
│  └─────────────────┘                                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Define
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      FASE 1 (CONFIGURACION)                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Equipos Asignados (ManyToMany)                     │   │
│  │  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐       │   │
│  │  │ Eq 1 │  │ Eq 2 │  │ Eq 3 │  │ Eq 4 │       │   │
│  │  └───┬──┘  └───┬──┘  └───┬──┘  └───┬──┘       │   │
│  └──────┼─────────┼─────────┼─────────┼─────────┘   │
│         │         │         │         │                 │
│  ┌──────┴─────────┴─────────┴─────────┴─────────┐      │
│  │            GRUPOS VACÍOS                      │      │
│  │  ┌─────────────┐      ┌─────────────┐          │      │
│  │  │  Grupo A   │      │  Grupo B   │          │      │
│  │  │  (vacío)   │      │  (vacío)   │          │      │
│  │  └─────────────┘      └─────────────┘          │      │
│  └─────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
            POST /activar   │   Distribuye equipos
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      FASE 1 (EN_CURSO)                      │
│  ┌─────────────────────────────┐                            │
│  │        GRUPO A              │  ┌────────────────────────┐│
│  │  ┌──────┐     ┌──────┐    │  │        GRUPO B        ││
│  │  │ Eq 1 │ vs  │ Eq 4 │    │  │  ┌──────┐   ┌──────┐  ││
│  │  └──────┘     └──────┘    │  │  │ Eq 2 │ vs │ Eq 3 │  ││
│  │  ┌──────┐     ┌──────┐    │  │  └──────┘   └──────┘  ││
│  │  │ Eq 4 │ vs  │ Eq 1 │    │  │  ┌──────┐   ┌──────┐  ││
│  │  └──────┘     └──────┘    │  │  │ Eq 3 │ vs │ Eq 2 │  ││
│  │                           │  │  └──────┘   └──────┘  ││
│  │  Tabla Posiciones         │  │  Tabla Posiciones     ││
│  │  1°: Eq 1 (clasifica)     │  │  1°: Eq 2 (clasifica) ││
│  │  2°: Eq 4 (elimina)       │  │  2°: Eq 3 (elimina)   ││
│  └───────────────────────────┘  └────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Eq 1 y Eq 2 clasifican
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      FASE 2 (CONFIGURACION)                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Equipos Asignados (ManyToMany)                     │   │
│  │  ┌────────┐  ┌────────┐                            │   │
│  │  │ Eq 1 │  │ Eq 2 │                            │   │
│  │  └───┬──┘  └───┬──┘                            │   │
│  └──────┼─────────┼───────────────────────────────┘   │
│         │         │                                    │
│  ┌──────┴─────────┴────────┐                           │
│  │      GRUPO ÚNICO        │ ← Puede tener diferente  │
│  │  ┌──────────────────┐   │   cantidad de grupos     │
│  │  │    (vacío)       │   │                           │
│  │  └──────────────────┘   │                           │
│  └─────────────────────────┘                           │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Validaciones Implementadas

### Backend - Torneo (update)

1. **Solo editable en CONFIGURACION**:
   ```java
   if (!EstadoTorneo.CONFIGURACION.equals(existingTorneo.getEstado()))
       throw new RuntimeException("Solo se pueden editar torneos en estado CONFIGURACION.");
   ```

2. **Cantidad de grupos 1-15**:
   ```java
   if (torneo.getCantidadDeGrupos() < 1 || torneo.getCantidadDeGrupos() > 15)
       throw new RuntimeException("La cantidad de grupos debe estar entre 1 y 15.");
   ```

3. **Grupos no superan equipos inscritos**:
   ```java
   if (torneo.getCantidadDeGrupos() > equiposInscritos.size())
       throw new RuntimeException("La cantidad de grupos no puede superar " +
                                 "la cantidad de equipos inscritos.");
   ```

4. **Redistribución automática** (cuando cambia cantidadDeGrupos):
   ```java
   // Solo si Fase 1 está en CONFIGURACION
   private void redistribuirEquiposEnFase1(Torneo torneo, int nuevaCantidadGrupos) {
       // 1. Eliminar grupos existentes
       // 2. Crear nuevos grupos vacíos (A, B, C...)
       // 3. Redistribuir equipos usando Snake Draft
   }
   ```

### Backend - Fases (configurarFase)

3. **Equipos requeridos**: 
   ```java
   if (dto.getEquipos() == null || dto.getEquipos().isEmpty())
       throw new RuntimeException("Debes seleccionar al menos 1 equipo...");
   ```

4. **Mínimo 2 equipos (Fase 1)**:
   ```java
   if (totalEquiposSeleccionados < 2)
       throw new RuntimeException("Se necesitan al menos 2 equipos...");
   ```

5. **Suficientes equipos para grupos**:
   ```java
   if (totalEquiposSeleccionados < cantidadGrupos)
       throw new RuntimeException("No hay suficientes equipos...");
   ```

6. **Clasificados no superan equipos fase anterior (Fase N>1)**:
   ```java
   int clasificadosEstaFase = cantidadGrupos * equiposClasificadosPorGrupo;
   int equiposFaseAnterior = faseAnterior.getEquipos().size();
   
   if (clasificadosEstaFase > equiposFaseAnterior)
       throw new RuntimeException("La cantidad de clasificados no puede superar " +
                                 "la cantidad de equipos de la fase anterior.");
   
   if (equiposSeleccionados > clasificadosEstaFase)
       throw new RuntimeException("Has seleccionado X equipos, pero solo pueden " +
                                 "clasificar Y equipos según la configuración.");
   ```

7. **Equipos asignados antes de activar**:
   ```java
   if (fase.getEquipos() == null || fase.getEquipos().isEmpty())
       throw new RuntimeException("La fase no tiene equipos asignados...");
   ```

### Frontend

1. **Al menos 1 equipo seleccionado**:
   ```javascript
   {selectedEquipos.length === 0 && (
     <p className="text-xs text-yellow-400">
       ⚠️ Debes seleccionar al menos un equipo para la fase
     </p>
   )}
   ```

2. **Grupos existentes**:
   ```javascript
   {(!grupos || grupos.length === 0) && (
     <div className="p-4 bg-yellow-500/10 ...">
       ⚠️ No hay grupos generados...
     </div>
   )}
   ```

---

## 🚀 Multi-Fase - IMPLEMENTADO

### ✅ Fase N > 1 - Automático:

1. **✅ Clasificación automática por tabla de posiciones**:
   - Al crear Fase N>1, equipos se obtienen automáticamente de la fase anterior
   - Se usa la tabla de posiciones de cada grupo de la fase anterior
   - Se toman los top N según `equiposClasificadosPorGrupo`
   - Orden: PTS → DG → GF (FIFA estándar)

2. **✅ Lógica implementada**:
   ```java
   // En configurarFase() para Fase N>1:
   List<EquipoTorneo> clasificados = obtenerClasificadosDeFaseAnteriorPorTabla(faseAnterior);
   
   // Método obtenerClasificadosDeFaseAnteriorPorTabla():
   for (Grupo grupo : gruposAnterior) {
       List<TablaPosiciones> tabla = tablaPosicionesService.findTablaPosicionesOrdenada(grupo.getId());
       // Tomar top N de cada grupo
       for (int i = 0; i < Math.min(equiposClasificadosPorGrupo, tabla.size()); i++) {
           clasificados.add(tabla.get(i).getEquipoTorneo());
       }
   }
   ```

3. **✅ Validaciones**:
   - Fase anterior debe estar FINALIZADA
   - Clasificados no superan equipos disponibles
   - Tabla de posiciones debe existir y tener datos

4. **Flujo completo**:
   ```
   Fase 1 (8 equipos, 2 grupos, 2 clasifican por grupo)
   ├── Todos los partidos finalizados
   ├── Tabla de posiciones calculada
   ├── Cerrar Fase 1 → FINALIZADA
   │
   └── Crear Fase 2 automáticamente:
       ├── Obtiene top 2 de Grupo A de Fase 1
       ├── Obtiene top 2 de Grupo B de Fase 1
       ├── Total: 4 equipos clasificados
       └── Distribuye en nuevos grupos de Fase 2
   ```

---

## 📝 Resumen

| Aspecto | Antes | Ahora |
|---------|-------|-------|
| **Fuente de grupos** | Backend automático | Configuración del torneo (editable en CONFIGURACION) |
| **Cantidad grupos** | Ilimitada/sin validar | 1-15 grupos (validado) |
| **Redistribución** | Manual | ✅ Automática al cambiar cantidadDeGrupos |
| **Asignación de equipos** | Automática por fase | Manual por fase (selección) |
| **Relación equipos-fase** | EquipoTorneo.grupo | Fase.equipos (ManyToMany) |
| **Multi-fase** | No soportado | ✅ Soportado con validaciones |
| **Validación clasificados** | N/A | ✅ No superan equipos fase anterior |
| **Clasificación automática** | N/A | ✅ Por tabla de posiciones (PTS→DG→GF) |
| **Fase N>1 equipos** | Manual | ✅ Automático de fase anterior |
| **Edición torneo** | Siempre permitida | ✅ Solo en CONFIGURACION |

**Listo para usar!** 🎉
