package net.fina.first.interceptors;

import net.fina.first.common.exception.*;
import org.jboss.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("serial")
public class FirstRecordingAuditor implements Serializable {

    private Logger log = Logger.getLogger(getClass());

    @Resource
    private SessionContext sc;

    /**
     * Persistently records the intercepted {@link InvocationContext} such that
     * we may examine it later
     */
    @AroundInvoke
    public Object audit(final InvocationContext invocationContext) throws Exception {
        // Precondition checks
        assert invocationContext != null : "Context was not specified";

        //Caller
        String caller = "U:" + sc.getCallerPrincipal().getName() + "|";

        // Running method name
        Method method = invocationContext.getMethod();
        Boolean ignore = isMethodDescriptionIgnore(method);
        if (ignore != null && ignore) {
            log.debug("Ignored: " + caller + "Call:" + method + "|P:" + Arrays.toString(invocationContext.getParameters()));
        } else {
            log.info(caller + "Call:" + method + "|P:" + Arrays.toString(invocationContext.getParameters()));
        }

        // Result
        Object result = null;

        try {
            // Method Processed
            long startTime = System.currentTimeMillis();
            result = invocationContext.proceed();

            if (ignore != null && ignore) {
                return result;
            }

            //Method Description
            String methodDescription = getMethodDescription(method);
            if (methodDescription == null) {
                methodDescription = method.toGenericString();
            }

            if (result instanceof Collection) {
                Collection<?> c = (Collection<?>) result;
                log.info(methodDescription + "RS:" + c.size());
            } else if (result instanceof Map) {
                Map m = (Map) result;
                log.info(caller + methodDescription + "RS:" + m.size());
            } else {
                log.info(caller + methodDescription + "R:" + result);
            }
            log.info(caller + methodDescription + "|SC|WT:" + (System.currentTimeMillis() - startTime) + ".");
        } catch (Throwable t) {
            log.error(caller + t.getMessage(), t);
            throw t;
        }
        return result;
    }

    private String getMethodDescription(Method method) {
        try {
            return method.getAnnotation(FirstLogDescription.class).name();
        } catch (Throwable t) {
            //TODO
        }
        return null;
    }

    private Boolean isMethodDescriptionIgnore(Method method) {
        try {
            return method.getAnnotation(FirstLogDescription.class).ignore();
        } catch (Throwable t) {
            //TODO
        }
        return null;
    }


}
