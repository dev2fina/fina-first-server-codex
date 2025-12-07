package net.fina.server.returns.impl;


import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.net.URL;

public class ReturnFreeMarkerTemplate {
    private static volatile ReturnFreeMarkerTemplate _instance;

    private Configuration cfg;

    private ReturnFreeMarkerTemplate() {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setTemplateLoader(new FsopTemplateLoader());
    }

    public static ReturnFreeMarkerTemplate getInstance() {
        if (_instance == null) {
            synchronized (ReturnFreeMarkerTemplate.class) {
                if (_instance == null) {
                    _instance = new ReturnFreeMarkerTemplate();
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
            String templateFile = getClass().getPackage().getName().replace('.', '/') + "/fsop.ftl";
            return getClass().getClassLoader().getResource(templateFile);
        }
    }
}
