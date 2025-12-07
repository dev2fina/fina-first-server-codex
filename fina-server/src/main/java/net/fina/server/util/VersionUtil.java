package net.fina.server.util;

import net.fina.common.shared.Version;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {
    private static final Logger log = Logger.getLogger(VersionUtil.class.getName());

    public static Version getVersion() {
        Version version = new Version();
        String property = version.getClass().getPackage().getName().replace('.', '/') + "/version.properties";
        try (InputStream in = version.getClass().getClassLoader().getResourceAsStream(property)) {
            Properties properties = new Properties();
            properties.load(in);
            String finaVersion = properties.getProperty("fina.version", version.getFinaVersion());
            version.setFinaVersion(finaVersion);
            version.setFullVersion(finaVersion);
//            Date notAfter = Product.getInstance().getNotAfter();
//            if (notAfter != null) {
//                Random random=new Random();;
//                byte[] array = new byte[6];
//                //populate prefix array
//                random.nextBytes(array);
//                String prefix = new String(array, StandardCharsets.UTF_8);
//                //populate suffix array
//                random.nextBytes(array);
//                String suffix = new String(array, StandardCharsets.UTF_8);
//
//
//                StringBuilder fullVersionBuilder = new StringBuilder(finaVersion);
//                fullVersionBuilder.append(".")
//                        .append(prefix)
//                        .append(Long.toBinaryString(notAfter.getTime()))
//                        .append(suffix);
//
//                System.out.println(fullVersionBuilder);
//            }
//            String fullVersion = finaVersion;

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return version;
    }

}
