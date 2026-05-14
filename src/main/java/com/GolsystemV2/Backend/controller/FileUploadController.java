package com.GolsystemV2.Backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para subida de archivos (logos de equipos, fotos de jugadores)
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @Value("${upload.base-url:http://localhost:8080/uploads/}")
    private String baseUrl;

    /**
     * Sube una imagen de logo para equipo
     */
    @PostMapping("/logo")
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "logos");
    }

    /**
     * Sube una foto de jugador
     */
    @PostMapping("/foto-jugador")
    public ResponseEntity<?> uploadFotoJugador(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "fotos-jugadores");
    }

    /**
     * Sube una imagen de escudo/torneo
     */
    @PostMapping("/escudo")
    public ResponseEntity<?> uploadEscudo(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "escudos");
    }

    /**
     * Método genérico para subir archivos
     */
    private ResponseEntity<?> uploadFile(MultipartFile file, String subcarpeta) {
        try {
            // Validar que no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "El archivo está vacío"
                ));
            }

            // Validar tipo de archivo (solo imágenes)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Solo se permiten archivos de imagen"
                ));
            }

            // Crear directorio si no existe
            Path directorio = Paths.get(uploadPath, subcarpeta);
            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }

            // Generar nombre único para el archivo
            String extension = obtenerExtension(file.getOriginalFilename());
            String nombreArchivo = UUID.randomUUID().toString() + extension;

            // Guardar archivo
            Path rutaArchivo = directorio.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Generar URL pública
            String urlArchivo = baseUrl + subcarpeta + "/" + nombreArchivo;

            return ResponseEntity.ok(Map.of(
                "mensaje", "Archivo subido exitosamente",
                "url", urlArchivo,
                "nombreArchivo", nombreArchivo,
                "tamanio", file.getSize()
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error al subir el archivo: " + e.getMessage()
            ));
        }
    }

    /**
     * Obtiene la extensión del archivo
     */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".jpg";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
}
