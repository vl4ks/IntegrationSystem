package org.denisova.integrationapp.repo;

import org.denisova.integrationapp.domain.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    Optional<SparePart> findBySpareCode(String spareCode);
}
