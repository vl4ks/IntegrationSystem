package org.denisova.integrationapp.repo;

import org.denisova.integrationapp.domain.SparePartVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SparePartVersionRepository extends JpaRepository<SparePartVersion, Long> {
}
