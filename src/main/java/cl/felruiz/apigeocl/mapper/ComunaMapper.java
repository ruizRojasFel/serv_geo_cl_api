package cl.felruiz.apigeocl.mapper;

import org.springframework.stereotype.Component;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.model.Comuna;

/**
 * Mapper para convertir entre Comuna (entidad) y ComunaDTO.
 */
@Component
public class ComunaMapper {

  public ComunaDTO toDTO(Comuna comuna) {
    if (comuna == null) return null;

    return ComunaDTO.builder()
      .id(comuna.getId())
      .nombre(comuna.getNombre())
      .codigoCut(comuna.getCodigoCut())
      .build();
  }
}
