# GOLSYSTEM - Backend API ⚽

![JAVA](https://img.shields.io/badge/JAVA-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![SPRING](https://img.shields.io/badge/SPRING-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![MYSQL](https://img.shields.io/badge/MYSQL-%23005C84.svg?style=for-the-badge&logo=mysql&logoColor=white) ![HIBERNATE](https://img.shields.io/badge/HIBERNATE-%2359666C.svg?style=for-the-badge&logo=hibernate&logoColor=white) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![REST_API](https://img.shields.io/badge/REST_API-%23000000.svg?style=for-the-badge&logo=postman&logoColor=white)

🏟️ **Arquitectura de Gestión y Sistematización de Torneos de Fútbol**

**GOLSYSTEM Backend** es un potente motor de servicios REST desarrollado para automatizar la lógica compleja de torneos deportivos profesionales y locales. A diferencia de sistemas de registro básicos, este núcleo procesa activamente reglas de justicia deportiva, persistencia histórica y analítica de datos en tiempo real.

---

## 🔥 Módulos Críticos y Lógica de Negocio Avanzada

El sistema implementa algoritmos específicos para cubrir cada aspecto de la competición:

*   **📈 Tablas de Posiciones Dinámicas:** Algoritmo de actualización instantánea que recalcula puntos, goles (favor/contra/diferencia) y rendimiento porcentual tras cada reporte de partido de forma automática.
*   **⚖️ Sistema Disciplinario Automatizado:** Control exhaustivo de tarjetas (amarillas/rojas). Incluye condicionales lógicos para la **suspensión automática de jugadores** basada en acumulación de tarjetas o sanciones directas, inhabilitando su participación en el siguiente encuentro programado.
*   **🏆 Histórico de Campeones (Hall of Fame):** Módulo de persistencia temporal que mantiene el registro de gloria deportiva a través de las ediciones, permitiendo consultas de palmarés y trayectoria de clubes a lo largo del tiempo.
*   **🎯 Histórico de Goleadores (Pichichi):** Seguimiento detallado y acumulativo de anotaciones por jugador. El sistema genera tablas de artillería actualizadas por torneo y mantiene un registro histórico de máximos anotadores históricos.
*   **📅 Gestión de Encuentros y Sedes:** Motor de programación que vincula equipos, árbitros y locaciones, asegurando la integridad del calendario y evitando conflictos de programación.

---

## 🛠️ Stack Tecnológico

*   **Lenguaje:** Java 17+
*   **Framework:** Spring Boot 3.x (Spring Data JPA, Spring Security)
*   **Persistencia:** Hibernate / MySQL (Diseño relacional optimizado para estadísticas)
*   **Arquitectura:** Patrón MVC (Model-View-Controller)
*   **Infraestructura:** AWS (Amazon Web Services) para despliegue y base de datos cloud (EC2/RDS)

---

## 📁 Estructura del Dominio (Backend Core)

La arquitectura de datos está diseñada para asegurar la consistencia mediante las siguientes entidades principales:
*   **Torneo:** Gestión de temporadas, ediciones y parámetros de liga.
*   **Sancion:** Lógica de penalizaciones y control de suspensiones automáticas por tarjetas.
*   **EstadisticaJugador:** Registro detallado de goles y eventos individuales por cada partido.
*   **HistoricoCampeones:** Repositorio de ganadores y méritos deportivos persistentes en el tiempo.
*   **TablaPosiciones:** Procesamiento lógico de estadísticas acumuladas y rendimiento.

---

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/JuanDavidHerrera2125/golsystem-backend.git](https://github.com/JuanDavidHerrera2125/golsystem-backend.git)
