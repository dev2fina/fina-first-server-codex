package net.fina.ecm.alfresco;

import org.jboss.logging.Logger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class LoggingFilter implements ClientRequestFilter, ClientResponseFilter, WriterInterceptor {
    private final Logger logger = Logger.getLogger(getClass());
    private final String ENTITY_STREAM_PROPERTY = "LoggingFilter.entityStream";
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final int maxEntitySize = 1024 * 8;

    private void log(StringBuilder sb) {
        logger.info(sb.toString());
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.hasEntity() && requestContext.getMediaType().getType().equals(MediaType.APPLICATION_JSON)) {
            final OutputStream stream = new LoggingStream(requestContext.getEntityStream());
            requestContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_STREAM_PROPERTY, stream);
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        final StringBuilder sb = new StringBuilder();
        if (responseContext.hasEntity() && responseContext.getMediaType().getType().equals(MediaType.APPLICATION_JSON)) {
            responseContext.setEntityStream(logInboundEntity(sb, responseContext.getEntityStream(),
                    DEFAULT_CHARSET));
            log(sb);
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final LoggingStream stream = (LoggingStream) context.getProperty(ENTITY_STREAM_PROPERTY);
        context.proceed();
        if (stream != null) {
            log(stream.getStringBuilder(DEFAULT_CHARSET));
        }
    }

    private class LoggingStream extends FilterOutputStream {

        private final StringBuilder sb = new StringBuilder();
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(OutputStream out) {
            super(out);
        }

        StringBuilder getStringBuilder(Charset charset) {
            // write entity to the builder
            final byte[] entity = baos.toByteArray();

            sb.append(new String(entity, 0, entity.length, charset));
            if (entity.length > maxEntitySize) {
                sb.append("...more...");
            }
            sb.append('\n');

            return sb;
        }

        @Override
        public void write(final int i) throws IOException {
            if (baos.size() <= maxEntitySize) {
                baos.write(i);
            }
            out.write(i);
        }
    }
}
