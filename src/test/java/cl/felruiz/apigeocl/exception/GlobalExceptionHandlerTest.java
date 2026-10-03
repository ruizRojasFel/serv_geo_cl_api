package cl.felruiz.apigeocl.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cl.felruiz.apigeocl.dto.ErrorDTO;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("handleNotFound debe retornar 404 con mensaje")
  void handleNotFound_retorna404() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Región con id 999 no encontrada");

    ResponseEntity<ErrorDTO> response = handler.handleNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    assertThat(response.getBody())
        .isNotNull()
        .satisfies(body -> {
          assertThat(body.getStatus()).isEqualTo(404);
          assertThat(body.getMensaje()).contains("999");
          assertThat(body.getTimestamp()).isNotNull();
        });
  }

  @Test
  @DisplayName("handleNoResource debe retornar 404 con la ruta")
  void handleNoResource_retorna404() {
    NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "api/v1/xyz");

    ResponseEntity<ErrorDTO> response = handler.handleNoResource(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    assertThat(response.getBody())
        .isNotNull()
        .satisfies(body -> {
          assertThat(body.getStatus()).isEqualTo(404);
          assertThat(body.getMensaje()).contains("/api/v1/xyz");
        });
  }

  @Test
  @DisplayName("handleTypeMismatch debe retornar 400 con nombre del parámetro")
  void handleTypeMismatch_retorna400() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("abc", Long.class, "id", null, null);

    ResponseEntity<ErrorDTO> response = handler.handleTypeMismatch(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(response.getBody())
        .isNotNull()
        .satisfies(body -> {
          assertThat(body.getStatus()).isEqualTo(400);
          assertThat(body.getMensaje()).contains("id").contains("abc");
        });
  }

  @Test
  @DisplayName("handleGeneral debe retornar 500")
  void handleGeneral_retorna500() {
    Exception ex = new RuntimeException("Error inesperado");

    ResponseEntity<ErrorDTO> response = handler.handleGeneral(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    assertThat(response.getBody())
        .isNotNull()
        .satisfies(body -> {
          assertThat(body.getStatus()).isEqualTo(500);
          assertThat(body.getError()).isEqualTo("Internal Server Error");
        });
  }
}
