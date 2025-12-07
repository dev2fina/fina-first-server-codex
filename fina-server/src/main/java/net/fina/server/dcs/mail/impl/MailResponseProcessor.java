package net.fina.server.dcs.mail.impl;

import freemarker.template.Template;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.messages.MessagesUtil;
import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.mail.model.MailResponseImportedXmlMetaModel;
import net.fina.server.dcs.mail.model.MailResponseMetaModel;
import net.fina.server.dcs.mail.model.MailResponseUploadFileMetaModel;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import org.jboss.logging.Logger;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailResponseProcessor {

    public static final String DEFAULT_LANGUAGE = "en_US";
    public static final String DEFAULT_RESPONCE = "Dear Sir/Madam,   e-mail sent by you had unexpected problem(s). Please contact DCS Team.";
    public static final String ERROR_REASON = "net.fina.dcs.mailRespomce.errorReason";
    public static final String RESPONCE_MESSAGE_SUBJECT = "[DCS] Re: ";
    public static final int SUBJECT_MAX_SIZE = 100;
    private static final String MESSAGE_CODE_REGEX = "\\$\\{(.+?)\\}";
    private final ResourceBundle resourceBundle;
    private Logger log = Logger.getLogger(getClass());

    public MailResponseProcessor(String languageCode) {
        this.resourceBundle = MessagesUtil.loadMessageBundle(languageCode);

    }

    public static String getSubject(String originalSubject) {
        if (originalSubject == null) {
            originalSubject = "";
        }

        StringBuilder sb = new StringBuilder(RESPONCE_MESSAGE_SUBJECT + originalSubject);
        if (sb.length() > SUBJECT_MAX_SIZE) {
            sb.replace(SUBJECT_MAX_SIZE - 4, sb.length(), "...");
        }

        return sb.toString();
    }

    private static List<String> getMatches(String input, String pattern) {
        List<String> matches = new ArrayList<>();
        Pattern regexPattern = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regexPattern.matcher(input);

        // Find all matches in the input string
        while (matcher.find()) {
            matches.add(matcher.group(0));
        }

        return matches;
    }

    public String process(Message message) {
        return processTemplate(createModel(message.getUploadFiles(), null, message.getReceivedDate(), message.getReadDate()));
    }

    public String process(Message message, Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns) {
        return processTemplate(createModel(message.getUploadFiles(), uploadFileImportedReturns, message.getReceivedDate(), message.getReadDate()));
    }

    public String process(UploadFile uploadFile, Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns) {
        return processTemplate(createModel(Collections.singletonList(uploadFile), uploadFileImportedReturns, null, null));
    }

    private MailResponseMetaModel createModel(Collection<UploadFile> uploadFiles, Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns, Date receivedDate, Date sentDate) {
        MailResponseMetaModel model = new MailResponseMetaModel();

        model.setHeader(getI18nString("fina2.dcs.mail.responce.header"));
        model.setFooter(getI18nString("fina2.dcs.mail.responce.footer"));
        model.setErrorMail(getI18nString("fina2.dcs.mail.responce.errorMail"));
        model.setReceived(getI18nString("net.fina.dcs.mail.sent"));
        model.setSent(getI18nString("net.fina.dcs.mail.received"));

        model.setReceivedDate(receivedDate);
        model.setSentDate(sentDate);

        List<MailResponseUploadFileMetaModel> files = new ArrayList<>();
        model.setFiles(files);

        List<MailResponseUploadFileMetaModel> errorFiles = new ArrayList<>();
        model.setErrorFiles(errorFiles);

        for (UploadFile uf : uploadFiles) {

            MailResponseUploadFileMetaModel mailResponseUploadFileMetaModel = new MailResponseUploadFileMetaModel();
            mailResponseUploadFileMetaModel.setFileName(uf.getFileName());

            UploadFileStatus status = UploadFileStatus.values()[Integer.parseInt(uf.getStatus())];
            mailResponseUploadFileMetaModel.setStatus(getI18nString(status.getCode()));

            if ((status != UploadFileStatus.UPLOADED_CONVERTED) && (status != UploadFileStatus.CONVERTED) && (status != UploadFileStatus.IMPORTED)) {

                String reason = resourceBundle.getString(ERROR_REASON);

                if (UploadFileStatus.MATRIX_ERROR.getCode().equals(status.getCode())) {
                    mailResponseUploadFileMetaModel.setStatus(getI18nString("net.fina.dcs.converter.error"));
                }

                if (uf.getReason() != null && !uf.getReason().trim().isEmpty()) {
                    //Parse reason
                    StringBuilder text = new StringBuilder();
                    for (String r : uf.getReason().split(";")) {
                        if (!r.trim().isEmpty()) {
                            String messageText = compileMessage(r);
                            if (messageText == null) {
                                messageText = r;
                            }
                            text.append(messageText);
                            text.append(". ");
                        }
                    }
                    reason = text.toString();
                }

                mailResponseUploadFileMetaModel.setReason(reason);

                errorFiles.add(mailResponseUploadFileMetaModel);
            }

            files.add(mailResponseUploadFileMetaModel);

            //Process imported returns
            if (uploadFileImportedReturns != null) {
                List<FsopImportedReturnMetaModel> importedReturns = uploadFileImportedReturns.get(uf.getId());
                if (importedReturns != null) {
                    List<MailResponseImportedXmlMetaModel> xmls = new ArrayList<>();
                    for (FsopImportedReturnMetaModel ir : importedReturns) {
                        MailResponseImportedXmlMetaModel xml = new MailResponseImportedXmlMetaModel();
                        xml.setMessage(ir.getMessage());
                        xml.setReturnCode(ir.getReturnCode());
                        xmls.add(xml);
                    }

                    //Sort file xmls
                    Collections.sort(xmls, (o1, o2) -> {
                        if (o1 != null && o2 != null && o1.getReturnCode() != null && o2.getReturnCode() != null) {
                            return o1.getReturnCode().compareTo(o2.getReturnCode());
                        }
                        return 0;
                    });

                    mailResponseUploadFileMetaModel.setXmls(xmls);
                }
            }
        }
        return model;
    }

    private String processTemplate(MailResponseMetaModel model) {

        try {
            Template template = MailResponseFreeMarkerTemplate.getInstance().getTemplate();

            // Create the root hash
            Map<String, Object> root = new HashMap<>();
            root.put("data", model);
            root.put("title", "DCS Mail");

            try (StringWriter stringWriter = new StringWriter()) {
                template.process(root, stringWriter);
                return stringWriter.toString();
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            return DEFAULT_RESPONCE;
        }
    }

    private String compileMessage(String sourcestring) {
        List<String> matches = getMatches(sourcestring, MESSAGE_CODE_REGEX);

        if (matches.size() > 0) {
            for (String matche : matches) {
                sourcestring = sourcestring.replace(matche, getMessage(matche));
            }
        } else {
            String message = getI18nString(sourcestring);
            if (message != null) {
                sourcestring = message;
            }
        }
        return sourcestring;
    }

    private String getMessage(String code) {
        String tmp = code.replace("${", "").replace("}", "");

        String message = getI18nString(tmp);

        if (message == null) {
            message = getI18nString(code);
            if (message == null) {
                message = code;
            }
        }

        return message;
    }

    private String getI18nString(String key) {
        String value;
        try {
            value = this.resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            value = key;
        }
        return value;
    }

}
