package net.fina.first.repository;

import net.fina.first.model.FiType;
import net.fina.first.model.enums.FiTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FiType entity operations.
 */
@Repository
public interface FiTypeRepository extends JpaRepository<FiType, Long> {

    Optional<FiType> findByCode(FiTypeCode code);

    boolean existsByCode(FiTypeCode code);

    @Query("SELECT f FROM FiType f WHERE f.active = true ORDER BY f.sequence")
    List<FiType> findAllActive();

    @Query("SELECT f FROM FiType f WHERE f.active = true AND f.hasBranches = true ORDER BY f.sequence")
    List<FiType> findAllWithBranchesEnabled();

    @Query("SELECT f FROM FiType f WHERE f.active = true AND f.hasLicenses = true ORDER BY f.sequence")
    List<FiType> findAllWithLicensesEnabled();

    @Query("SELECT f FROM FiType f WHERE :branchType MEMBER OF f.branchTypes")
    List<FiType> findByBranchType(@Param("branchType") String branchType);
}
