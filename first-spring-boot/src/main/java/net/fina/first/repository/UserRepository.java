package net.fina.first.repository;

import net.fina.first.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r.id = :roleId")
    boolean existsByRolesId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.blocked = false AND u.disabled = false AND u.deleted = false")
    Page<User> findAllEnabled(Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.login) = LOWER(:login) AND u.deleted = false")
    Optional<User> findByLoginIgnoreCase(@Param("login") String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.login = :login AND u.deleted = false")
    Optional<User> findByLoginWithRoles(@Param("login") String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.login = :login AND u.deleted = false")
    Optional<User> findByLoginWithRolesAndPermissions(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.login) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginDate = :lastLoginDate WHERE u.id = :userId")
    void updateLastLoginDate(@Param("userId") Long userId, @Param("lastLoginDate") LocalDateTime lastLoginDate);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") int attempts);

    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil WHERE u.id = :userId")
    void updateLockedUntil(@Param("userId") Long userId, @Param("lockedUntil") LocalDateTime lockedUntil);
}
