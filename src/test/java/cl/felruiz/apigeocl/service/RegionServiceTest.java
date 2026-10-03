package cl.felruiz.apigeocl.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.dto.RegionDTO;
import cl.felruiz.apigeocl.exception.ResourceNotFoundException;
import cl.felruiz.apigeocl.mapper.ComunaMapper;
import cl.felruiz.apigeocl.mapper.RegionMapper;
import cl.felruiz.apigeocl.model.Comuna;
import cl.felruiz.apigeocl.model.Region;
import cl.felruiz.apigeocl.repository.ComunaRepository;
import cl.felruiz.apigeocl.repository.RegionRepository;

/**
 * Tests unitarios para RegionService.
 */

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

  @Mock
  private RegionRepository regionRepository;

  @Mock
  private ComunaRepository comunaRepository;

  @Spy
  private RegionMapper regionMapper;

  @Spy
  private ComunaMapper comunaMapper;

  @InjectMocks
  private RegionService regionService;

  @Test
  @DisplayName("obtenerTodas debe retornar lista de RegionDTO")
  void obtenerTodas_conRegiones_retornaListaDTO() {
    // Arrange
    List<Region> regiones = List.of(
        Region.builder().id(1L).numero("VIII").nombre("Biobío").capital("Concepción").build(),
        Region.builder().id(2L).numero("XIII").nombre("Metropolitana de Santiago").capital("Santiago").build());
    when(regionRepository.findAll(Sort.by("id"))).thenReturn(regiones);

    // Act
    List<RegionDTO> resultado = regionService.obtenerTodas();

    // Assert
    assertThat(resultado).hasSize(2);
    assertThat(resultado.get(0).getNombre()).isEqualTo("Biobío");
    assertThat(resultado.get(1).getNombre()).isEqualTo("Metropolitana de Santiago");
  }

  @Test
  @DisplayName("obtenerComunasPorRegion con ID válido retorna comunas")
  void obtenerComunasPorRegion_conIdValido_retornaComunas() {
    // Arrange
    Comuna comuna = Comuna.builder().id(219L).nombre("Concepción").codigoCut("08101").build();
    when(regionRepository.existsById(11L)).thenReturn(true);
    when(comunaRepository.findByProvinciaRegionIdOrderByNombreAsc(11L)).thenReturn(List.of(comuna));

    // Act
    List<ComunaDTO> resultado = regionService.obtenerComunasPorRegion(11L);

    // Assert
    assertThat(resultado).hasSize(1);
    assertThat(resultado.get(0).getNombre()).isEqualTo("Concepción");
    assertThat(resultado.get(0).getCodigoCut()).isEqualTo("08101");
  }

  @Test
  @DisplayName("obtenerComunasPorRegion con ID inexistente lanza excepción")
  void obtenerComunasPorRegion_conIdInexistente_lanzaExcepcion() {
    when(regionRepository.existsById(999L)).thenReturn(false);

    assertThatThrownBy(() -> regionService.obtenerComunasPorRegion(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("999");
    verifyNoInteractions(comunaRepository);
  }
}
