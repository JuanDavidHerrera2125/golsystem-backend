# 📡 GOLSYSTEM V2 - CONTRATO DE API
## Backend-Frontend Synchronization Document

**Última actualización:** 2026-04-12  
**Versión API:** 1.0.0  
**URL Base:** `http://localhost:8080/api`

---

## 🚀 ENDPOINTS PRINCIPALES

### 1. TORNEOS (`/api/torneos`)

| Método | Endpoint | Descripción | Body/Params |
|--------|----------|-------------|-------------|
| GET | `/` | Listar todos los torneos | - |
| GET | `/{id}` | Obtener torneo por ID | `id: Long` |
| GET | `/nombre/{nombre}` | Buscar por nombre | `nombre: String` |
| GET | `/estado/{estado}` | Filtrar por estado | `estado: CONFIGURACION/EN_CURSO/FINALIZADO` |
| GET | `/configuracion` | Torneos en configuración | - |
| GET | `/curso` | Torneos en curso | - |
| POST | `/` | Crear torneo | `Torneo` JSON |
| PUT | `/{id}` | Actualizar torneo | `id: Long, Torneo` JSON |
| PUT | `/{id}/iniciar` | Iniciar torneo | `id: Long` |
| PUT | `/{id}/finalizar` | Finalizar torneo | `id: Long` |
| DELETE | `/{id}` | Eliminar torneo | `id: Long` |

**JSON Torneo (Request/Response):**
```json
{
  "id": 1,
  "nombre": "Torneo Primavera 2026",
  "logoUrl": "https://... (TEXT, sin límite de longitud)",
  "categoria": "MASCULINO",
  "estado": "CONFIGURACION",
  "minJugadores": 7,
  "maxJugadores": 12,
  "fechaCreacion": "2026-04-12"
}
```

---

### 2. EQUIPOS (`/api/equipos`)

| Método | Endpoint | Descripción | Body/Params |
|--------|----------|-------------|-------------|
| GET | `/` | Listar equipos | - |
| GET | `/{id}` | Obtener equipo | `id: Long` |
| GET | `/codigo/{codigoEquipo}` | Buscar por código | `codigoEquipo: String` |
| GET | `/activos` | Equipos activos | - |
| POST | `/` | Crear equipo + inscribir a torneo | `EquipoRequestDTO` |
| PUT | `/{id}` | Actualizar equipo | `id: Long, Equipo` |
| PUT | `/{id}/desactivar` | Desactivar | `id: Long` |
| PUT | `/{id}/activar` | Reactivar | `id: Long` |
| DELETE | `/{id}` | Eliminar | `id: Long` |

**EquipoRequestDTO (Solo POST):**
```json
{
  "nombre": "Los Titanes",
  "codigoEquipo": "TIT001",
  "logoUrl": "https://...",
  "activo": true,
  "torneoId": 1  // Opcional: inscribe automáticamente al torneo
}
```

**Equipo (Response):**
```json
{
  "id": 1,
  "nombre": "Los Titanes",
  "codigoEquipo": "TIT001",
  "logoUrl": "https://...",
  "activo": true,
  "createdAt": "2026-04-12T10:30:00",
  "updatedAt": "2026-04-12T10:30:00"
}
```

---

### 3. EQUIPO-TORNEO (`/api/equipos-torneo`)

