package net.fina.first.service.registry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import net.fina.first.dto.registry.QuestionnaireResponseDto;
import net.fina.first.exception.registry.MissingDataException;
import net.fina.first.model.registry.FinancialInstitution;
import net.fina.first.model.registry.GapDetail;
import net.fina.first.model.registry.QuestionAnswer;
import net.fina.first.repository.registry.GapDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceTest {

    @Mock
    private GapDetailRepository gapDetailRepository;

    @InjectMocks
    private QuestionnaireService questionnaireService;

    private QuestionnaireResponseDto obligatoryYes;
    private QuestionnaireResponseDto obligatoryMissing;

    @BeforeEach
    void setUp() {
        obligatoryYes = new QuestionnaireResponseDto();
        obligatoryYes.setObligatory(true);
        obligatoryYes.setAnswer(QuestionAnswer.YES);

        obligatoryMissing = new QuestionnaireResponseDto();
        obligatoryMissing.setObligatory(true);
        obligatoryMissing.setAnswer(QuestionAnswer.NOT_APPLICABLE);
    }

    @Test
    void validateObligatoryThrowsWhenMissing() {
        assertThrows(MissingDataException.class, () -> questionnaireService.validateObligatory(List.of(obligatoryMissing)));
    }

    @Test
    void validateObligatoryPassesWhenAnswered() {
        assertDoesNotThrow(() -> questionnaireService.validateObligatory(List.of(obligatoryYes)));
    }

    @Test
    void ensureGapsResolvedThrowsWhenOpenGaps() {
        FinancialInstitution fi = new FinancialInstitution();
        fi.setId(1L);
        GapDetail gapDetail = new GapDetail();
        gapDetail.setResolution(null);
        when(gapDetailRepository.findByFinancialInstitutionId(fi.getId())).thenReturn(List.of(gapDetail));
        assertThrows(MissingDataException.class, () -> questionnaireService.ensureGapsResolved(fi));
    }

    @Test
    void ensureGapsResolvedPassesWhenResolved() {
        FinancialInstitution fi = new FinancialInstitution();
        fi.setId(1L);
        GapDetail gapDetail = new GapDetail();
        gapDetail.setResolution("fixed");
        when(gapDetailRepository.findByFinancialInstitutionId(fi.getId())).thenReturn(List.of(gapDetail));
        assertDoesNotThrow(() -> questionnaireService.ensureGapsResolved(fi));
    }
}
