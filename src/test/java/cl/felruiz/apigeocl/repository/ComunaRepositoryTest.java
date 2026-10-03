package cl.felruiz.apigeocl.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import cl.felruiz.apigeocl.model.Comuna;
import cl.felruiz.apigeocl.model.Provincia;
import cl.felruiz.apigeocl.model.Region;

/**
 * Repository Tests para ComunaRepository.
 */

@DataJpaTest
@ActiveProfiles("test")
class ComunaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ComunaRepository comunaRepository;

  private Region biobio;
  private Region nuble;

  /**
   * @BeforeEach → se ejecuta antes de cada test.
   *             Inserta datos de prueba en H2.
   */

  @BeforeEach
  void setUp() {
    biobio = entityManager.persist(Region.builder()
        .numero("VIII").nombre("Biobío").capital("Concepción").build());
    nuble = entityManager.persist(Region.builder()
        .numero("XVI").nombre("Ñuble").capital("Chillán").build());

    Provincia concepcion = entityManager.persist(Provincia.builder()
        .nombre("Concepción").capital("Concepción").region(biobio).build());
    Provincia arauco = entityManager.persist(Provincia.builder()
        .nombre("Arauco").capital("Lebu").region(biobio).build());
    Provincia diguillin = entityManager.persist(Provincia.builder()
        .nombre("Diguillín").capital("Bulnes").region(nuble).build());

    entityManager.persist(Comuna.builder()
        .nombre("Talcahuano").codigoCut("08110").provincia(concepcion).build());
    entityManager.persist(Comuna.builder()
        .nombre("Concepción").codigoCut("08101").provincia(concepcion).build());
    entityManager.persist(Comuna.builder()
        .nombre("Lebu").codigoCut("08201").provincia(arauco).build());
    entityManager.persist(Comuna.builder()
        .nombre("Chillán").codigoCut("16101").provincia(diguillin).build());

    entityManager.flush();
  }

  @Test
  @DisplayName("findByProvinciaRegionIdOrderByNombreAsc retorna solo comunas de la región, ordenadas")
  void findByProvinciaRegionId_retornaComunasDeRegionOrdenadas() {
    List<Comuna> comunas = comunaRepository.findByProvinciaRegionIdOrderByNombreAsc(biobio.getId());

    assertThat(comunas)
        .extracting(Comuna::getNombre)
        .containsExactly("Concepción", "Lebu", "Talcahuano");
  }

  @Test
  @DisplayName("findByProvinciaRegionIdOrderByNombreAsc con región inexistente retorna vacío")
  void findByProvinciaRegionId_conRegionInexistente_retornaVacio() {
    assertThat(comunaRepository.findByProvinciaRegionIdOrderByNombreAsc(999L)).isEmpty();
  }
}
