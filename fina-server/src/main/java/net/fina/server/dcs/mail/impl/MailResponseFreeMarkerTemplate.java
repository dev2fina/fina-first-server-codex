package net.fina.server.dcs.mail.impl;


import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.net.URL;

public class MailResponseFreeMarkerTemplate {
    private static volatile MailResponseFreeMarkerTemplate _instance;

    private Configuration cfg;

    private MailResponseFreeMarkerTemplate() {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setTemplateLoader(new FsopTemplateLoader());
    }

    public static MailResponseFreeMarkerTemplate getInstance() {
        if (_instance == null) {
            synchronized (MailResponseFreeMarkerTemplate.class) {
                if (_instance == null) {
                    _instance = new MailResponseFreeMarkerTemplate();
                }
            }
        }
        return _instance;
    }

    public Template getTemplate() throws IOException {
        return cfg.getTemplate("fsop.ftl");
    }

    static class FsopTemplateLoader extends URLTemplateLoader {
        @Override
        protected URL getURL(String name) {
            String templateFile = getClass().getPackage().getName().replace('.', '/') + "/mail_response_template.ftl";
            return getClass().getClassLoader().getResource(templateFile);
        }
    }
}
