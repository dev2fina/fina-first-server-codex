package net.fina.first.repository;

import net.fina.first.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Permission entity operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT p FROM Permission p WHERE p.active = true ORDER BY p.module, p.name")
    List<Permission> findAllActive();

    @Query("SELECT p FROM Permission p WHERE p.module = :module AND p.active = true ORDER BY p.name")
    List<Permission> findByModule(@Param("module") String module);

    @Query("SELECT DISTINCT p.module FROM Permission p WHERE p.active = true ORDER BY p.module")
    List<String> findAllModules();
}
