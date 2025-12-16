package net.fina.first.service.registry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import net.fina.first.dto.registry.QuestionnaireResponseDto;
import net.fina.first.exception.registry.MissingDataException;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.GapDetail;
import net.fina.first.model.registry.QuestionAnswer;
import net.fina.first.model.registry.QuestionnaireResponse;
import net.fina.first.repository.registry.GapDetailRepository;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final GapDetailRepository gapDetailRepository;

    public void validateObligatory(List<QuestionnaireResponseDto> responses) {
        boolean hasMissing = responses.stream()
            .filter(QuestionnaireResponseDto::isObligatory)
            .anyMatch(r -> r.getAnswer() == null || r.getAnswer() == QuestionAnswer.NOT_APPLICABLE);
        if (hasMissing) {
            throw new MissingDataException("All obligatory questionnaire items must be answered");
        }
    }

    public void validateObligatory(List<QuestionnaireResponse> responses) {
        boolean hasMissing = responses.stream()
            .filter(QuestionnaireResponse::isObligatory)
            .anyMatch(r -> r.getAnswer() == null || r.getAnswer() == QuestionAnswer.NOT_APPLICABLE);
        if (hasMissing) {
            throw new MissingDataException("All obligatory questionnaire items must be answered");
        }
    }

    public void syncGaps(FinancialInstitution fi, Set<QuestionnaireResponse> responses) {
        gapDetailRepository.deleteAll(gapDetailRepository.findByFinancialInstitutionId(fi.getId()));
        List<GapDetail> gaps = responses.stream()
            .filter(r -> r.isObligatory() && r.getAnswer() == QuestionAnswer.NO)
            .map(r -> {
                GapDetail gapDetail = new GapDetail();
                gapDetail.setFinancialInstitution(fi);
                gapDetail.setQuestionReference(String.valueOf(r.getId()));
                gapDetail.setDescription("Obligatory question answered NO: " + r.getQuestion());
                return gapDetail;
            }).collect(Collectors.toList());
        gapDetailRepository.saveAll(gaps);
    }

    public void ensureGapsResolved(FinancialInstitution fi) {
        boolean hasOpenGaps = gapDetailRepository.findByFinancialInstitutionId(fi.getId()).stream()
            .anyMatch(g -> g.getResolution() == null || g.getResolution().isBlank());
        if (hasOpenGaps) {
            throw new MissingDataException("All questionnaire gaps must be resolved before submission");
        }
    }
}
