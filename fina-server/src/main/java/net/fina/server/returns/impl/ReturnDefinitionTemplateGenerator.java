package net.fina.server.returns.impl;

import net.fina.common.client.property.PropertyKeys;
import net.fina.server.returns.api.ReturnDefinitionTemplateRepositoryLocal;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.inject.Inject;

@Startup
@Singleton
public class ReturnDefinitionTemplateGenerator {

    @Inject
    private Logger log;

    @EJB
    private PropertyLocal propertyLocal;

    @EJB
    private ReturnDefinitionTemplateRepositoryLocal returnDefinitionTemplateRepositoryLocal;

    @Resource(lookup = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory threadFactory;

    @PostConstruct
    public void autoStart() {
        String prop = propertyLocal.getSystemProperty(PropertyKeys.RETURN_DEFINITION_TEMPLATE_GENERATION_ENABLE);
        try {
            if (Integer.parseInt(prop) > 0) {
                Runnable run = () -> {
                    log.info("Start templates generation");
                    returnDefinitionTemplateRepositoryLocal.storePackageTemplates();
                };
                threadFactory.newThread(run).start();
            }
        } catch (Exception e) {
            log.error("Please provide integer value to property: " + PropertyKeys.RETURN_DEFINITION_TEMPLATE_GENERATION_ENABLE);
        }
    }
}
