package net.fina.server.st;

import net.fina.server.st.api.TemplateException;
import net.fina.server.st.impl.TemplateSession;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class OstVersionTest {

    @Test
    public void test() throws TemplateException {
        System.setProperty("fina.config.dir", "/home/nikoloz/Dev/FinA/AppServer/jboss-eap-6.1/standalone/configuration");

        TemplateSession templateSession = new TemplateSession();
        System.out.println(templateSession.getOstVersion());
    }
}
