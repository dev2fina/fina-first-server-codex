package net.fina.first.repository;

import net.fina.first.model.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Administrator entity operations.
 */
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long>, JpaSpecificationExecutor<Administrator> {

    List<Administrator> findByFiRegistryId(Long fiRegistryId);

    Page<Administrator> findByFiRegistryId(Long fiRegistryId, Pageable pageable);

    @Query("SELECT a FROM Administrator a WHERE a.fiRegistry.id = :fiRegistryId AND a.deleted = false")
    List<Administrator> findActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT a FROM Administrator a WHERE a.fiRegistry.id = :fiRegistryId AND a.active = true AND a.deleted = false")
    List<Administrator> findCurrentByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    Optional<Administrator> findByFiRegistryIdAndIdentificationNumber(Long fiRegistryId, String identificationNumber);

    @Query("SELECT a FROM Administrator a WHERE a.fiRegistry.id = :fiRegistryId AND a.position = :position AND a.deleted = false")
    List<Administrator> findByFiRegistryIdAndPosition(
            @Param("fiRegistryId") Long fiRegistryId,
            @Param("position") String position);

    @Query("SELECT COUNT(a) FROM Administrator a WHERE a.fiRegistry.id = :fiRegistryId AND a.deleted = false")
    long countByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    @Query("SELECT COUNT(a) FROM Administrator a WHERE a.fiRegistry.id = :fiRegistryId AND a.active = true AND a.deleted = false")
    long countActiveByFiRegistryId(@Param("fiRegistryId") Long fiRegistryId);

    boolean existsByFiRegistryIdAndIdentificationNumber(Long fiRegistryId, String identificationNumber);
}
