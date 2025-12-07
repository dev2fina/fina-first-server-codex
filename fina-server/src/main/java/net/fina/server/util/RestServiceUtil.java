package net.fina.server.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.server.util.RestUtil;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.server.dcs.uploadfile.api.UploadFileStreamable;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.jcr.impl.FileContentManagementSession;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestServiceUtil {
    private static final String DEFAULT_LANG_CODE = "en_US";
    private static final String LOCALE_PARAM_NAME = "locale";

    private final static Logger log = Logger.getLogger(RestServiceUtil.class.getName());

    public static String parseParam(List<InputPart> inputParts) throws IOException {
        String result = null;
        if (inputParts != null) {
            for (InputPart inputPart : inputParts) {
                InputStream is = inputPart.getBody(InputStream.class, null);
                byte[] content = IOUtils.toByteArray(is);
                result = new String(content, StandardCharsets.UTF_8);
            }
        }
        return result;
    }


    public static Response getFileResponse(FileContentManagementSession fileContentManagementSession, UploadFile uploadFile, String fileName) {
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

        StreamingOutput streamingOutput = output -> {
            fileContentManagementSession.loadUploadFileStreamInto(uploadFile, new UploadFileStreamable() {
                @Override
                public void readFileStream(InputStream inputStream) throws Exception {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    try (InputStream input = inputStream) {
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        output.flush();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });

        };

        return Response.ok(streamingOutput)
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }


    public static Response getFileResponse(final byte[] content, String fileName) {

        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        StreamingOutput output = outputStream -> outputStream.write(content);

        String mediaType = MediaType.APPLICATION_OCTET_STREAM;

        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.type(mediaType).entity(output);
        responseBuilder.header("content-disposition", "attachment; filename=\"" + fileName + "\"");

        return responseBuilder.build();
    }

    public static Response reviewFile(byte[] content, String contentType, Map<String, String> headers) {
        StreamingOutput output = outputStream -> outputStream.write(content == null ? new byte[0] : content);

        jakarta.ws.rs.core.Response.ResponseBuilder response = jakarta.ws.rs.core.Response.ok();
        response.type(contentType).entity(output);
        headers.forEach(response::header);

        return response.build();
    }

    public static MultipartFormDataOutput getMdoFromMultipartForm(MultipartFormDataInput multipartForm, String fileName) {
        try {
            MultipartFormDataOutput mdo = new MultipartFormDataOutput();
            for (Map.Entry<String, List<InputPart>> inputPartEntry : multipartForm.getFormDataMap().entrySet()) {
                String partId = inputPartEntry.getKey();
                List<InputPart> inputParts = inputPartEntry.getValue();

                for (InputPart part : inputParts) {
                    InputStream inputStream;
                    if (partId.equals(APIConstants.MULTIPART_FILE_DATA)) {
                        inputStream = part.getBody(InputStream.class, null);
                        OutputPart objPart = mdo.addFormData(partId, inputStream, part.getMediaType());
                        mdo.addFormData("name", fileName, MediaType.TEXT_PLAIN_TYPE);
                        objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=" + partId + "; filename=" + (fileName != null && !fileName.trim().isEmpty() ? fileName : RestServiceUtil.getFileName(part.getHeaders())));
                    } else {
                        mdo.addFormData(partId, part.getBodyAsString(), part.getMediaType());
                    }
                }
            }
            return mdo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean isEcmEnabled() {
        try {
            String enableEcm = ConfigurationUtil.get().get("ECM.enable");
            return enableEcm != null && (!enableEcm.isEmpty()) && Integer.parseInt(enableEcm) > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static String getFileName(MultivaluedMap<String, String> header) throws Exception {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = URLDecoder.decode(name[1].trim().replaceAll("\"", ""), StandardCharsets.UTF_8.name());
                return finalFileName;
            }
        }
        return "unknown";
    }

    public static Map<String, Object> getFilterMap(String filter) {
        try {
            if (filter != null) {
                return new ObjectMapper().readValue(filter, new TypeReference<HashMap<String, Object>>() {
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    public static String getLocaleFromRequest(HttpServletRequest request) {
        return getLocaleFromRequest(request, LOCALE_PARAM_NAME);
    }

    public static String getLocaleFromRequest(HttpServletRequest request, String localeParamName) {
        String locale = request.getParameter(localeParamName);

        if ((locale == null) || locale.trim().isEmpty()) {
            if (request.getCookies() != null) {
//                log.info("Could not get locale parameter");
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(localeParamName)) {
                        locale = cookie.getValue();
                        break;
                    }
                }
                if (locale == null) {
                    locale = RestUtil.getLanguageCodeFromRequest(request);
                }
//                log.info("Get Cookie '" + localeParamName + "'");
            } else {
                locale = RestUtil.getLanguageCodeFromRequest(request);
            }
        }

        locale = locale == null ? DEFAULT_LANG_CODE : locale;

//        log.info("Locale from Cookie - " + locale);

        return locale;
    }

}