Relación entre equipos y torneos (inscripciones).

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/torneo/{torneoId}/activos` | Equipos activos en un torneo |
| GET | `/torneo/{torneoId}/count-activos` | Cantidad de equipos |
| GET | `/torneo/{torneoId}/equipo/{equipoId}` | Verificar si existe relación |
| POST | `/` | Inscribir equipo existente a torneo |
| PUT | `/{id}/eliminar` | Marcar como eliminado (soft delete) |
| PUT | `/{id}/reactivar` | Reactivar en torneo |

**EquipoTorneo (Request/Response):**
```json
{
  "id": 1,
  "torneo": { "id": 1 },
  "equipo": { "id": 2 },
  "eliminado": false,
  "fechaInscripcion": "2026-04-12"
}
```

---

### 4. JUGADORES (`/api/jugadores`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar jugadores |
| GET | `/{id}` | Obtener por ID |
| GET | `/documento/{documentoIdentidad}` | Buscar por documento |
| GET | `/activos` | Jugadores activos |
| POST | `/` | Crear jugador |
| PUT | `/{id}` | Actualizar |
| DELETE | `/{id}` | Eliminar |

**Jugador (Request/Response):**
```json
{
  "id": 1,
  "documentoIdentidad": "12345678",
  "nombre": "Juan",
  "apellido": "Pérez",
  "fechaNacimiento": "1995-05-15",
  "fotoUrl": "https://...",
  "activo": true
}
```

---

### 5. JUGADOR-EQUIPO-TORNEO (`/api/jugadores-equipos-torneo`)

Vincula jugadores a equipos dentro de un torneo.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/equipo-torneo/{equipoTorneoId}/activos` | Jugadores activos en equipo |
| GET | `/equipo-torneo/{equipoTorneoId}/count-activos` | Cantidad |
| GET | `/jugador/{jugadorId}/torneo/{torneoId}` | Buscar vinculación |
| POST | `/` | Vincular jugador a equipo |
| PUT | `/{id}/desvincular` | Desvincular (soft) |
| PUT | `/{id}/vincular` | Reactivar |

---

### 6. HISTÓRICOS (`/api/historicos`)

| Método | Endpoint | Descripción | Response |
|--------|----------|-------------|----------|
| GET | `/campeones` | Lista de campeones históricos | `List<CampeonHistoricoDTO>` |
| GET | `/goleadores` | Lista de goleadores históricos | `List<GoleadorHistoricoDTO>` |

---

### 7. CAMPEONES HISTÓRICOS (`/api/campeones-historicos`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/torneo/{torneoId}` | Campeones de un torneo |
| GET | `/equipo/{equipoId}` | Campeonatos de un equipo |
| POST | `/registrar-campeon?torneoId=X&equipoId=Y` | Registrar campeón |

---

### 8. GOLEADORES HISTÓRICOS (`/api/goleadores-historicos`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/torneo/{torneoId}` | Goleadores de un torneo |
| GET | `/jugador/{jugadorId}` | Torneos como goleador |
| GET | `/torneo/{torneoId}/tabla-goleadores` | Ranking de goleadores |
| PUT | `/actualizar-goles?jugadorId=X&equipoId=Y&torneoId=Z&totalGoles=N` | Actualizar |

---

## 📊 DTOs COMPLETOS

### CampeonHistoricoDTO
```json
{
  "id": 1,
  "fechaLogro": "2026-04-12",
  "equipo": {
    "id": 1,
    "codigoEquipo": "TIT001",
    "nombre": "Los Titanes",
    "logoUrl": "...",
    "activo": true
  },
  "torneo": {
    "id": 1,
    "nombre": "Torneo Primavera",
    "logoUrl": "...",
    "categoria": "MASCULINO",
    "estado": "FINALIZADO",
    "minJugadores": 7,
    "maxJugadores": 12,
    "fechaCreacion": "2026-04-01"
  }
}
```

### GoleadorHistoricoDTO
```json
{
  "id": 1,
  "totalGoles": 15,
  "jugador": {
    "id": 1,
    "documentoIdentidad": "12345678",
    "nombre": "Juan",
    "apellido": "Pérez",
    "fechaNacimiento": "1995-05-15",
    "fotoUrl": "...",
    "activo": true
  },
  "equipo": { "id": 1, "nombre": "Los Titanes", ... },
  "torneo": { "id": 1, "nombre": "Torneo Primavera", ... }
}
```

---

## ⚠️ MANEJO DE ERRORES

Todos los endpoints retornan errores en formato JSON:

```json
{
  "error": "Error de validación",
  "message": "Ya existe un equipo con el código: TIT001"
}
```

