-- =========================================================
-- ARCHIVO DE SEMILLA (SEED) PARA GOLSYSTEM V2
-- =========================================================

-- LIMPIEZA PREVIA PARA EVITAR ERRORES DE DUPLICADOS (Duplicate entry)
-- Esto reinicia las tablas para que el script siempre corra limpio
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE jugador_equipo_torneo;
TRUNCATE TABLE equipo_torneo;
TRUNCATE TABLE torneo;
TRUNCATE TABLE equipo;
TRUNCATE TABLE jugador;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. CREACIÓN DEL CORAZÓN DE LA APP (Registro Global)
-- =========================================================

-- Jugadores Globales (Identificados por documentoIdentidad)
INSERT INTO jugador (documento_identidad, nombre, apellido, fecha_nacimiento, activo, created_at, updated_at) VALUES
('11111111', 'Falcao', 'García', '1986-02-10', 1, NOW(), NOW()),
('22222222', 'James', 'Rodríguez', '1991-07-12', 1, NOW(), NOW()),
('33333333', 'Luis', 'Díaz', '1997-01-13', 1, NOW(), NOW()),
('44444444', 'David', 'Ospina', '1988-08-31', 1, NOW(), NOW()),
('55555555', 'Juan Fernando', 'Quintero', '1993-01-18', 1, NOW(), NOW()),
('66666666', 'Linda', 'Caicedo', '2005-02-22', 1, NOW(), NOW()),
('77777777', 'Carlos', 'Valderrama', '1961-09-02', 1, NOW(), NOW()),
('88888888', 'Freddy', 'Rincón', '1966-08-14', 1, NOW(), NOW());

-- Equipos Globales (Identificados por codigoEquipo auto/manual)
INSERT INTO equipo (codigo_equipo, nombre, logo_url, activo, created_at, updated_at) VALUES
('NAL-01', 'Atlético Nacional', 'https://upload.wikimedia.org/wikipedia/commons/2/2b/Escudo_de_Atl%C3%A9tico_Nacional.png', 1, NOW(), NOW()),
('MIL-02', 'Millonarios', 'https://upload.wikimedia.org/wikipedia/commons/e/ea/Logo_Millonarios_FC.png', 1, NOW(), NOW()),
('AME-03', 'América de Cali', 'https://upload.wikimedia.org/wikipedia/commons/f/fb/America_de_Cali.svg', 1, NOW(), NOW()),
('SFE-04', 'Santa Fe', 'https://upload.wikimedia.org/wikipedia/commons/3/36/Escudo_de_Independiente_Santa_Fe.png', 1, NOW(), NOW());


-- 2. CREACIÓN DE MUNDOS INDEPENDIENTES (Torneos)
-- =========================================================

-- Torneo 1: Liga Betplay Dimayor V2 (MASCULINO)
INSERT INTO torneo (nombre, categoria, descripcion, cantidad_grupos, estado, min_jugadores, max_jugadores, created_at, updated_at) VALUES
('Liga Betplay Clausura', 'MASCULINO', 'El torneo de la máxima categoría de Colombia', 1, 'CONFIGURACION', 1, 11, NOW(), NOW());

-- Torneo 2: Torneo Relámpago Empresarial (MIXTO)
INSERT INTO torneo (nombre, categoria, descripcion, cantidad_grupos, estado, min_jugadores, max_jugadores, created_at, updated_at) VALUES
('Torneo Relámpago Nocturno', 'MIXTO', 'Torneo rápido de empresas viernes por la noche', 1, 'CONFIGURACION', 1, 11, NOW(), NOW());


-- 3. INTERSECCIÓN: INSCRIPCIÓN DE EQUIPOS A TORNEOS
-- =========================================================
-- IMPORTANTE: equipo_torneo NO tiene columna updated_at en Java

INSERT INTO equipo_torneo (equipo_id, torneo_id, eliminado, created_at) VALUES
(1, 1, 0, NOW()), -- 1: Nacional a Liga Betplay
(2, 1, 0, NOW()), -- 2: Millonarios a Liga Betplay
(3, 1, 0, NOW()); -- 3: América a Liga Betplay

INSERT INTO equipo_torneo (equipo_id, torneo_id, eliminado, created_at) VALUES
(2, 2, 0, NOW()), -- 4: Millonarios a Relámpago
(4, 2, 0, NOW()); -- 5: Santa Fe a Relámpago

-- 4. INTERSECCIÓN: JUGADORES A EQUIPOS DENTRO DEL TORNEO 1
-- =========================================================
-- IMPORTANTE: jugador_equipo_torneo NO tiene columna updated_at en Java
-- (ID equipo_torneo: 1=Nacional, 2=Millonarios, 3=America)
INSERT INTO jugador_equipo_torneo (jugador_id, equipo_torneo_id, numero_camiseta, activo, created_at) VALUES
(1, 1, 9, 1, NOW()),  -- Falcao en Nacional (Liga)
(4, 1, 1, 1, NOW()),  -- Ospina en Nacional (Liga)
(2, 2, 10, 1, NOW()), -- James en Millonarios (Liga)
(5, 2, 8, 1, NOW()),  -- Quintero en Millonarios (Liga)
(3, 3, 7, 1, NOW());  -- Luis Díaz en América (Liga)

-- 5. INTERSECCIÓN: JUGADORES A EQUIPOS DENTRO DEL TORNEO 2
-- =========================================================
-- (ID equipo_torneo: 4=Millonarios Relámpago, 5=Santa Fe Relámpago)
INSERT INTO jugador_equipo_torneo (jugador_id, equipo_torneo_id, numero_camiseta, activo, created_at) VALUES
(1, 4, 99, 1, NOW()), -- Falcao en Millonarios (Relámpago)
(2, 5, 10, 1, NOW()), -- James en Santa Fe (Relámpago)
(6, 4, 11, 1, NOW()), -- Linda Caicedo en Millonarios (Relámpago)
(7, 5, 10, 1, NOW()); -- Valderrama en Santa Fe (Relámpago)

-- =========================================================
-- FIN DEL SCRIPT
-- =========================================================
