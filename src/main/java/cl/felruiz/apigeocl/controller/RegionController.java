package cl.felruiz.apigeocl.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.dto.RegionDTO;
import cl.felruiz.apigeocl.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para el recurso Región.
 */

@RestController
@RequestMapping("/api/v1/regiones")
@RequiredArgsConstructor
@Tag(name = "Regiones", description = "Endpoints para consultar regiones de Chile y sus comunas")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "Listar todas las regiones")
    public ResponseEntity<List<RegionDTO>> obtenerTodas() {
        return ResponseEntity.ok(regionService.obtenerTodas());
    }

    @GetMapping("/{id}/comunas")
    @Operation(summary = "Listar comunas de una región")
    public ResponseEntity<List<ComunaDTO>> obtenerComunas(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.obtenerComunasPorRegion(id));
    }
}
