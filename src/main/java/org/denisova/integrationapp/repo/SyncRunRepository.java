package org.denisova.integrationapp.repo;

import org.denisova.integrationapp.domain.SyncRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncRunRepository extends JpaRepository<SyncRun, Long> {
}
