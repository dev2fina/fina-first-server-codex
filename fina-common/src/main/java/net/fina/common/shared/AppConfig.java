package net.fina.common.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AppConfig implements Serializable {

    private List<SimpleMenuModel> menu;
    private String language;
    private String user;
    private Map<String, String> userNameDescriptions;
    private String dateFormat;
    private String dateTimeFormat;
    private String numberFormat;
    private boolean changePassword;
    private boolean enaleXlsxSupport;
    private Theme theme;
    private boolean isAdvancedDashboardEnable;
    private boolean isExportImportEnable;
    private FinaPasswordPolicy finaPasswordPolicy;
    private List<String> permissions;

    private long maxPostSize;
    private String maxPostSizeDisplayValue;
    private boolean ecmEnable;
    private String finaStatUrl;
    private AuthorizationType authenticationType;
    private String i18nTranslationVersion = "1.0";

    private Map<String, Serializable> properties;

    public AppConfig() {
    }

    public List<SimpleMenuModel> getMenu() {
        return menu;
    }

    public void setMenu(List<SimpleMenuModel> menu) {
        this.menu = menu;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
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

    public Map<String, String> getUserNameDescriptions() {
        return userNameDescriptions;
    }

    public void setUserNameDescriptions(Map<String, String> userNameDescriptions) {
        this.userNameDescriptions = userNameDescriptions;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public boolean isEnaleXlsxSupport() {
        return enaleXlsxSupport;
    }

    public void setEnaleXlsxSupport(boolean enaleXlsxSupport) {
        this.enaleXlsxSupport = enaleXlsxSupport;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public boolean isAdvancedDashboardEnable() {
        return isAdvancedDashboardEnable;
    }

    public void setAdvancedDashboardEnable(boolean advancedDashboardEnable) {
        isAdvancedDashboardEnable = advancedDashboardEnable;
    }

    public boolean isExportImportEnable() {
        return isExportImportEnable;
    }

    public void setExportImportEnable(boolean exportImportEnable) {
        isExportImportEnable = exportImportEnable;
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

    public boolean isEcmEnable() {
        return ecmEnable;
    }

    public void setEcmEnable(boolean ecmEnable) {
        this.ecmEnable = ecmEnable;
    }


    public String getFinaStatUrl() {
        return finaStatUrl;
    }

    public void setFinaStatUrl(String finaStatUrl) {
        this.finaStatUrl = finaStatUrl;
    }

    public FinaPasswordPolicy getPasswordPolicy() {
        return finaPasswordPolicy;
    }

    public void setPasswordPolicy(FinaPasswordPolicy finaPasswordPolicy) {
        this.finaPasswordPolicy = finaPasswordPolicy;
    }

    public AuthorizationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthorizationType authType) {
        this.authenticationType = authType;
    }

    public String getI18nTranslationVersion() {
        return i18nTranslationVersion;
    }

    public void setI18nTranslationVersion(String i18nTranslationVersion) {
        this.i18nTranslationVersion = i18nTranslationVersion;
    }

    public Map<String, Serializable> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }
}