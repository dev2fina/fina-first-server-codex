package net.fina.server.st.json;

import java.util.List;

public class ExcelConverterMetaInfo {
    private List<String> returnDefCodes;
    private int vctEmptyLine;
    private String sheetControl;
    private String sheetProtectionPassword;
    private String sheetProtectionPasswordByFiType;

    public List<String> getReturnDefCodes() {
        return returnDefCodes;
    }

    public void setReturnDefCodes(List<String> returnDefCodes) {
        this.returnDefCodes = returnDefCodes;
    }

    public int getVctEmptyLine() {
        return vctEmptyLine;
    }

    public void setVctEmptyLine(int vctEmptyLine) {
        this.vctEmptyLine = vctEmptyLine;
    }

    public String getSheetControl() {
        return sheetControl;
    }

    public void setSheetControl(String sheetControl) {
        this.sheetControl = sheetControl;
    }

    public String getSheetProtectionPassword() {
        return sheetProtectionPassword;
    }

    public void setSheetProtectionPassword(String sheetProtectionPassword) {
        this.sheetProtectionPassword = sheetProtectionPassword;
    }

    public String getSheetProtectionPasswordByFiType() {
        return sheetProtectionPasswordByFiType;
    }

    public void setSheetProtectionPasswordByFiType(String sheetProtectionPasswordByFiType) {
        this.sheetProtectionPasswordByFiType = sheetProtectionPasswordByFiType;
    }
}
