package net.fina.server.dcs.mail.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.constants.MessageStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MailLogFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.mail.entity.MessageReply_;
import net.fina.server.dcs.mail.entity.Message_;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileConvertEvent;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.entity.User;
import net.fina.server.util.DBUtil;
import org.jboss.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(MailMessageLocal.class)
@Interceptors(RecordingAuditor.class)
public class MailMessageSession implements MailMessageLocal {
    private static final String DEFAULT_LANGUAGE = "en_US";
    @Inject
    Logger log;
    @Inject
    private EntityManager em;
    @EJB
    private PropertyLocal propertyLocal;
    @Inject
    private Event<UploadFileConvertEvent> uploadFileConvertEvent;

    @Override
    public Message saveMessage(Message message, String languageCode) {
        if (message.getId() > 0) {
            message = em.merge(message);
        } else {
            em.persist(message);

            //Fire Convert event
            if (message.getUploadFiles() != null) {
                for (UploadFile uf : message.getUploadFiles()) {
                    uploadFileConvertEvent.fire(new UploadFileConvertEvent(uf.getId(), languageCode, uf.getFileName()));
                }
            }
        }
        return message;
    }

    @Override
    public MessageReply saveMessageReplay(long messageId, MessageReply messageReply) {
        Message message = em.find(Message.class, messageId);
        em.persist(messageReply);
        message.setMessageReply(messageReply);
        return messageReply;
    }

    @Override
    public MessageReply saveReplay(MessageReply messageReply) {
        em.persist(messageReply);
        return messageReply;
    }

