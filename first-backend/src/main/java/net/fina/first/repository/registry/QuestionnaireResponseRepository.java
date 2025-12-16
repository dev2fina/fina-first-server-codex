package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.QuestionnaireResponse;

public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, Long> {
    List<QuestionnaireResponse> findByFinancialInstitutionId(Long fiId);
}
