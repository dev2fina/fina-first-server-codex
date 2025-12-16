package net.fina.first.repository.registry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.fina.first.model.registry.GapDetail;

public interface GapDetailRepository extends JpaRepository<GapDetail, Long> {
    List<GapDetail> findByFinancialInstitutionId(Long fiId);
}
