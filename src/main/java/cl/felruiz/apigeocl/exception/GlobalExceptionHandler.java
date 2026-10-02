package cl.felruiz.apigeocl.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cl.felruiz.apigeocl.dto.ErrorDTO;

/**
 * Manejador global de excepciones.
 *
 * @RestControllerAdvice → intercepta excepciones de TODOS
 * los controllers y devuelve respuestas JSON personalizadas
 * en lugar de la "Whitelabel Error Page" por defecto de Spring.
 *
 * Sin esto: try-catch repetido en cada controller.
 * Con esto: el manejo de errores está centralizado aquí.
 *
 * Orden de los handlers:
 *   1. handleNotFound      → 404 (recurso no encontrado)
 *   2. handleNoResource    → 404 (ruta inexistente)
 *   3. handleMissingParam  → 400 (parámetro requerido faltante)
 *   4. handleGeneral       → 500 (cualquier otro error no controlado)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Captura ResourceNotFoundException → devuelve HTTP 404.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleNotFound(ResourceNotFoundException ex) {
    ErrorDTO error = ErrorDTO.builder()
      .status(HttpStatus.NOT_FOUND.value())
      .error("Not Found")
      .mensaje(ex.getMessage())
      .timestamp(LocalDateTime.now())
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Captura rutas que no existen (ej. "/" o "/api/v1/xyz") → devuelve HTTP 404.
   * Sin esto, handleGeneral las convertiría en 500.
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorDTO> handleNoResource(NoResourceFoundException ex) {
    ErrorDTO error = ErrorDTO.builder()
      .status(HttpStatus.NOT_FOUND.value())
      .error("Not Found")
      .mensaje("Ruta '/" + ex.getResourcePath() + "' no encontrada")
      .timestamp(LocalDateTime.now())
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Captura parámetros requeridos faltantes → devuelve HTTP 400.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorDTO> handleMissingParam(MissingServletRequestParameterException ex) {
    ErrorDTO error = ErrorDTO.builder()
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .mensaje("Parámetro requerido '" + ex.getParameterName() + "' no encontrado")
      .timestamp(LocalDateTime.now())
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Captura cualquier excepción no controlada → devuelve HTTP 500.
   * Este SIEMPRE debe ser el último handler.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> handleGeneral(Exception ex) {
    ErrorDTO error = ErrorDTO.builder()
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .error("Internal Server Error")
      .mensaje("Ocurrió un error inesperado")
      .timestamp(LocalDateTime.now())
      .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}