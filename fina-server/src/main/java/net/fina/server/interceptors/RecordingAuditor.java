package net.fina.server.interceptors;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import net.fina.common.client.exception.FinATypeException;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("serial")
public class RecordingAuditor implements Serializable {

    private final Logger log = Logger.getLogger(getClass());

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
        boolean logMethodParameters = logMethodParameters(method);
        if (ignore != null && ignore) {
            log.debug("Ignored: " + caller + "Call:" + method + "|P:" + getParametersString(invocationContext.getParameters()));
        } else {
            log.info(caller + "Call:" + method + (logMethodParameters ? "|P:" + getParametersString(invocationContext.getParameters()) : ""));
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
            if (methodDescription == null || methodDescription.isBlank()) {
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
        } catch (jakarta.persistence.OptimisticLockException ex) {
            log.error(caller + ex.getMessage(), ex);
            throw new FinATypeException(ex, FinATypeException.Type.CONCURENT_MODIFICATION);
        } catch (Exception e) {
            log.error(caller + e.getMessage(), e);
            if (e.getClass().getName().startsWith("net.fina")) {
                throw e;
            } else {
                throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
            }
        } catch (Throwable t) {
            log.error(caller + t.getMessage(), t);
            throw new FinATypeException(t, FinATypeException.Type.GENERAL_ERROR);
        }
        return result;
    }

    private String getMethodDescription(Method method) {
        try {
            return method.getAnnotation(LogDescription.class).name();
        } catch (Throwable t) {
            //TODO
        }
        return null;
    }

    private Boolean isMethodDescriptionIgnore(Method method) {
        try {
            return method.getAnnotation(LogDescription.class).ignore();
        } catch (Throwable t) {
            //TODO
        }
        return null;
    }

    private boolean logMethodParameters(Method method) {
        try {
            return method.getAnnotation(LogDescription.class).logMethodParameters();
        } catch (Throwable t) {
            //TODO
        }
        return true;
    }

    private String getParametersString(Object[] params) {
        if (params == null)
            return "null";

        int iMax = params.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Collection) {
                b.append(Collection.class);
            } else if (param instanceof Map) {
                b.append(Map.class);
            } else {
                b.append(param);
            }

            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }

        return "[]";
    }
}
