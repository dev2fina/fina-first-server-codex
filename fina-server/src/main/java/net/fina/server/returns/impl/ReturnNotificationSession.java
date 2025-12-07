package net.fina.server.returns.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.ReturnNotificationLocal;
import net.fina.server.returns.api.ReturnNotificationSender;
import net.fina.server.returns.qualifier.ReturnNotificationQualifierLiteral;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;

@Stateless
@Local(ReturnNotificationLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnNotificationSession implements ReturnNotificationLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private PropertyLocal propertyLocal;

    @Inject
    private BeanManager beanManager;

    @Inject
    @Any
    private Instance<ReturnNotificationSender> senderInstance;


    @Override
    public void sendReturnStatusChangeNotification(List<UploadFile> uploadFiles, ProcessStatus status, String note,  String langCode) {
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return;
        }
        try {

            //retrieve providers and then send message based  on which are active
            String providersString = propertyLocal.getSystemProperty(PropertyKeys.RETURN_NOTIFICATION_PROVIDERS);
            if (providersString == null || providersString.trim().isBlank()) {
                log.info("providers is empty, skipping return status change notification!");
                return;
            }
            log.info("return status notification providers : " + providersString);

            List<String> providers = Arrays.stream(providersString.split(",|;")).toList();

            try {
                String returnStatusBundleText = getReturnStatusBundleText(status);

                for (String provider : providers) {
                    if (isProviderImplemented(provider)) {
                        ReturnNotificationSender sender = senderInstance.select(new ReturnNotificationQualifierLiteral(provider)).get();
                        sender.send(uploadFiles, returnStatusBundleText, note, langCode);
                    } else {
                        log.warn("provider [" + provider + "] implementation does not exist, skipping it!");
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


    }

    private String getReturnStatusBundleText(ProcessStatus processStatus) {
        if (processStatus == ProcessStatus.STATUS_ACCEPTED) {
            return "net.fina.returns.status.accepted";
        } else if (processStatus == ProcessStatus.STATUS_REJECTED) {
            return "fina2.web.rejected";
        }
        return "";
    }

    private boolean isProviderImplemented(String provider) {
        return !beanManager.getBeans(ReturnNotificationSender.class, new ReturnNotificationQualifierLiteral(provider)).isEmpty();
    }
}
