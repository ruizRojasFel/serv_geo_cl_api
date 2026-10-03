package cl.felruiz.apigeocl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para exponer datos de una Comuna.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComunaDTO {
  private Long id;
  private String nombre;
  private String codigoCut;
}
