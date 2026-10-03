package cl.felruiz.apigeocl.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.model.Comuna;

/**
 * Tests unitarios para ComunaMapper.
 */

class ComunaMapperTest {

  private final ComunaMapper mapper = new ComunaMapper();

  @Test
  @DisplayName("Debe convertir Comuna a ComunaDTO")
  void toDTO_conComunaValida_retornaDTO() {
    // Arrange
    Comuna comuna = Comuna.builder()
        .id(219L)
        .nombre("Concepción")
        .codigoCut("08101")
        .build();

    // Act
    ComunaDTO dto = mapper.toDTO(comuna);

    // Assert
    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(219L);
    assertThat(dto.getNombre()).isEqualTo("Concepción");
    assertThat(dto.getCodigoCut()).isEqualTo("08101");
  }

  @Test
  @DisplayName("Debe retornar null cuando Comuna es null")
  void toDTO_conNull_retornaNull() {
    assertThat(mapper.toDTO(null)).isNull();
  }
}
