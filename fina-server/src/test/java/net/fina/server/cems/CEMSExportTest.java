package net.fina.server.cems;

import net.fina.common.shared.LanguageSampleModel;
import net.fina.server.BaseTest;
import net.fina.server.cems.entity.*;
import net.fina.server.cems.entity.sanction.CEMSSanctionStatus;
import net.fina.server.cems.util.CEMSExportUtil;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.entity.User;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CEMSExportTest extends BaseTest {

    @Test
    public void testExportInspections() throws Exception {
        List<CEMSInspection> inspections = new ArrayList<>();
        CEMSInspection inspection = createInspection();

        inspections.add(inspection);
        inspections.add(inspection);

        String outFolderPath = "./target/CEMS/";

        File outFile = new File(outFolderPath);
        if (!outFile.exists()) {
            outFile.mkdir();
        }

        String templateFilePath = outFolderPath + "inspectionTemplateFIle.xlsx";

        Workbook template = WorkbookFactory.create(true);
        template.createSheet("master");
        template.createSheet("details");
        template.write(Files.newOutputStream(Paths.get(templateFilePath)));
        template.close();

        byte[] data = CEMSExportUtil.exportInspections(inspections, templateFilePath, new LanguageSampleModel(1, "en_US"));

        String fileName = outFolderPath + "inspectionExport.xlsx";

        Files.write(new File(fileName).toPath(), data);
    }

    @Test
    public void testExportRecommendations() throws Exception {
        List<CEMSRecommendation> recommendations = new ArrayList<>();

        recommendations.add(createRecommendation(CEMSRecommendationType.DECISION));
        recommendations.add(createRecommendation(CEMSRecommendationType.DECISION));
        recommendations.add(createRecommendation(CEMSRecommendationType.DECISION));
        recommendations.add(createRecommendation(CEMSRecommendationType.RECOMMENDATION));
        recommendations.add(createRecommendation(CEMSRecommendationType.RECOMMENDATION));

        String outFolderPath = "./target/CEMS/";

        File outFile = new File(outFolderPath);
        if (!outFile.exists()) {
            outFile.mkdir();
        }

        String templateFilePath = outFolderPath + "recommendationTemplateFIle.xlsx";

        Workbook template = WorkbookFactory.create(true);
        template.createSheet("master");
        template.createSheet("details");
        template.write(Files.newOutputStream(Paths.get(templateFilePath)));
        template.close();

        byte[] data = CEMSExportUtil.exportRecommendations(recommendations, templateFilePath, new LanguageSampleModel(1, "en_US"));

        String fileName = outFolderPath + "recommendationExport.xlsx";

        Files.write(new File(fileName).toPath(), data);
    }


    private CEMSInspection createInspection() {
        CEMSInspection inspection = new CEMSInspection();
        inspection.setInfo("Info");
        inspection.setFoundation("Foundation");
        inspection.setFi(new Fi(1, "BNK", new Description(1, 1, "GELA BANKI")));
        inspection.setType(CEMSInspectionType.TARGETED_VERIFICATION);
        User manager = new User();
        manager.setDescription(new Description(1, 1, "Fridon Injia"));
        inspection.setManager(manager);
        inspection.setManagerPosition("Durgali");
        inspection.setStartDate(new Date());
        inspection.setEndDate(new Date());

        return inspection;
    }

    private CEMSRecommendation createRecommendation(CEMSRecommendationType type) {

        CEMSRecommendation recommendation = new CEMSRecommendation();
        recommendation.setType(type);
        recommendation.setInspection(createInspection());
        recommendation.setStatusInfo(new CEMSRecommendationStatusInfo("note", "Action", new CEMSSanctionStatus("IN_PROGRESS", new Description(1,1,"In_Progress"))));
        recommendation.setCreationDate(new Date());
        recommendation.setNumber(11);
        recommendation.setLetterInfo("sad");
        recommendation.setLetterDate(new Date());
        List<CEMSResponsiblePerson> responsiblePersonList = new ArrayList<>();
        responsiblePersonList.add(new CEMSResponsiblePerson(1, "Givi Sixarulidze", "CEO"));
        responsiblePersonList.add(new CEMSResponsiblePerson(1, "Gela Charkviani", "Prezidentis Mrcheveli"));
        recommendation.setFiResponsiblePersons(responsiblePersonList);

        return recommendation;
    }


}
