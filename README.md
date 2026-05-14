# GOLSYSTEM ⚽

![JAVA](https://img.shields.io/badge/JAVA-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![SPRING](https://img.shields.io/badge/SPRING-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![MYSQL](https://img.shields.io/badge/MYSQL-%23005C84.svg?style=for-the-badge&logo=mysql&logoColor=white) ![HIBERNATE](https://img.shields.io/badge/HIBERNATE-%2359666C.svg?style=for-the-badge&logo=hibernate&logoColor=white) ![BOOTSTRAP](https://img.shields.io/badge/BOOTSTRAP-%237952B3.svg?style=for-the-badge&logo=bootstrap&logoColor=white) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

🏟️ **Plataforma Profesional para la Sistematización de Torneos de Fútbol**

GOLSYSTEM es una solución de software empresarial diseñada para transformar radicalmente la gestión de campeonatos de fútbol. El ecosistema digitaliza el flujo completo de la competición: desde la inscripción de clubes hasta el cálculo automatizado de métricas de rendimiento y estadísticas de juego en tiempo real.

---

## 🚀 Funcionalidades de Alto Impacto

El sistema está construido sobre una lógica de negocio compleja que garantiza la integridad de la información deportiva:

*   **Gestión de Competiciones:** Configuración de múltiples formatos de torneo, fases eliminatorias y ligas.
*   **Administración de Clubes y Plantillas:** Módulo centralizado para el registro de delegados, jugadores y gestión de transferencias.
*   **Motor de Programación (Scheduling):** Automatización de calendarios de partidos, evitando conflictos de horarios y sedes.
*   **Cálculo Estadístico en Tiempo Real:** Algoritmo propietario para la actualización inmediata de tablas de posiciones (PG, PE, PP, GF, GC, DG, PTS).
*   **Monitorización de Encuentros:** Captura detallada de eventos de partido (goles, tarjetas y sustituciones).

---

## 🛠️ Stack Tecnológico y Arquitectura

Se ha implementado una arquitectura limpia y escalable que permite el crecimiento modular del sistema:

*   **Backend:** Java 17+ con **Spring Boot 3**, utilizando **Spring Data JPA** para la capa de persistencia y **Spring Security** para el control de acceso.
*   **Base de Datos:** **MySQL** con un diseño relacional normalizado para asegurar la consistencia de los datos.
*   **Frontend:** Interfaz dinámica desarrollada con **JavaScript**, **HTML5**, **CSS3** y **Bootstrap**, enfocada en la experiencia de usuario (UX).
*   **Infraestructura:** Preparado para despliegue en la nube mediante **AWS (Amazon Web Services)**, utilizando instancias EC2 para el hosting.

---

## 📁 Estructura del Dominio (Entidades)

El modelo de datos refleja una arquitectura orientada al dominio (DDD):
*   `Torneo`: Nodo principal de la competición.
*   `Equipo`: Participante vinculado a torneos y tablas.
*   `Partido`: Entidad de enlace que gestiona fechas, estados y resultados.
*   `TablaPosiciones`: Lógica de negocio para el procesamiento de estadísticas acumuladas.

---

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/JuanDavidHerrera2125/golsystem-backend.git](https://github.com/JuanDavidHerrera2125/golsystem-backend.git)
