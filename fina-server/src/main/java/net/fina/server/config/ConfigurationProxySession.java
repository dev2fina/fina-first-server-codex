package net.fina.server.config;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.Configuration;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.AuthorizationType;
import net.fina.common.shared.FinaPasswordPolicy;
import net.fina.common.shared.UserType;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.LanguageModelHelper;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.jboss.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.fina.security.util.SecurityUtil.getAuthTypeEnum;

@Stateless
public class ConfigurationProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private UploadFileLocal uploadFileLocal;

    @Inject
    private LanguageLocal languageLocal;

    @Inject
    private PropertyLocal propertyLocal;

    @Inject
    private UserLocal userLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private AuthorizationLocal authorizationLocal;

    public Configuration getConfiguration(String login, String locale) {
        Configuration config = new Configuration();
        String authTypeProperty = propertyLocal.getSystemProperty(PropertyKeys.FINA_CURRENT_AUTHENTICATION);
        AuthorizationType authType = getAuthTypeEnum(authTypeProperty);

        User currUser = userLocal.findUserbyLogin(login);

        config.setUserName(login);
        config.setPermissions(authorizationLocal.loadUserPermission(login));
        config.setIsLdapUser(currUser.getUserType() == UserType.LDAP_USER);
        config.setRequestPasswordChange(!authType.equals(AuthorizationType.LDAP) && currUser.getChangePassword());
        config.setFiCodes(userLocal.getUserFiCodes(login));

        config.setHasUserMailVerified(isUserMailProvided(currUser));

        String defaultLanguageCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);

        locale = (locale == null) || locale.isEmpty()
                ? defaultLanguageCode : locale;
        Language language = languageLocal.getLanguageByCode(locale);

        config.setLanguageCode(language.getCode().trim());
        if (language.getDateFormat() != null) {
            config.setDateFormat(language.getDateFormat().trim());
        }
        if (language.getDateTimeFormat() != null) {
            config.setDateTimeFormat(language.getDateTimeFormat().trim());
        }
        if (language.getNumberFormat() != null) {
            config.setNumberFormat(language.getNumberFormat().trim());
        }

        String restrictFileUploadWhileProcessingString = propertyLocal.getSystemProperty(PropertyKeys.DCS_RESTRICT_FILE_UPLOAD_WHILE_PROCESSING);
        if (restrictFileUploadWhileProcessingString != null && !restrictFileUploadWhileProcessingString.trim().isEmpty()) {
            int res;
            try {
                res = Integer.parseInt(restrictFileUploadWhileProcessingString);
            } catch (NumberFormatException e) {
                res = 0;
            }
            config.setRestrictFileUploadWhileProcessing(res > 0);
        }

        config.setLanguages(LanguageModelHelper.toModels(languageLocal.loadLanguages(), defaultLanguageCode));
        config.setMaxPostSize(CommonUtil.getServerMaxPostSizeProperty(ManagementFactory.getPlatformMBeanServer()));

        String passMinLength = propertyLocal.getSystemProperty(PropertyKeys.MINIMAL_PASSWORD_LENGTH);
        String passContainLetters = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_LETTERS);
        String passContainNums = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_NUMS);
        String passContainUpperLetters = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_LETTERS_UPPERCASE);
        String passContainSpecialChars = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_SPECIAL_CHARACTERS);

        FinaPasswordPolicy passwordPolicy = new FinaPasswordPolicy(passMinLength, passContainLetters, passContainNums, passContainUpperLetters, passContainSpecialChars);
        config.setPasswordPolicy(passwordPolicy);

        setMatrixOptions(config);

        return config;
    }


    public Configuration getConfiguration(String locale) {
        return getConfiguration(userLocal.getCurrentUserLogin(), locale);
    }


    public Map<String, String> getSystemInformation() throws Exception {
        Map<String, String> result = new HashMap<>();

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("jboss.as:management-root=server");
        String version = (String) (server.getAttribute(name, "releaseVersion"));
        result.put("JBoss", version);

        List<String[]> databaseProps = propertyLocal.getDatabaseProperties();
        for (String[] data : databaseProps) {
            result.put(data[0], data[1]);
        }

        return result;
    }

    public Map<String, String> getSysProperties() {
        return propertyLocal.getSystemProperty();
    }

    public void setSystemProperty(String key, String value) {
        propertyLocal.setSystemProperty(key, value);
    }

    private void setMatrixOptions(Configuration config) {
        try {
            config.setFileNamePatterns(uploadFileLocal.getAllPatterns());
            if (config.getPermissions().contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER) && !config.getFiCodes().isEmpty()) {
                String fiTypeCode = fiLocal.getFiTypeCodeByFiCode(config.getFiCodes().get(0));
                config.setPeriodTypePattern(uploadFileLocal.getPeriodTypePatternsForFiType(fiTypeCode));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    private boolean isUserMailProvided(User currUser) {
        if (currUser != null) {
            return (currUser.getEmail() != null && !currUser.getEmail().isBlank());
        }
        return false;
    }

}
