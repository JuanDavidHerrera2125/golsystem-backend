package com.GolsystemV2.Backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	private static final Logger logger = LoggerFactory.getLogger(BackendApplication.class);

	public static void main(String[] args) {
		logger.info("[STARTUP] Iniciando GolsystemV2 Backend...");
		try {
			SpringApplication.run(BackendApplication.class, args);
			logger.info("[STARTUP] ✅ Aplicación iniciada exitosamente");
		} catch (Exception e) {
			logger.error("[STARTUP] ❌ Error al iniciar aplicación: {}", e.getMessage(), e);
			throw e;
		}
	}

}
