package cl.felruiz.apigeocl;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que la documentación OpenAPI se genera y la exporta a
 * target/swagger/openapi.json, que el CI publica en GitHub Pages junto a
 * Swagger UI.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocsExportTest {

  private static final Path OUTPUT = Path.of("target", "swagger", "openapi.json");

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Debe generar la especificación OpenAPI y exportarla")
  void apiDocs_generaEspecificacion_exportaJson() throws Exception {
    String json = mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    assertThat(json).contains("\"openapi\"", "/api/v1/");

    Files.createDirectories(OUTPUT.getParent());
    Files.writeString(OUTPUT, json);
  }
}
