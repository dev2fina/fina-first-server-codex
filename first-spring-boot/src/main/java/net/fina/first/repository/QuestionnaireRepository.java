package net.fina.first.repository;

import net.fina.first.model.FiType;
import net.fina.first.model.Questionnaire;
import net.fina.first.model.enums.FiTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Questionnaire entity operations.
 */
@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    Optional<Questionnaire> findByCode(String code);

    @Query("SELECT q FROM Questionnaire q WHERE q.active = true ORDER BY q.groupCode, q.sequence")
    List<Questionnaire> findAllActive();

    @Query("SELECT q FROM Questionnaire q WHERE q.fiType.code = :fiTypeCode AND q.active = true ORDER BY q.groupCode, q.sequence")
    List<Questionnaire> findByFiTypeCode(@Param("fiTypeCode") FiTypeCode fiTypeCode);

    @Query("SELECT q FROM Questionnaire q WHERE q.groupCode = :groupCode AND q.active = true ORDER BY q.sequence")
    List<Questionnaire> findByGroupCode(@Param("groupCode") String groupCode);

    @Query("SELECT q FROM Questionnaire q WHERE q.obligatory = true AND q.active = true ORDER BY q.groupCode, q.sequence")
    List<Questionnaire> findObligatoryQuestions();

    @Query("SELECT DISTINCT q.groupCode FROM Questionnaire q WHERE q.active = true ORDER BY q.groupCode")
    List<String> findAllGroups();

    @Query("SELECT q FROM Questionnaire q WHERE q.fiType = :fiType AND q.active = true ORDER BY q.groupCode, q.sequence")
    List<Questionnaire> findByFiTypeAndActiveTrue(FiType fiType);
}
