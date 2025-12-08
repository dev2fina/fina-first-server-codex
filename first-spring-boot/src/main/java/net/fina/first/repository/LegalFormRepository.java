package net.fina.first.repository;

import net.fina.first.model.LegalForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LegalForm entity operations.
 */
@Repository
public interface LegalFormRepository extends JpaRepository<LegalForm, Long> {

    Optional<LegalForm> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT l FROM LegalForm l WHERE l.active = true ORDER BY l.sequence")
    List<LegalForm> findAllActive();

    @Query("SELECT l FROM LegalForm l ORDER BY l.sequence ASC")
    List<LegalForm> findAllOrderBySortOrderAsc();

    @Query("SELECT l FROM LegalForm l WHERE l.active = true ORDER BY l.sequence ASC")
    List<LegalForm> findActiveOrderBySortOrderAsc();
}
