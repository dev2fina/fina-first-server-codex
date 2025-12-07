package net.fina.common.client;

import net.fina.common.shared.FinaPasswordPolicy;
import net.fina.common.shared.LanguageSampleModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Configuration implements Serializable {

    protected int uploadFilesGridPageSize;
    protected int returnsGridPageSize;

    protected boolean isLdapUser;

    protected String dateFormat;

    protected String dateTimeFormat;

    protected String numberFormat;

    protected String username;

    protected String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

    protected List<String> permissions;

    protected List<String> fileNamePatterns;

    protected Boolean requestPasswordChange;

    protected boolean requestPassword;
    protected boolean requestUserId;

    protected String languageCode;

    private long maxPostSize;
    private String maxPostSizeDisplayValue;
    private boolean restrictFileUploadWhileProcessing;

    private String headerText;

    private List<String> fiCodes;
    private Map<String, String> periodTypePattern;

    private FinaPasswordPolicy passwordPolicy;
    private List<LanguageSampleModel> languages;

    private boolean hasUserMailVerified;

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String text) {
        this.headerText = text;
    }

    public Boolean getRequestPasswordChange() {
        return requestPasswordChange;
    }

    public void setRequestPasswordChange(Boolean requestPasswordChange) {
        this.requestPasswordChange = requestPasswordChange;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public int getUploadFilesGridPageSize() {
        return uploadFilesGridPageSize;
    }

    public void setUploadFilesGridPageSize(int uploadFilesGridPageSize) {
        this.uploadFilesGridPageSize = uploadFilesGridPageSize;
    }

    public List<String> getFileNamePatterns() {
        return fileNamePatterns;
    }

    public void setFileNamePatterns(List<String> fileNamePatterns) {
        this.fileNamePatterns = fileNamePatterns;
    }

    public int getReturnsGridPageSize() {
        return returnsGridPageSize;
    }

    public void setReturnsGridPageSize(int returnsGridPageSize) {
        this.returnsGridPageSize = returnsGridPageSize;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public boolean isLdapUser() {
        return isLdapUser;
    }

    public void setIsLdapUser(boolean isLdapUser) {
        this.isLdapUser = isLdapUser;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public boolean isRequestPassword() {
        return requestPassword;
    }

    public void setRequestPassword(boolean requestPassword) {
        this.requestPassword = requestPassword;
    }

    public boolean isRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(boolean requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public long getMaxPostSize() {
        return maxPostSize;
    }

    public void setMaxPostSize(long maxPostSize) {
        this.maxPostSize = maxPostSize;
    }

    public String getMaxPostSizeDisplayValue() {
        return maxPostSizeDisplayValue;
    }

    public void setMaxPostSizeDisplayValue(String maxPostSizeDisplayValue) {
        this.maxPostSizeDisplayValue = maxPostSizeDisplayValue;
    }

    public boolean isRestrictFileUploadWhileProcessing() {
        return restrictFileUploadWhileProcessing;
    }

    public void setRestrictFileUploadWhileProcessing(boolean restrictFileUploadWhileProcessing) {
        this.restrictFileUploadWhileProcessing = restrictFileUploadWhileProcessing;
    }

    public List<String> getFiCodes() {
        return fiCodes;
    }

    public void setFiCodes(List<String> fiCodes) {
        this.fiCodes = fiCodes;
    }

    public Map<String, String> getPeriodTypePattern() {
        return periodTypePattern;
    }

    public void setPeriodTypePattern(Map<String, String> periodTypePattern) {
        this.periodTypePattern = periodTypePattern;
    }

    public List<LanguageSampleModel> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageSampleModel> languages) {
        this.languages = languages;
    }


    public FinaPasswordPolicy getPasswordPolicy() {
        return passwordPolicy == null ? new FinaPasswordPolicy() : passwordPolicy;
    }

    public void setPasswordPolicy(FinaPasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public boolean isHasUserMailVerified() {
        return hasUserMailVerified;
    }

    public void setHasUserMailVerified(boolean hasUserMailVerified) {
        this.hasUserMailVerified = hasUserMailVerified;
    }
}
