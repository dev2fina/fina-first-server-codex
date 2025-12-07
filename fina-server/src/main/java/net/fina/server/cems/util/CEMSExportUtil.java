package net.fina.server.cems.util;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.cems.entity.CEMSInspection;
import net.fina.server.cems.entity.CEMSRecommendation;
import net.fina.server.cems.entity.CEMSRecommendationType;
import net.fina.server.cems.entity.sanction.*;
import org.apache.poi.ss.usermodel.*;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CEMSExportUtil {

    private static final Logger log = Logger.getLogger(CEMSInspection.class.getName());

    public static byte[] exportInspections(List<CEMSInspection> inspections, String templatePath, LanguageSampleModel language) {
        try (InputStream in = Files.newInputStream(Paths.get(templatePath)); ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            long langId = language.getId();
            String langCode = language.getCode();

            Workbook workbook = WorkbookFactory.create(in);

            Sheet masterSheet = workbook.getSheetAt(0);

            int startRowNum = readConfigNumericValue("CEMS.Templates.Inspection.MasterSheetStartRow");
            int startCell = readConfigNumericValue("CEMS.Templates.Inspection.MasterSheetStartColumn");

            int rowIndex = startRowNum - 1;
            int celIndex;
            Row startRow = masterSheet.getRow(rowIndex) == null ? masterSheet.createRow(rowIndex) : masterSheet.getRow(rowIndex);

            //master sheet data
            for (int i = 0; i < inspections.size(); i++) {
                CEMSInspection insp = inspections.get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : masterSheet.createRow(rowIndex);
                rowIndex++;
                celIndex = startCell;

                //Row Number
                createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - startRowNum + 1));

                String fi = insp.getFi().getDescription().getDescription(langId);
                String type = MessagesUtil.getString(insp.getType().getCode(), langCode);
                String foundation = insp.getFoundation();
                String manager = insp.getManager() != null ? insp.getManager().getDescription().getDescription(langId) : "";
                String managerPosition = insp.getManagerPosition();
                Date startDate = insp.getStartDate();
                Date planedEndDate = insp.getEndDate();
                String info = insp.getInfo();

                createCellWithData(row, celIndex++, startRow, workbook, fi);
                createCellWithData(row, celIndex++, startRow, workbook, type);
                createCellWithData(row, celIndex++, startRow, workbook, foundation);
                createCellWithData(row, celIndex++, startRow, workbook, manager);
                createCellWithData(row, celIndex++, startRow, workbook, managerPosition);
                createCellWithData(row, celIndex++, startRow, workbook, startDate);
                createCellWithData(row, celIndex++, startRow, workbook, planedEndDate);
                createCellWithData(row, celIndex++, startRow, workbook, info);

            }


            //Details sheet data
            startRowNum = readConfigNumericValue("CEMS.Templates.Inspection.DetailSheetStartRow");
            startCell = readConfigNumericValue("CEMS.Templates.Inspection.DetailSheetStartColumn");

            Sheet detailsSheet = workbook.getSheetAt(1);
            rowIndex = startRowNum - 1;
            startRow = detailsSheet.getRow(rowIndex) == null ? detailsSheet.createRow(rowIndex) : detailsSheet.getRow(rowIndex);

            for (int i = 0; i < inspections.size(); i++) {
                CEMSInspection insp = inspections.get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : detailsSheet.createRow(rowIndex);
                rowIndex++;
                celIndex = startCell;

                //Row Number
                createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - startRowNum + 1));

                String fi = insp.getFi().getDescription().getDescription(langId);
                String type = MessagesUtil.getString(insp.getType().getCode(), langCode);
                String purpose = insp.getPurpose();
                String foundation = insp.getFoundation();
                Date lastInspectionDate = insp.getLastInspectionDate();
                Date onGoing = insp.getOngoingInspectionDate();
                Date startDate = insp.getReportingYearInspection() != null ? insp.getReportingYearInspection().getStartDate() : null;
                Date endDate = insp.getReportingYearInspection() != null ? insp.getReportingYearInspection().getEndDate() : null;
                Date reportSendDate = insp.getMetaInfo() != null ? insp.getMetaInfo().getReportSentDate() : null;
                String manager = "";
                if (insp.getReportingYearInspection() != null && insp.getReportingYearInspection().getManager() != null) {
                    manager = insp.getReportingYearInspection().getManager().getDescription().getDescription(langId);
                }
                Date preliminaryDiscussionDate = insp.getMetaInfo() != null ? insp.getMetaInfo().getPreliminaryDiscussionDate() : null;
                Date finalDiscussionDate = insp.getMetaInfo() != null ? insp.getMetaInfo().getFinalDiscussionDate() : null;
                String problemDescription = insp.getMetaInfo() != null ? insp.getMetaInfo().getProblemDescription() : "";
                int numberOfOrders = insp.getMetaInfo() != null ? insp.getMetaInfo().getNumberOfOrders() : 0;
                int numberOfOrdersAML = insp.getMetaInfo() != null ? insp.getMetaInfo().getNumberOfOrdersAML() : 0;

                createCellWithData(row, celIndex++, startRow, workbook, fi);
                createCellWithData(row, celIndex++, startRow, workbook, type);
                createCellWithData(row, celIndex++, startRow, workbook, purpose);
                createCellWithData(row, celIndex++, startRow, workbook, foundation);
                createCellWithData(row, celIndex++, startRow, workbook, lastInspectionDate);
                createCellWithData(row, celIndex++, startRow, workbook, onGoing);
                createCellWithData(row, celIndex++, startRow, workbook, startDate);
                createCellWithData(row, celIndex++, startRow, workbook, endDate);
                createCellWithData(row, celIndex++, startRow, workbook, manager);
                createCellWithData(row, celIndex++, startRow, workbook, preliminaryDiscussionDate);
                createCellWithData(row, celIndex++, startRow, workbook, finalDiscussionDate);
                createCellWithData(row, celIndex++, startRow, workbook, reportSendDate);
                createCellWithData(row, celIndex++, startRow, workbook, problemDescription);
                createCellWithData(row, celIndex++, startRow, workbook, numberOfOrders);
                createCellWithData(row, celIndex++, startRow, workbook, numberOfOrdersAML);

            }

            workbook.write(bos);
            workbook.close();
            return bos.toByteArray();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return new byte[0];
    }

    public static byte[] exportRecommendations(List<CEMSRecommendation> data, String templatePath, LanguageSampleModel language) {

        try (InputStream in = Files.newInputStream(Paths.get(templatePath)); ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            long langId = language.getId();
            String langCode = language.getCode();

            Workbook workbook = WorkbookFactory.create(in);

            Sheet decisionSheet = workbook.getSheetAt(0);

            int startRowNum = readConfigNumericValue("CEMS.Templates.Recommendation.DecisionStartRow");
            int startCell = readConfigNumericValue("CEMS.Templates.Recommendation.DecisionStartColumn");

            int rowIndex = startRowNum - 1;
            int celIndex;
            Row startRow = decisionSheet.getRow(rowIndex) == null ? decisionSheet.createRow(rowIndex) : decisionSheet.getRow(rowIndex);

            List<CEMSRecommendation> decisions = data.stream().filter(r -> r.getType().equals(CEMSRecommendationType.DECISION)).collect(Collectors.toList());
            List<CEMSRecommendation> recommendations = data.stream().filter(r -> r.getType().equals(CEMSRecommendationType.RECOMMENDATION)).collect(Collectors.toList());

            //decisions sheet data
            for (int i = 0; i < decisions.size(); i++) {
                CEMSRecommendation decision = decisions.get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : decisionSheet.createRow(rowIndex);
                rowIndex++;
                celIndex = startCell;

                insertData(decision, row, celIndex, rowIndex, startRowNum, startRow, workbook, langId, langCode);

            }


            //recommendations sheet data
            startRowNum = readConfigNumericValue("CEMS.Templates.Recommendation.RecommendationStartRow");
            startCell = readConfigNumericValue("CEMS.Templates.Recommendation.RecommendationStartColumn");

            Sheet recommendationSheet = workbook.getSheetAt(1);
            rowIndex = startRowNum - 1;
            startRow = recommendationSheet.getRow(rowIndex) == null ? recommendationSheet.createRow(rowIndex) : recommendationSheet.getRow(rowIndex);

            for (int i = 0; i < recommendations.size(); i++) {
                CEMSRecommendation recommendation = recommendations.get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : recommendationSheet.createRow(rowIndex);
                rowIndex++;
                celIndex = startCell;

                insertData(recommendation, row, celIndex, rowIndex, startRowNum, startRow, workbook, langId, langCode);


            }

            workbook.write(bos);
            workbook.close();
            return bos.toByteArray();

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return new byte[0];
    }


    public static byte[] exportSanctions(List<CEMSSanction> sanctions, String templatePath, LanguageSampleModel language) {


        try (InputStream in = Files.newInputStream(Paths.get(templatePath)); ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            int sanctionSheetStartRowNum = readConfigNumericValue("CEMS.Templates.Sanction.SanctionSheetStartRow");
            int sanctionSheetStartCell = readConfigNumericValue("CEMS.Templates.Sanction.SanctionSheetStartColumn");
            int regulationSheetStartRowNum = readConfigNumericValue("CEMS.Templates.Sanction.RegulationSheetStartRow");
            int regulationSheetStartCell = readConfigNumericValue("CEMS.Templates.Sanction.RegulationSheetStartColumn");
            int employeeSheetStartRowNum = readConfigNumericValue("CEMS.Templates.Sanction.EmployeeSheetStartRow");
            int employeeSheetStartCell = readConfigNumericValue("CEMS.Templates.Sanction.EmployeeSheetStartColumn");

            String langCode = language.getCode();
            long langId = language.getId();

            Workbook workbook = WorkbookFactory.create(in);

            Sheet sanctionSheet = workbook.getSheetAt(0);
            Sheet regulationsSheet = workbook.getSheetAt(1);
            Sheet employeesSheet = workbook.getSheetAt(2);

            int rowIndex = sanctionSheetStartRowNum - 1;
            int celIndex;
            Row startRow = sanctionSheet.getRow(rowIndex) == null ? sanctionSheet.createRow(rowIndex) : sanctionSheet.getRow(rowIndex);
            Map<CEMSSanction, List<CEMSSanctionRegulation>> regulationMap = new HashMap<>();
            Map<CEMSSanction, List<CEMSSanctionedEmployeeInfo>> employeeMap = new HashMap<>();

            //master sheet data
            for (int i = 0; i < sanctions.size(); i++) {
                CEMSSanction sanction = sanctions.get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : sanctionSheet.createRow(rowIndex);
                rowIndex++;
                celIndex = sanctionSheetStartCell;

                //Row Number
                createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - sanctionSheetStartRowNum + 1));

                String organizationType = sanction.getOrganizationType() != null ? MessagesUtil.getString(sanction.getOrganizationType().name(), langCode) : "";
                String decisionMakingBody = "";
                String status = "";

                if (sanction.getDecisionMakingBodyCatalog() != null) {
                    decisionMakingBody = MessagesUtil.getString(sanction.getDecisionMakingBodyCatalog().getCode(), langCode);
                }
                if (sanction.getStatus() != null) {
                    status = sanction.getStatus().getDescription().getDescription(langId);
                }


                StringBuilder measureOfInfluence = new StringBuilder();
                for (CEMSSanctionMeasureInfluence cemsSanctionMeasureCatalog : sanction.getMeasureOfInfluence()) {
                    measureOfInfluence.append(cemsSanctionMeasureCatalog.getDescription().getDescription(langId)).append("; ");
                }

                StringBuilder measureReason = new StringBuilder();
                for (CEMSSanctionMeasureReasons data : sanction.getMeasureReasonCatalog()) {
                    measureReason.append(data.getDescription().getDescription(langId)).append("; ");
                }

                createCellWithData(row, celIndex++, startRow, workbook, organizationType);
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectLegalName());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectID());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getLicenseNumber());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getRegistrationLetterNumber());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getAddress());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSanctionNote());
                createCellWithData(row, celIndex++, startRow, workbook, measureOfInfluence.toString());
                createCellWithData(row, celIndex++, startRow, workbook, decisionMakingBody);
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getActionDate());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getDocumentNumber());
                createCellWithData(row, celIndex++, startRow, workbook, measureReason.toString());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getExecutionPeriod());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getValidityPeriodFrom());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getValidityPeriodTo());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getInitialCourtAppealDate());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getCourtDecision());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getFinalCourtAppealDate());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getInitialCourtAppealDecision());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getResponsiblePersonNames());
                createCellWithData(row, celIndex++, startRow, workbook, status);
                createCellWithData(row, celIndex, startRow, workbook, sanction.getNote());

                regulationMap.put(sanction, sanction.getRegulationList());
                employeeMap.put(sanction, sanction.getSanctionedEmployeeList());

            }
            insertRegulationsSheetData(workbook, regulationsSheet, regulationMap, langCode, regulationSheetStartCell, regulationSheetStartRowNum);
            insertEmployeesSheetData(workbook, employeesSheet, employeeMap, employeeSheetStartRowNum, employeeSheetStartCell);


            workbook.write(bos);
            workbook.close();
            return bos.toByteArray();

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return new byte[0];
    }

    private static void insertEmployeesSheetData(Workbook workbook, Sheet sheet, Map<CEMSSanction, List<CEMSSanctionedEmployeeInfo>> employeeMap, int employeeSheetStartRowNum, int employeeSheetStartCell) {

        int rowIndex = employeeSheetStartRowNum - 1;
        int celIndex;
        Row startRow = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);

        //master sheet data
        for (Map.Entry<CEMSSanction, List<CEMSSanctionedEmployeeInfo>> entry : employeeMap.entrySet()) {
            CEMSSanction sanction = entry.getKey();
            for (int i = 0; i < sanction.getSanctionedEmployeeList().size(); i++) {
                CEMSSanctionedEmployeeInfo employee = sanction.getSanctionedEmployeeList().get(i);
                Row row = startRow.getRowNum() == rowIndex ? startRow : sheet.createRow(rowIndex);
                rowIndex++;
                celIndex = employeeSheetStartCell;

                //Row Number
                createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - employeeSheetStartRowNum + 1));

                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectLegalName());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectID());
                createCellWithData(row, celIndex++, startRow, workbook, employee.getEmployeeName());
                createCellWithData(row, celIndex++, startRow, workbook, employee.getEmployeeId());
                createCellWithData(row, celIndex, startRow, workbook, employee.getEmployeePosition());

            }
        }
    }

    private static void insertRegulationsSheetData(Workbook workbook, Sheet sheet, Map<CEMSSanction, List<CEMSSanctionRegulation>> regulationMap, String langCode, int regulationSheetStartCell, int regulationSheetStartRowNum) {

        int rowIndex = regulationSheetStartRowNum - 1;
        int celIndex;
        Row startRow = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);

        //master sheet data
        for (Map.Entry<CEMSSanction, List<CEMSSanctionRegulation>> entry : regulationMap.entrySet()) {
            CEMSSanction sanction = entry.getKey();
            for (CEMSSanctionRegulation regulation : entry.getValue()) {

                Row row = startRow.getRowNum() == rowIndex ? startRow : sheet.createRow(rowIndex);
                rowIndex++;
                celIndex = regulationSheetStartCell;

                //Row Number
                createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - regulationSheetStartRowNum + 1));

                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectLegalName());
                createCellWithData(row, celIndex++, startRow, workbook, sanction.getSubjectID());
                createCellWithData(row, celIndex++, startRow, workbook, MessagesUtil.getString(regulation.getRegulationCatalog().getCode(), langCode));
                createCellWithData(row, celIndex++, startRow, workbook, regulation.getValue());
                createCellWithData(row, celIndex, startRow, workbook, regulation.getActualValue());
            }
        }

    }

    private static int readConfigNumericValue(String key) {
        try {
            return Integer.parseInt(ConfigurationUtil.get().get(key));
        } catch (Throwable ignore) {
        }
        return 1;
    }


    private static void createCellWithData(Row row, int celIndex, Row startRow, Workbook workbook, Object value) {
        Cell cell = row.getCell(celIndex) != null ? row.getCell(celIndex) : row.createCell(celIndex);
        Cell oldCell = startRow.getCell(celIndex) == null ? row.createCell(celIndex) : startRow.getCell(celIndex);
        // Copy style from old cell and apply to new cell
        CellStyle newCellStyle = workbook.createCellStyle();
        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
        cell.setCellStyle(newCellStyle);
        cell.setCellType(oldCell.getCellType());

        setCellValue(cell, value);
    }


    private static void setCellValue(Cell cell, Object value) {
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Float) {
                cell.setCellValue((Float) value);
            }
        } else {
            cell.setCellValue("");
        }

    }


    private static void insertData(CEMSRecommendation decision, Row row, int celIndex, int rowIndex, int startRowNum, Row startRow, Workbook workbook, long langId, String langCode) {
        //Row Number
        createCellWithData(row, celIndex++, startRow, workbook, (rowIndex - startRowNum + 1));

        String fi = decision.getInspection().getFi().getDescription().getDescription(langId);
        Date startDate = decision.getCreationDate();
        String letterInfo = (decision.getLetterInfo() != null && !decision.getLetterInfo().isEmpty() ? decision.getLetterInfo() + "; " : "") + (decision.getLetterDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(decision.getLetterDate()) : "");
        int number = decision.getNumber();
        String orderContent = decision.getOrderContent();
        Date executionPeriod = decision.getExecutionPeriod();
        StringBuilder responsibleUsersSb = new StringBuilder();

        decision.getFiResponsiblePersons().forEach(ru -> {
            if ((ru.getFullName() != null && !ru.getFullName().isEmpty()) || (ru.getPosition() != null && !ru.getPosition().isEmpty())) {
                responsibleUsersSb.append(ru.getFullName()).append(" - ").append(ru.getPosition())
                        .append(" | ");
            }
        });

        String fiAction = decision.getStatusInfo() != null ? decision.getStatusInfo().getFiActions() : "";
        String status = decision.getStatusInfo() != null ? decision.getStatusInfo().getStatus().getDescription().getDescription(langId) : "";
        String note = decision.getStatusInfo() != null ? decision.getStatusInfo().getNote() : "";

        createCellWithData(row, celIndex++, startRow, workbook, fi);
        createCellWithData(row, celIndex++, startRow, workbook, startDate);
        createCellWithData(row, celIndex++, startRow, workbook, letterInfo);
        createCellWithData(row, celIndex++, startRow, workbook, number);
        createCellWithData(row, celIndex++, startRow, workbook, orderContent);
        createCellWithData(row, celIndex++, startRow, workbook, executionPeriod);
        createCellWithData(row, celIndex++, startRow, workbook, responsibleUsersSb.toString());
        createCellWithData(row, celIndex++, startRow, workbook, fiAction);
        createCellWithData(row, celIndex++, startRow, workbook, status);
        createCellWithData(row, celIndex, startRow, workbook, note);
    }
}