    @Override
    public void updateMailReplayStatus(long messageReplyId, MessageReplySendStatus status) {
        MessageReply messageReply = em.find(MessageReply.class, messageReplyId);
        messageReply.setSendStatus(status);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MessageReply> loadNotSentMessageReplays(String mailUser) {
        Query query = em.createQuery("SELECT mmr FROM OUT_MAIL_MESSAGE_REPLY mmr WHERE mmr.sendStatus=:sendStatus and mmr.from=:mailUser ");
        query.setParameter("sendStatus", MessageReplySendStatus.NOT_SENT);
        query.setParameter("mailUser", mailUser);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> loadActiveMessageIds(String mailUser) {
        Query query = em.createQuery("SELECT m.messageId FROM IN_MAIL_MESSAGE m WHERE m.status<>:status and m.mailUser=:mailUser ");
        query.setParameter("status", MessageStatus.ARCHIVE);
        query.setParameter("mailUser", mailUser);
        return query.getResultList();
    }

    @Override
    public List<String> loadActiveMessageIds(String mailUser, int offset, int limit) {
        return em.createQuery("SELECT distinct m.messageId FROM IN_MAIL_MESSAGE m WHERE m.status<>:status and m.mailUser=:mailUser", String.class)
                .setParameter("status", MessageStatus.ARCHIVE)
                .setParameter("mailUser", mailUser)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public int getActiveMessageIdCount(String mailUser) {
        int count = em.createQuery("SELECT distinct count(distinct m.messageId) FROM IN_MAIL_MESSAGE m WHERE m.status<>:status and m.mailUser=:mailUser", Long.class)
                .setParameter("status", MessageStatus.ARCHIVE)
                .setParameter("mailUser", mailUser)
                .getSingleResult().intValue();

        if (count > 5_000) {
            log.warn("Too many active message ids for mail user: " + mailUser + ", message count: " + count);
        }

        return count;
    }

    @Override
    public List<Object[]> loadMessages(Map<MailLogFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Message> root = query.from(Message.class);
        Join<Message, MessageReply> replyJoin = root.join(Message_.messageReply, JoinType.LEFT);

        query.orderBy(cb.desc(root.get(Message_.receivedDate)));

        Selection[] selections = new Selection[]{
                root.get(Message_.id),
                root.get(Message_.messageId),
                root.get(Message_.mailUser),
                root.get(Message_.address),
                root.get(Message_.from),
                root.get(Message_.status),
                root.get(Message_.receivedDate),
                root.get(Message_.readDate),
                root.get(Message_.note),
                replyJoin.get(MessageReply_.sendStatus)
        };
        query.select(cb.array(selections));

        List<Predicate> predicates = getFilterPredicate(cb, root, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        Query loadQuery = em.createQuery(query);
        if (filter != null) {
            if (filter.get(MailLogFilter.OFFSET) != null) {
                loadQuery.setFirstResult((Integer) filter.get(MailLogFilter.OFFSET));
            }
            if (filter.get(MailLogFilter.LIMIT) != null) {
                loadQuery.setMaxResults((Integer) filter.get(MailLogFilter.LIMIT));
            }
        }
        return loadQuery.getResultList();

    }

    @Override
    public List<MessageReply> loadReplyMessages(Map<MailLogFilter, Object> filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MessageReply> query = cb.createQuery(MessageReply.class);
        Root<MessageReply> root = query.from(MessageReply.class);

        Subquery<Long> messageSubquery = query.subquery(Long.class);
        Root<Message> messageRoot = messageSubquery.from(Message.class);
        messageSubquery.select(messageRoot.get(Message_.messageReply).get(MessageReply_.id));

        messageSubquery.where(cb.isNotNull(messageRoot.get(Message_.messageReply)));

        query.orderBy(cb.desc(root.get(MessageReply_.date)));

        query.select(root);

        List<Predicate> predicates = getReplyFilterPredicate(cb, root, filter);
        predicates.add(cb.not(root.get(MessageReply_.id).in(messageSubquery)));

        query.where(predicates.toArray(new Predicate[0]));

        Query loadQuery = em.createQuery(query);
        if (filter != null) {
            if (filter.get(MailLogFilter.OFFSET) != null) {
                loadQuery.setFirstResult((Integer) filter.get(MailLogFilter.OFFSET));
            }
            if (filter.get(MailLogFilter.LIMIT) != null) {
                loadQuery.setMaxResults((Integer) filter.get(MailLogFilter.LIMIT));
            }
        }


        return loadQuery.getResultList();
    }

    @Override
    public long messageCount(Map<MailLogFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Message> root = query.from(Message.class);

        query.select(cb.count(root.get(Message_.id)));
        List<Predicate> predicates = getFilterPredicate(cb, root, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public long systemMessageReplyCount(Map<MailLogFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MessageReply> root = query.from(MessageReply.class);

        Subquery<Long> messageSubquery = query.subquery(Long.class);
        Root<Message> messageRoot = messageSubquery.from(Message.class);
        messageSubquery.select(messageRoot.get(Message_.messageReply).get(MessageReply_.id));

        query.select(cb.count(root.get(MessageReply_.id)));

        List<Predicate> predicates = getReplyFilterPredicate(cb, root, filter);
        predicates.add(cb.not(root.get(MessageReply_.id).in(messageSubquery)));

        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getSingleResult();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<Message> root, Map<MailLogFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<MailLogFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case ADDRESS:
                            predicates.add(cb.like((root.get(Message_.address)), entry.getValue().toString().trim()));
                            break;
                        case FROM_ADDRESS:
                            predicates.add(cb.like((root.get(Message_.from)), entry.getValue().toString().trim()));
                            break;
                        case MAIL_USER:
                            predicates.add(cb.like((root.get(Message_.mailUser)), entry.getValue().toString().trim()));
                            break;
                        case READ_FROM_DATE:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Message_.receivedDate), (Date) entry.getValue()));
                            break;
                        case READ_TO_DATE:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Message_.receivedDate), (Date) entry.getValue()));
                            break;
                        case RECEIVE_FROM_DATE:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(Message_.readDate), (Date) entry.getValue()));
                            break;
                        case RECEIVE_TO_DATE:
                            predicates.add(cb.lessThanOrEqualTo(root.get(Message_.readDate), (Date) entry.getValue()));
                            break;
                        case STATUS:
                            predicates.add(cb.equal(root.get(Message_.status), MessageStatus.valueOf(entry.getValue().toString().trim())));
                            break;
                        case REPLY_STATUS:
                            predicates.add(cb.equal(root.get(Message_.messageReply).get(MessageReply_.sendStatus), MessageReplySendStatus.valueOf(entry.getValue().toString().trim())));
                            break;
                    }
                }
            }
        }

        return predicates;
    }

    private List<Predicate> getReplyFilterPredicate(CriteriaBuilder cb, Root<MessageReply> root, Map<MailLogFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<MailLogFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case ADDRESS:
                            predicates.add(cb.like((root.get(MessageReply_.from)), entry.getValue().toString().trim()));
                            break;
                        case FROM_ADDRESS:
                            predicates.add(cb.like((root.get(MessageReply_.sender)), entry.getValue().toString().trim()));
                            break;
                        case TO_ADDRESS:
                            predicates.add(cb.like((root.get(MessageReply_.to)), entry.getValue().toString().trim()));
                            break;
                        case MAIL_USER:
                            predicates.add(cb.like((root.get(MessageReply_.from)), entry.getValue().toString().trim()));
                            break;
                        case RECEIVE_FROM_DATE:
                            predicates.add(cb.greaterThanOrEqualTo(root.get(MessageReply_.date), (Date) entry.getValue()));
                            break;
                        case RECEIVE_TO_DATE:
                            predicates.add(cb.lessThanOrEqualTo(root.get(MessageReply_.date), (Date) entry.getValue()));
                            break;
                        case REPLY_STATUS:
                            predicates.add(cb.equal(root.get(MessageReply_.sendStatus), MessageReplySendStatus.valueOf(entry.getValue().toString().trim())));
                            break;
                    }
                }
            }
        }

        return predicates;
    }

    @Override
    public MessageReply loadMessageReplay(long messageId) {
        return em.find(Message.class, messageId).getMessageReply();
    }

    @Override
    public MessageReply loadMessageReplayByReplyId(long replyId) {
        return em.find(MessageReply.class, replyId);
    }

    @Override
    public List<UploadFile> loadUploadFiles(long messageId) {
        return em.createQuery("SELECT MM.uploadFiles FROM IN_MAIL_MESSAGE MM WHERE MM.id=:id").setParameter("id", messageId).getResultList();
    }

    @Override
    public MessageReply createMessageReplyForResponsibleUsers(UploadFile uploadFile) {

        String mailUser = getMailUser();
        if (mailUser == null) {
            return null;
        }

        MessageReply mr = new MessageReply();
        mr.setSubject(MailResponseProcessor.getSubject(uploadFile.getFileName()));

        Collection<User> responsibleUsers = em.createQuery("SELECT DISTINCT u FROM IN_BANKS b, IN(b.users) u WHERE b.code=:bankCode", User.class)
                .setParameter("bankCode", uploadFile.getBankCode())
                .getResultList();

        User uploader = uploadFile.getUser();
        if (uploader.getEmail() != null && !uploader.getEmail().isEmpty() && !responsibleUsers.contains(uploader)) {
            responsibleUsers.add(uploader);
        }

        String[] mailCC = getMailResponseCC();
        String[] responsibleUserMails = getResponsibleUserMails(responsibleUsers);

        if (mailCC == null && responsibleUserMails.length == 0) {
            return null;
        }

        mr.setFrom(mailUser);
        mr.setSender(uploader.getEmail() != null ? uploader.getEmail() : mailUser);

        if (responsibleUserMails.length == 0) {
            mr.setTo(mailCC);
        } else {
            mr.setTo(responsibleUserMails);
            mr.setCc(mailCC);
        }

        mr.setDate(new Date());
        mr.setSendStatus(MessageReplySendStatus.NOT_SENT);

        return mr;
    }

    @Override
    public void resetPasswordReplyMessage(String mailTo, String password, String langCode) throws FinATypeException {
        String mailUser = getMailUser();
        if (mailUser == null) {
            throw new FinATypeException("Mail Properties Are Not Correctly Set!");
        }

        String languageCode = langCode == null || langCode.isEmpty() ? getDefaultLanguageCode() : langCode;
        Map<String, String> bundles = MessagesUtil.loadMessageBundleMap(languageCode);

        MessageReply mr = new MessageReply();

        String mailAddress = getMailAddress();
        if (mailAddress == null) {
            mailAddress = mailUser;
        }

        String mailSubject = "FinA/DCS | Reset Password";
        if (bundles.get("net.fina.dcs.passwordReset.subject") != null) {
            mailSubject = bundles.get("net.fina.dcs.passwordReset.subject");
        }

        mr.setSubject(mailSubject);
        mr.setFrom(mailUser);
        mr.setSender(mailAddress);
        mr.setTo(mailTo);
        mr.setDate(new Date());
        mr.setSendStatus(MessageReplySendStatus.NOT_SENT);
        mr.setContent(generateResetPasswordText(password, bundles));

        em.persist(mr);

    }

    @Override
    public String[] getMailResponseCC() {
        String[] cc = null;
        String ccProperty = propertyLocal.getSystemProperty(PropertyKeys.MAIL_RESPONCE_CC);
        if (!isPropertyNull(ccProperty, PropertyKeys.MAIL_RESPONCE_CC)) {
            cc = ccProperty.split(",|;");
        }
        return cc;
    }

    @Override
    public void archiveMails(List<String> messageIds) {

        int rowsUpdated = 0;
        if (!messageIds.isEmpty()) {
            List<String> messageIdParam = messageIds.stream().map(s -> "'" + s + "'").collect(Collectors.toList());
            rowsUpdated = em.createQuery("UPDATE IN_MAIL_MESSAGE m set m.status=:status WHERE ( " + DBUtil.get().generateConcatenatedInStatementWithIds("m.messageId", messageIdParam) + " )")
                    .setParameter("status", MessageStatus.ARCHIVE)
                    .executeUpdate();
        }

        String curStatus = propertyLocal.getSystemProperty(PropertyKeys.MAIL_LAST_SYNC_STATUS);
        String user = "unknown";
        if (curStatus != null && !curStatus.isEmpty()) {
            user = curStatus.split(";")[0];
        }

        propertyLocal.setSystemProperty(PropertyKeys.MAIL_SYNC, "-1");
        propertyLocal.setSystemProperty(PropertyKeys.MAIL_LAST_SYNC_STATUS,
                CommonUtil.compileMessageWithParams(
                        MessagesUtil.getString("net.fina.mail.sync.updateStatus"),
                        user,
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                        Integer.toString(rowsUpdated)));

    }

    @Override
    public void setMailServiceActiveStatus(String date) {
        propertyLocal.setSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_STATUS_CURR, date, true);
    }

    @Override
    public void setMailServiceLastJobStatus(String date) {
        String startDate = propertyLocal.getSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_STATUS_CURR);
        if (startDate == null || startDate.isEmpty()) {
            startDate = "N/A";
        }
        propertyLocal.setSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_STATUS_LAST, startDate + ";" + date, true);
        propertyLocal.setSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_STATUS_CURR, "", true);
    }

    @Override
    public void setMailServiceNextFireTime(String date) {
        propertyLocal.setSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_NEXT_FIRE_TIME, date);
    }

    private String getMailAddress() {
        String mailAddress = propertyLocal.getSystemProperty(PropertyKeys.MAIL_ADDRESS);
        if (!isPropertyNull(mailAddress, PropertyKeys.MAIL_ADDRESS)) {
            return mailAddress;
        }
        return null;
    }

    private String getStatusesString(int statusId) {
        return MessagesUtil.getString(UploadFileStatus.values()[statusId].getCode());
    }

    private String getDefaultLanguageCode() {
        String defaultLanguageCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);

        if (defaultLanguageCode == null || defaultLanguageCode.trim().equals("")) {
            defaultLanguageCode = DEFAULT_LANGUAGE;
        }
        return defaultLanguageCode;
    }

    private String[] getResponsibleUserMails(Collection<User> responsibleUsers) {
        List<String> responsibleUserMails = responsibleUsers.stream()
                .filter(user -> user.getEmail() != null && !user.getEmail().trim().isEmpty())
                .map(User::getEmail)
                .toList();
        return responsibleUserMails.toArray(new String[0]);
    }

    private String getMailUser() {
        String mailUser = propertyLocal.getSystemProperty(PropertyKeys.MAIL_ADDRESS);
        if (!isPropertyNull(mailUser, PropertyKeys.MAIL_ADDRESS)) {
            return mailUser;
        }

        return null;
    }

    private boolean isPropertyNull(String property, String key) {
        if (property != null && (!property.trim().equals(""))) {
            return false;
        }
        return true;
    }

    private String generateResetPasswordText(String newPass, Map<String, String> bundles) {
        StringBuilder sb = new StringBuilder();

        String header = "Dear Sir/Madam,<br><br> Your password to DCS(Data Collection System) has been successfully reset.";
        if (bundles.get("net.fina.dcs.passwordReset.header") != null) {
            header = bundles.get("net.fina.dcs.passwordReset.header");
        }
        sb.append(header);

        sb.append("<UL>");
        sb.append("<LI>");

        String newPasswordMessage = "New Password : ";
        if (bundles.get("net.fina.dcs.passwordReset.newPassword") != null) {
            newPasswordMessage = bundles.get("net.fina.dcs.passwordReset.newPassword");
        }

        sb.append(newPasswordMessage).append("<b>").append(newPass).append("</b>");
        sb.append("</LI>");
        sb.append("</UL>");
        return sb.toString();
    }
}
