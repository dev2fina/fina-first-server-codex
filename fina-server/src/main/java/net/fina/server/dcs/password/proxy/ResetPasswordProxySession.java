package net.fina.server.dcs.password.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.ResetPasswordMetaModel;
import net.fina.server.dcs.password.api.ResetPasswordLocal;
import net.fina.server.dcs.password.util.ResetPasswordStatus;
import org.jboss.logging.Logger;

@Stateless
public class ResetPasswordProxySession {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private ResetPasswordLocal resetPasswordLocal;

    public ResetPasswordStatus resetUserPassword(ResetPasswordMetaModel resetPasswordMetaModel, String langCode) {
        ResetPasswordStatus result = ResetPasswordStatus.ERROR;

        try {

            if (resetPasswordMetaModel != null) {
                result = ResetPasswordStatus.SUCCESS;

                final String userLogin = resetPasswordMetaModel.getUserLogin();
                final String userEmail = resetPasswordMetaModel.getUserEmail();
                final String userPhone = resetPasswordMetaModel.getUserPhone();

                if (userLogin == null || userLogin.trim().isEmpty()) {
                    return ResetPasswordStatus.ERROR_USER_LOGIN_NOT_PROVIDED;
                }

                // email
                boolean isEmailServiceEnable = isEmailServiceEnable();
                boolean isUserEmailValueProvided = isStringValueProvided(userEmail);

                // sms
                boolean isSmsServiceEnable = isSmsServiceEnable();
                boolean isUserPhoneValueProvided = isStringValueProvided(userPhone);

                if (isEmailServiceEnable && isSmsServiceEnable && (!isUserEmailValueProvided || !isUserPhoneValueProvided)) {
                    return ResetPasswordStatus.ERROR_USER_EMAIL_AND_PHONE_NOT_PROVIDED;
                } else if (isEmailServiceEnable && !isUserEmailValueProvided) {
                    return ResetPasswordStatus.ERROR_USER_EMAIL_NOT_PROVIDED;
                } else if (isSmsServiceEnable && !isUserPhoneValueProvided) {
                    return ResetPasswordStatus.ERROR_USER_PHONE_NOT_PROVIDED;
                }

                try {
                    if (isEmailServiceEnable && isSmsServiceEnable) {
                        resetPasswordLocal.resetPasswordByEmailAndSms(userLogin, userEmail, userPhone, langCode);
                    } else if (isEmailServiceEnable) {
                        resetPasswordLocal.resetPasswordByEmail(userLogin, userEmail, langCode);
                    } else if (isSmsServiceEnable) {
                        resetPasswordLocal.resetPasswordBySms(userLogin, userPhone);
                    }

                } catch (FinATypeException e) {
                    log.error(e.getMessage(), e);
                    throw e;
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
                }
            }
        } catch (FinATypeException exception) {
            result = switch (exception.getType()) {
                case USER_NOT_FOUND -> ResetPasswordStatus.USER_NOT_FOUND;
                case USER_BLOCKED -> ResetPasswordStatus.USER_BLOCKED;
                case USER_DISABLED -> ResetPasswordStatus.USER_DISABLED;
                default -> ResetPasswordStatus.ERROR;
            };
        }

        return result;
    }

    private boolean isStringValueProvided(String value) {
        return (value != null && !value.trim().isEmpty());
    }

    private boolean isSmsServiceEnable() {
        boolean result = false;
        try {
            String smsEnableValue = ConfigurationUtil.get().get("SMS.enable");
            result = (smsEnableValue != null && Integer.parseInt(smsEnableValue) > 0);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return result;
    }

    private boolean isEmailServiceEnable() {
        boolean result = false;
        try {
            String mailRobotDisableValue = ConfigurationUtil.get().get("MAIL_ROBOT_SERVICE_DISABLE");
            result = (mailRobotDisableValue != null && Integer.parseInt(mailRobotDisableValue) <= 0);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return result;
    }

}
