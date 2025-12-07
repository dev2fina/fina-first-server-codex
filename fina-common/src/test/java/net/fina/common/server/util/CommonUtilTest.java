package net.fina.common.server.util;

import jakarta.security.jacc.PolicyContext;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonUtilTest {

    @Test
    public void shouldReturnClientIpFromXForwardedFor() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("X-FORWARDED-FOR")).thenReturn("203.0.113.42");

        try (MockedStatic<PolicyContext> context = Mockito.mockStatic(PolicyContext.class)) {
            context.when(() -> PolicyContext.getContext(HttpServletRequest.class.getName()))
                    .thenReturn(mockRequest);

            String ip = CommonUtil.getCurrentClientIpAddress();
            assertEquals("203.0.113.42", ip);
        }
    }

    @Test
    public void shouldReturnClientIpFromRemoteAddr() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("X-FORWARDED-FOR")).thenReturn(null);
        when(mockRequest.getRemoteAddr()).thenReturn("10.0.0.5");

        try (MockedStatic<PolicyContext> context = Mockito.mockStatic(PolicyContext.class)) {
            context.when(() -> PolicyContext.getContext(HttpServletRequest.class.getName()))
                    .thenReturn(mockRequest);

            String ip = CommonUtil.getCurrentClientIpAddress();
            assertEquals("10.0.0.5", ip);
        }
    }


    @Test
    public void shouldReturnClientIpFromThreadName() {
        String threadName = "worker-12-thread-3 192.168.1.100";
        Thread current = Thread.currentThread();
        String oldName = current.getName();
        try {
            current.setName(threadName);
            try (MockedStatic<PolicyContext> context = Mockito.mockStatic(PolicyContext.class)) {
                context.when(() -> PolicyContext.getContext(HttpServletRequest.class.getName()))
                        .thenReturn(null);

                String ip = CommonUtil.getCurrentClientIpAddress();
                assertEquals("192.168.1.100", ip);
            }
        } finally {
            current.setName(oldName);
        }
    }

}
