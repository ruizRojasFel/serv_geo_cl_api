package cl.felruiz.apigeocl.controller;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.dto.RegionDTO;
import cl.felruiz.apigeocl.exception.ResourceNotFoundException;
import cl.felruiz.apigeocl.service.RegionService;

/**
 * Integration Tests para RegionController.
 */

@WebMvcTest(RegionController.class)
class RegionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RegionService regionService;

  @Test
  @DisplayName("GET /api/v1/regiones debe retornar 200 con lista de regiones")
  void obtenerTodas_retorna200ConLista() throws Exception {
    // Arrange
    List<RegionDTO> regiones = List.of(
        RegionDTO.builder().id(1L).numero("VIII").nombre("Biobío").capital("Concepción").build(),
        RegionDTO.builder().id(2L).numero("XIII").nombre("Metropolitana de Santiago").capital("Santiago").build());
    when(regionService.obtenerTodas()).thenReturn(regiones);

    // Act & Assert
    mockMvc.perform(get("/api/v1/regiones"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].nombre", is("Biobío")))
        .andExpect(jsonPath("$[1].nombre", is("Metropolitana de Santiago")));
  }

  @Test
  @DisplayName("GET /api/v1/regiones/{id}/comunas debe retornar 200 con comunas")
  void obtenerComunas_retorna200ConLista() throws Exception {
    // Arrange
    List<ComunaDTO> comunas = List.of(
        ComunaDTO.builder().id(219L).nombre("Concepción").codigoCut("08101").build());
    when(regionService.obtenerComunasPorRegion(11L)).thenReturn(comunas);

    // Act & Assert
    mockMvc.perform(get("/api/v1/regiones/11/comunas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].nombre", is("Concepción")))
        .andExpect(jsonPath("$[0].codigoCut", is("08101")));
  }

  @Test
  @DisplayName("GET /api/v1/regiones/{id}/comunas con ID inexistente debe retornar 404")
  void obtenerComunas_conIdInexistente_retorna404() throws Exception {
    // Arrange
    when(regionService.obtenerComunasPorRegion(999L))
        .thenThrow(new ResourceNotFoundException("Región con id 999 no encontrada"));

    // Act & Assert
    mockMvc.perform(get("/api/v1/regiones/999/comunas"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.mensaje", containsString("999")));
  }

  @Test
  @DisplayName("GET /api/v1/regiones/{id}/comunas con ID no numérico debe retornar 400")
  void obtenerComunas_conIdInvalido_retorna400() throws Exception {
    mockMvc.perform(get("/api/v1/regiones/abc/comunas"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)));
  }
}
