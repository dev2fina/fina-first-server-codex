package net.fina.server.dcs.password.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.security.util.SecurityUtil;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.password.api.ResetPasswordLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.entity.User;
import net.fina.server.sms.api.SmsLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.UUID;

@Stateless
@Local(ResetPasswordLocal.class)
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class ResetPasswordSession implements ResetPasswordLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private MailMessageLocal mailMessageLocal;

    @EJB
    private SmsLocal smsLocal;

    @Resource
    private SessionContext sessionContext;

    @Override
    public void resetPasswordByEmail(String login, String mail, String langCode) throws FinATypeException {
        log.info("start user search : [" + login + "]");
        User user = getUserByResetType(ResetType.EMAIL, login, mail, null);
        String randomPassword = getRandomPassword();
        changePassAndSendMail(user, randomPassword, langCode);
    }

    @Override
    public void resetPasswordBySms(String login, String phone) throws FinATypeException {
        log.info("start user search : [" + login + "]");
        User user = getUserByResetType(ResetType.PHONE, login, null, phone);
        String randomPassword = getRandomPassword();
        changePassAndSendSms(user, randomPassword);
    }

    @Override
    public void resetPasswordByEmailAndSms(String login, String mail, String phone, String langCode) throws FinATypeException {
        log.info("start user search : [" + login + "]");
        User user = getUserByResetType(ResetType.EMAIL_AND_PHONE, login, mail, phone);
        String randomPassword = getRandomPassword();
        initRandomPasswordTo(user, randomPassword);
        sendEmail(user.getEmail(), randomPassword, langCode);
        sendSms(user.getPhone(), randomPassword);
    }

    private User getUserByResetType(ResetType resetType, String login, String mail, String phone) throws FinATypeException {
        try {
            User user = null;

            switch (resetType) {
                case EMAIL:
                    user = em.createQuery("select u from SYS_USERS u where u.login=:login and u.email=:mail and u.deleted=:deleted", User.class)
                            .setParameter("login", login.trim())
                            .setParameter("mail", mail.trim())
                            .setParameter("deleted", false)
                            .getSingleResult();
                    break;
                case PHONE:
                    user = em.createQuery("select u from SYS_USERS u where u.login=:login and u.phone=:phone and u.deleted=:deleted", User.class)
                            .setParameter("login", login.trim())
                            .setParameter("phone", phone.trim())
                            .setParameter("deleted", false)
                            .getSingleResult();
                    break;
                case EMAIL_AND_PHONE:
                    user = em.createQuery("select u from SYS_USERS u where u.login=:login and u.email=:mail and u.phone=:phone and u.deleted=:deleted", User.class)
                            .setParameter("login", login.trim())
                            .setParameter("mail", mail.trim())
                            .setParameter("phone", phone.trim())
                            .setParameter("deleted", false)
                            .getSingleResult();
                    break;
                default:
                    break;
            }

            if (user == null) {
                throw new FinATypeException(FinATypeException.Type.USER_NOT_FOUND);
            } else if (user.getBlocked()) {
                throw new FinATypeException(FinATypeException.Type.USER_BLOCKED);
            } else if (user.isDisabled()) {
                throw new FinATypeException(FinATypeException.Type.USER_DISABLED);
            }

            return user;

        } catch (NoResultException nre) {
            log.error("Cannot Find User : [" + login + "]");
            throw new FinATypeException(FinATypeException.Type.USER_NOT_FOUND);
        } catch (NonUniqueResultException nue) {
            log.error("Found More Then One User : [" + login + "]");
            throw new FinATypeException(FinATypeException.Type.NOT_UNIQUE_USER_MAIL);
        }
    }

    private void changePassAndSendMail(User user, String decodedPassword, String langCode) throws FinATypeException {
        try {
            initRandomPasswordTo(user, decodedPassword);
            sendEmail(user.getEmail(), decodedPassword, langCode);
        } catch (FinATypeException e) {
            //RollBack transaction
            sessionContext.setRollbackOnly();
            log.error(e.getMessage(), e);
            throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
        }
    }

    private void changePassAndSendSms(User user, String decodedPassword) throws FinATypeException {
        try {
            initRandomPasswordTo(user, decodedPassword);
            sendSms(user.getPhone(), decodedPassword);
        } catch (FinATypeException e) {
            //RollBack transaction
            sessionContext.setRollbackOnly();
            log.error(e.getMessage(), e);
            throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
        }
    }

    private void sendEmail(String email, String decodedPassword, String langCode) throws FinATypeException {
        mailMessageLocal.resetPasswordReplyMessage(email, decodedPassword, langCode);
    }

    private void sendSms(String phone, String decodedPassword) throws FinATypeException {
        smsLocal.createResetPasswordReplyMessage(phone, decodedPassword);
    }

    private String getRandomPassword() {
        UUID uuid = UUID.randomUUID();
        String generatedPass = Long.toString(uuid.getMostSignificantBits(), 36).replace("-", "");
        return generatedPass.substring(0, 8);
    }

    private void initRandomPasswordTo(User user, String decodedPassword) {
        user.setPassword(SecurityUtil.encodePassword(decodedPassword));
        user.setChangePassword(true);
        em.merge(user);
    }

    private enum ResetType {
        EMAIL,
        PHONE,
        EMAIL_AND_PHONE
    }

}
