package net.fina.server.util;

import net.fina.messages.MessagesUtil;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.Role;
import net.fina.server.security.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserExportUtil {

    public static byte[] exportToExcel(Map<Long, Role> roleMap, Map<Long, User> userMap, Map<Long, Permission> permissionMap,
                                       List<Object[]> roleUsers, List<Object[]> userPermissions, List<Object[]> rolePermissions, long langId, String langCode) throws IOException {


        Workbook workbook = new XSSFWorkbook();


        //main sheet
        Sheet userInfoSheet = workbook.createSheet(MessagesUtil.getString("net.fina.users", langCode));

        //pivot sheets
        XSSFSheet pivotSheetUsers = (XSSFSheet) workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.userAndPermissions", langCode));
        XSSFSheet pivotSheetRoles = (XSSFSheet) workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.roleAndPermissions", langCode));
        XSSFSheet pivotSheetRoleUsers = (XSSFSheet) workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.roleAndUsers", langCode));

        // data sheets
        Sheet userData = workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.userPermissionData", langCode));
        Sheet roleData = workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.rolePermissionData", langCode));
        Sheet roleUsersData = workbook.createSheet(MessagesUtil.getString("net.fina.web.export.userPermission.sheet.roleUserData", langCode));

        // fill data
        createUserDataCells(roleMap, userMap, permissionMap, workbook, userData, userPermissions, langId, langCode);
        createRoleDataCells(roleMap, permissionMap, workbook, roleData, rolePermissions, langId, langCode);
        createRoleUserDataCells(roleMap, userMap, workbook, roleUsersData, roleUsers, langId, langCode);
        createUserInfoSheet(userMap, workbook, userInfoSheet, langId, langCode);

        // create pivot tables
        createUserPermissionPivotTable(pivotSheetUsers, userData, userPermissions.size() + 1);
        createRolePermissionPivotTable(pivotSheetRoles, roleData, rolePermissions.size() + 1);
        createRoleUsersPivotTable(pivotSheetRoleUsers, roleUsersData, roleUsers.size() + 1);

        // output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        outputStream.close();

        return outputStream.toByteArray();
    }


    private static void createUserInfoSheet(Map<Long, User> userMap, Workbook workbook, Sheet userInfoSheet, long langId, String langCode) {
        int startColumn = 0;
        int startRow = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Row headerRow = userInfoSheet.createRow(startRow++);
        List<String> headerNames = Arrays.asList(
                "net.fina.user.name",
                "net.fina.user.fullName",
                "net.fina.title",
                "net.fina.rd.generalInfoColumnTitle",
                "net.fina.phone",
                "net.fina.email",
                "net.fina.user.last.login.date",
                "net.fina.web.export.userPermission.header.blocked",
                "net.fina.web.export.userPermission.header.disabled",
                "DELETED"
        );

        createHeader(headerNames, workbook, headerRow, startColumn, langCode);
        headerRow.setHeight((short) (headerRow.getHeight() * 1.8));

        int rowIndex = startRow;
        for (User user : userMap.values()) {
            if (user != null) {
                Row row = userInfoSheet.createRow(rowIndex++);

                createDataRow(row, startColumn, new String[]{
                        user.getLogin(),
                        user.getDescription() != null ? user.getDescription().getDescription(langId) : "",
                        user.getTitledescription() != null ? user.getTitledescription().getDescription(langId) : "",
                        user.getContactPersonDescription() != null ? user.getContactPersonPosition().getDescription(langId) : "",
                        user.getPhone() != null ? user.getPhone() : "",
                        user.getEmail() != null ? user.getEmail() : "",
                        user.getLastLoginDate() != null ? sdf.format(user.getLastLoginDate()) : "",
                        String.valueOf(user.getBlocked()),
                        String.valueOf(user.isDisabled()),
                        String.valueOf(user.isDeleted())
                });
            }
        }

        for (int i = 0; i < headerNames.size(); i++) {
            userInfoSheet.autoSizeColumn(i);
        }
    }


    private static void createRoleUserDataCells(Map<Long, Role> roleMap, Map<Long, User> userMap, Workbook workbook, Sheet roleUsersData, List<Object[]> roles, long langId, String langCode) {
        int startColumn = 0;
        int startRow = 0;

        Row row = roleUsersData.createRow(startRow++);
        createHeader(Arrays.asList("#",
                "net.fina.web.export.userPermission.header.role.code",
                "net.fina.web.export.userPermission.header.role.name",
                "net.fina.web.export.userPermission.header.user.code",
                "net.fina.web.export.userPermission.header.user.name"), workbook, row, startColumn, langCode);
        row.setHeight((short) (row.getHeight() * 1.8));

        for (int i = 0; i < roles.size(); i++) {
            row = roleUsersData.createRow(startRow++);

            User user = (Long) roles.get(i)[1] != -1L ? userMap.get((Long) roles.get(i)[1]) : null;
            Role role = (Long) roles.get(i)[0] != -1L ? roleMap.get((Long) roles.get(i)[0]) : null;

            createDataRow(row, startColumn, new String[]{
                    String.valueOf(i + 1),
                    role != null ? role.getCode() : null,
                    role != null && role.getDescription() != null ? role.getDescription().getDescription(langId) : " ",
                    user != null ? user.getLogin() : " ",
                    user != null && user.getDescription() != null ? user.getDescription().getDescription(langId) : " "
            });
        }

        for (int i = 0; i <= 4; i++) {
            roleUsersData.autoSizeColumn(i);
        }

    }

    private static void createRoleDataCells(Map<Long, Role> roleMap, Map<Long, Permission> permissionMap, Workbook workbook, Sheet roleData, List<Object[]> rolePermissions, long langId, String langCode) {
        int startColumn = 0;
        int startRow = 0;

        Row row = roleData.createRow(startRow++);
        createHeader(Arrays.asList("#",
                "net.fina.web.export.userPermission.header.role.code",
                "net.fina.web.export.userPermission.header.role.name",
                "net.fina.web.export.userPermission.header.permission.code",
                "net.fina.web.export.userPermission.header.permission.name"), workbook, row, startColumn, langCode);
        row.setHeight((short) (row.getHeight() * 1.8));

        for (int i = 0; i < rolePermissions.size(); i++) {
            row = roleData.createRow(startRow++);

            Role role = (Long) rolePermissions.get(i)[0] != -1L ? roleMap.get((Long) rolePermissions.get(i)[0]) : null;
            Permission permission = (Long) rolePermissions.get(i)[1] != -1L ? permissionMap.get((Long) rolePermissions.get(i)[1]) : null;

            createDataRow(row, startColumn, new String[]{
                    String.valueOf(i + 1),
                    role != null ? role.getCode() : null,
                    role != null && role.getDescription() != null ? role.getDescription().getDescription(langId) : " ",
                    permission != null ? permission.getIdName() : " ",
                    permission != null && permission.getDescription() != null ? permission.getDescription().getDescription(langId) : " "
            });
        }
        for (int i = 0; i <= 4; i++) {
            roleData.autoSizeColumn(i);
        }

    }

    private static void createUserDataCells(Map<Long, Role> roleMap, Map<Long, User> userMap, Map<Long, Permission> permissionMap,
                                            Workbook workbook, Sheet userData, List<Object[]> permissions, long langId, String langCode) {
        int startColumn = 0;
        int startRow = 0;

        Row row = userData.createRow(startRow++);
        createHeader(Arrays.asList("#",
                "net.fina.web.export.userPermission.header.user.code",
                "net.fina.web.export.userPermission.header.user.name",
                "net.fina.web.export.userPermission.header.role.code",
                "net.fina.web.export.userPermission.header.role.name",
                "net.fina.web.export.userPermission.header.permission.code",
                "net.fina.web.export.userPermission.header.permission.name",
                "net.fina.web.export.userPermission.header.blocked",
                "net.fina.web.export.userPermission.header.disabled"
        ), workbook, row, startColumn, langCode);

        row.setHeight((short) (row.getHeight() * 1.8));

        for (int i = 0; i < permissions.size(); i++) {
            row = userData.createRow(startRow++);

            User user = (Long) permissions.get(i)[0] != -1L ? userMap.get((Long) permissions.get(i)[0]) : null;
            Permission permission = (Long) permissions.get(i)[1] != -1L ? permissionMap.get((Long) permissions.get(i)[1]) : null;
            Role role = (Long) permissions.get(i)[2] != -1L ? roleMap.get((Long) permissions.get(i)[2]) : null;

            createDataRow(row, startColumn, new String[]{
                    String.valueOf(i + 1),
                    user != null ? user.getLogin() : " ",
                    user != null && user.getDescription() != null ? user.getDescription().getDescription(langId) : " ",
                    role != null ? role.getCode() : MessagesUtil.getString("net.fina.web.export.userPermission.sheet.withoutRole", langCode),
                    role != null && role.getDescription() != null ? role.getDescription().getDescription(langId)
                            : MessagesUtil.getString("net.fina.web.export.userPermission.sheet.withoutRole", langCode),
                    permission != null ? permission.getIdName() : " ",
                    permission != null && permission.getDescription() != null ? permission.getDescription().getDescription(langId) : " ",
                    user != null ? user.getBlocked() ? "1" : "0" : " ",
                    user != null ? user.isDisabled() ? "1" : "0" : " "
            });
        }

        for (int i = 0; i <= 8; i++) {
            userData.autoSizeColumn(i);
        }

    }

    private static void createRoleUsersPivotTable(XSSFSheet pivotSheetRoleUsers, Sheet roleUsersData, int i) {
        pivotSheetRoleUsers.setColumnWidth(0, 2 * 256);
        pivotSheetRoleUsers.setColumnWidth(1, 25 * 256);
        pivotSheetRoleUsers.setColumnWidth(2, 60 * 256);

        pivotSheetRoleUsers.setDisplayGridlines(false);

        XSSFPivotTable pivotTableRoleUsers = pivotSheetRoleUsers.createPivotTable(
                new AreaReference("B1:E" + i, null),
                new CellReference("B2"),
                roleUsersData);

        pivotTableRoleUsers.addRowLabel(0);
        pivotTableRoleUsers.addRowLabel(2);

        setPivotStyle(pivotTableRoleUsers);
    }

    private static void createRolePermissionPivotTable(XSSFSheet pivotSheetRoles, Sheet roleData, int i) {
        pivotSheetRoles.setColumnWidth(0, 2 * 256);
        pivotSheetRoles.setColumnWidth(1, 25 * 256);
        pivotSheetRoles.setColumnWidth(2, 60 * 256);

        pivotSheetRoles.setDisplayGridlines(false);

        XSSFPivotTable pivotTableRoles = pivotSheetRoles.createPivotTable(
                new AreaReference("B1:E" + i, null),
                new CellReference("B2"),
                roleData);

        pivotTableRoles.addRowLabel(0);
        pivotTableRoles.addRowLabel(2);

        setPivotStyle(pivotTableRoles);
    }

    private static void createUserPermissionPivotTable(XSSFSheet pivotSheetUsers, Sheet userData, int i) {
        pivotSheetUsers.setColumnWidth(0, 2 * 256);
        pivotSheetUsers.setColumnWidth(1, 25 * 256);
        pivotSheetUsers.setColumnWidth(2, 25 * 256);
        pivotSheetUsers.setColumnWidth(3, 60 * 256);

        pivotSheetUsers.setDisplayGridlines(false);

        XSSFPivotTable pivotTableUsers = pivotSheetUsers.createPivotTable(
                new AreaReference("B1:I" + i, null),
                new CellReference("B2"),
                userData);

        pivotTableUsers.addRowLabel(0);
        pivotTableUsers.addRowLabel(2);
        pivotTableUsers.addRowLabel(4);

        setPivotStyle(pivotTableUsers);
    }


    private static void createDataRow(Row row, int startCol, String[] values) {
        for (int j = 0; j < values.length; j++) {
            Cell cell = row.createCell(startCol + j);
            cell.setCellValue(values[j]);
        }
    }

    private static void createHeader(List<String> headerNames, Workbook wb, Row row, int cellIndex, String langCode) {
        for (int i = 0; i < headerNames.size(); i++) {
            Cell cell = row.createCell(cellIndex + i);
            cell.setCellValue(MessagesUtil.getString(headerNames.get(i), langCode));

            CellStyle style = wb.createCellStyle();
            style.setWrapText(true);
            style.setBorderBottom(BorderStyle.valueOf((short) BorderStyle.THIN.ordinal()));
            style.setBorderTop(BorderStyle.valueOf((short) BorderStyle.THIN.ordinal()));
            style.setBorderRight(BorderStyle.valueOf((short) BorderStyle.THIN.ordinal()));
            style.setBorderLeft(BorderStyle.valueOf((short) BorderStyle.THIN.ordinal()));
            style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setAlignment(HorizontalAlignment.forInt((short) HorizontalAlignment.CENTER.ordinal()));
            style.setVerticalAlignment(VerticalAlignment.forInt((short) VerticalAlignment.CENTER.ordinal()));
            style.setFillPattern(FillPatternType.forInt((short) FillPatternType.LEAST_DOTS.ordinal()));
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }
    }

    private static void setPivotStyle(XSSFPivotTable pivotTable) {
        pivotTable.getCTPivotTableDefinition().setRowGrandTotals(false);
        pivotTable.getCTPivotTableDefinition().setCompact(false);
        pivotTable.getCTPivotTableDefinition().setCompactData(false);

        // set pivot design to 'outline'
        CTPivotFields pivotFields = pivotTable.getCTPivotTableDefinition().getPivotFields();
        for (int i = 0; i < pivotFields.getCount(); i++) {
            pivotFields.getPivotFieldArray(i).setCompact(false);
        }
    }


}
