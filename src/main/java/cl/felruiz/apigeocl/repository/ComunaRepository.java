package cl.felruiz.apigeocl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.felruiz.apigeocl.model.Comuna;

/**
 * Repository para la entidad Comuna.
 */

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {

  /** Comunas de una región (comuna → provincia → región), ordenadas por nombre. */
  List<Comuna> findByProvinciaRegionIdOrderByNombreAsc(Long regionId);
}
