package cl.felruiz.apigeocl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.felruiz.apigeocl.model.Region;

/**
 * Repository para la entidad Region.
 */

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}
