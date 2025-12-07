package net.fina.server.misc;

import org.jboss.logging.Logger;

import java.lang.reflect.Field;

public class ObjectUtil {
    private static final Logger log = Logger.getLogger(ObjectUtil.class);

    public static void copyProperties(Object source, Object dest) {
        if (source != null && dest != null) {
            convert(source, dest);
        }
    }

    private static void convert(Object source, Object dest) {
        try {
            Class<?> destClass = dest.getClass();
            Class<?> sourceClass = source.getClass();
            for (Field sf : sourceClass.getDeclaredFields()) {
                sf.setAccessible(true);
                Field df = null;

                try {
                    df = destClass.getDeclaredField(sf.getName());
                } catch (NoSuchFieldException ex) {
                    // TODO
                }

                if (df != null && df.getType().equals(sf.getType())) {
                    df.setAccessible(true);
                    Object value = sf.get(source);
                    df.set(dest, value);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
