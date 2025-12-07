package net.fina.first.repository;

import net.fina.first.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Region entity operations.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT r FROM Region r WHERE r.deleted = false ORDER BY r.sequence")
    List<Region> findAllActive();

    @Query("SELECT r FROM Region r WHERE r.parent IS NULL AND r.deleted = false ORDER BY r.sequence")
    List<Region> findRootRegions();

    @Query("SELECT r FROM Region r WHERE r.parent.id = :parentId AND r.deleted = false ORDER BY r.sequence")
    List<Region> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT r FROM Region r WHERE r.level = :level AND r.deleted = false ORDER BY r.sequence")
    List<Region> findByLevel(@Param("level") Integer level);

    @Query("SELECT r FROM Region r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) AND r.deleted = false")
    List<Region> findByNameContaining(@Param("name") String name);
}