**Códigos HTTP:**
- `200 OK` - Éxito
- `201 Created` - Creado (si aplica)
- `400 Bad Request` - Error de validación/negocio
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🔧 CONFIGURACIÓN CORS

El backend acepta peticiones desde:
- `http://localhost:5173` (Vite dev server)
- `*` (todos los orígenes en desarrollo)

**Headers permitidos:** Todos (`*`)
**Métodos permitidos:** GET, POST, PUT, DELETE, OPTIONS
**Credenciales:** Soportadas

---

## 📝 FLUJOS RECOMENDADOS

### Crear Torneo Completo:
```
1. POST /api/torneos → Crear torneo (obtener torneoId)
2. POST /api/equipos → Crear equipos con torneoId
   O
   POST /api/equipos (sin torneoId) → POST /api/equipos-torneo (relación manual)
3. POST /api/jugadores → Crear jugadores
4. POST /api/jugadores-equipos-torneo → Vincular jugadores a equipos
```

### Inscribir Equipo Existente:
```
POST /api/equipos-torneo
{
  "torneo": { "id": 1 },
  "equipo": { "id": 2 },
  "eliminado": false
}
```

### Registrar Campeón:
```
POST /api/campeones-historicos/registrar-campeon?torneoId=1&equipoId=2
```

---

## 🧪 PRUEBAS SUGERIDAS

### Test 1: Crear Torneo
```bash
curl -X POST http://localhost:8080/api/torneos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","codigoEquipo":"TST001","categoria":"MASCULINO","minJugadores":7,"maxJugadores":12}'
```

### Test 2: Crear Equipo con Torneo
```bash
curl -X POST http://localhost:8080/api/equipos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Equipo A","codigoEquipo":"EQ001","torneoId":1}'
```

### Test 3: Verificar CORS
```bash
curl -I -X OPTIONS http://localhost:8080/api/torneos \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET"
```

---

## 🐛 DEBUG CHECKLIST

Si hay errores 400:
- [ ] Verificar que `torneoId` esté en el payload (si es POST /equipos)
- [ ] Verificar unicidad de `codigoEquipo` (6 caracteres máx)
- [ ] Verificar que campos obligatorios no sean null
- [ ] Revisar formato de fecha: `"yyyy-MM-dd"`

Si hay errores 404:
- [ ] Verificar que el ID existe en la base de datos
- [ ] Revisar si hay prefijo `/api` en la URL

Si recibe HTML en lugar de JSON:
- [ ] Verificar URL completa: `http://localhost:8080/api/...`
- [ ] Revisar CORS en navegador (F12 → Network)
- [ ] Verificar que el servidor backend esté corriendo

---

## 📞 CANAL DE COMUNICACIÓN

### Para reportar errores al backend:

**Formato de reporte:**
```
1. Endpoint: POST /api/equipos
2. Payload enviado: { ... }
3. Respuesta recibida: { ... }
4. Error esperado/inesperado: ...
5. Comportamiento actual: ...
```

### Respuesta del backend:

Después de cada fix, se actualizará:
- Este documento (API_CONTRACT.md)
- El código fuente con comentarios de versión
- Los logs de cambios en el repo

---

## ✅ ESTADO ACTUAL (2026-04-12 12:40)

| Módulo | Estado | Notas |
|--------|--------|-------|
| Torneos | ✅ OK | URLs largas soportadas (TEXT), fechas con formato |
| Equipos | ✅ OK | DTO con torneoId opcional implementado |
| Equipo-Torneo | ✅ OK | Endpoints CRUD completos |
| Jugadores | ✅ OK | Endpoints básicos |
| Jugador-Equipo-Torneo | ✅ OK | Vinculación funcionando |
| Históricos | ✅ OK | DTOs implementados, sin ciclos JSON |
| CORS | ✅ OK | Configuración global para localhost:5173 |

**Próximas verificaciones pendientes:**
- [ ] Test de integración frontend-backend
- [ ] Validación de payloads desde React
- [ ] Verificación de manejo de errores en UI

---

**Documento mantenido por:** Cascade AI  
**Revisión requerida por:** Frontend Team
