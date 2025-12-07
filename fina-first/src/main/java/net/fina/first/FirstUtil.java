package net.fina.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ConstraintRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;

import jakarta.ws.rs.ClientErrorException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FirstUtil {

    private static final Logger log = Logger.getLogger(FirstUtil.class.getName());
    public static final String DATE_FORMAT_LONG_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_SHORT_STRING = "yyyy-MM-dd";

    public static byte[] base64ToBinary(String image) {
        if (image != null) {
            if (image.contains(",")) {
                image = image.substring(image.indexOf(',') + 1);
            }
            return Base64.getDecoder().decode(image);
        }
        return null;
    }

    public static String binaryToBase64(byte[] image) {
        return "data:image/*;base64," + Base64.getEncoder().encodeToString(image);
    }

    public static <T extends Object> T getValue(Object value, Class<T> type) {
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }

    public static List<SortField> getSortFieldsFromParam(String sortJson) {
        try {
            if (sortJson != null && !sortJson.trim().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                SortField[] sortFields = objectMapper.readValue(sortJson, SortField[].class);
                return Arrays.asList(sortFields);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }

    public static SortField getSortFieldFromParam(String groupJson) {
        try {
            if (groupJson != null && !groupJson.trim().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(groupJson, SortField.class);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static void handleClientException(ClientErrorException ex) throws FinATypeException {
        log.error(ex.getMessage(), ex);
        switch (ex.getResponse().getStatus()) {
            case HttpStatus.SC_CONFLICT:
                throw new FinATypeException(ex, FinATypeException.Type.CODE_UNIQUE);
            case HttpStatus.SC_FORBIDDEN:
                throw new FinATypeException(ex, FinATypeException.Type.ECM_NOT_ADMIN_PERMISSION);
            default:
                throw new FinATypeException(ex, FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public static String getPersonDisplayName(PersonRepresentation personRepresentation) {
        String displayName = personRepresentation.getId();
        String firstName = personRepresentation.getFirstName();
        if (firstName != null && !firstName.isEmpty()) {
            String lastName = personRepresentation.getLastName();
            displayName = (lastName != null && !lastName.isEmpty()) ? (firstName + " " + lastName) : firstName;
        }
        return displayName;
    }

    public static List<String> getPropertyListValues(List<ConstraintRepresentation> constraints) {
        List<String> result = new ArrayList<>();
        for (ConstraintRepresentation constraint : constraints) {
            if (constraint.getType().equalsIgnoreCase("LIST")) {
                for (Map<String, Object> parameter : constraint.getParameters()) {
                    if (parameter.get("allowedValues") != null) {
                        result = ((List<String>) parameter.get("allowedValues")).stream().map(String::trim).collect(Collectors.toList());
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static String getModifiedErrorBriefSummeryMessage(String message) {
        String checkPrefix = "couldn't execute event listener : ";
        if (message != null && !message.isEmpty() && message.startsWith(checkPrefix)) {
            message = message.substring(checkPrefix.length());
        }
        return message;
    }

    public static FieldsParam getFieldsParamFromValuesSafe(List<String> fieldValues) {
        FieldsParam result = null;
        if (fieldValues != null && !fieldValues.isEmpty()) {
            result = new FieldsParam(fieldValues);
        }
        return result;
    }

    public static Date getDateValue(String dateValue, String dateFormat) {
        Date date = null;
        DateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (dateValue != null && !dateValue.trim().isEmpty()) {
            try {
                date = df.parse(dateValue);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return date;
    }

    public static Date getDateValue(String dateValue) {
        Date date = null;
        if (dateValue != null && !dateValue.trim().isEmpty()) {
            DateFormat df = new SimpleDateFormat(dateValue.length() != DATE_FORMAT_SHORT_STRING.length() ? DATE_FORMAT_LONG_STRING : DATE_FORMAT_SHORT_STRING);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = df.parse(dateValue);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return date;
    }

    public static LocalDate getLocalDateValue(String dateValue, String dateFormat) {
        LocalDate localDate = null;
        Date date = getDateValue(dateValue, dateFormat);
        if (date != null && dateValue != null && !dateValue.trim().isEmpty()) {
            try {
                localDate = date.toInstant().atZone(TimeZone.getTimeZone("UTC").toZoneId()).toLocalDate();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return localDate;
    }

    public static <T> List<T> getPaginatedList(List<T> list, Integer start, Integer limit) {
        List<T> result = list;

        start = start == null ? 0 : start;
        limit = limit == null ? list.size() : limit;

        int end = Math.min(start + limit, list.size());

        if (start < end && start < list.size()) {
            result = list.subList(start, end);
        }

        return result;
    }

    public static int safeDateCmp(Date d1, Date d2) {
        int result;

        if (d1 == null && d2 == null) {
            result = 0;
        } else if (d1 == null) {
            result = -1;
        } else if (d2 == null) {
            result = 1;
        } else {
            result = d1.compareTo(d2);
        }

        return result;
    }

    public static int safeAlphabeticStringCmp(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        } else if (s1 == null) {
            return -1;
        } else if (s2 == null) {
            return 1;
        }

        return s1.toUpperCase().compareTo(s2.toUpperCase());
    }

    public static String getNodeLocationText(NodeRepresentation nr) {
        String pathName = nr.getPath().getName();
        String result = null;
        if (pathName != null) {
            if (pathName.lastIndexOf("/") == 0) {
                result = "Personal Files";
            } else {
                result = pathName.substring(pathName.lastIndexOf("/") + 1);
            }
        }

        return result;
    }

    public static OrderByParam getOrderByParam(String sort, String group) {
        List<SortField> sortFields = ListUtils.union(
                getSortFieldsFromParam(group != null ? "[" + group + "]" : null),
                getSortFieldsFromParam(sort));

        return new OrderByParam(sortFields.size() > 0 ? sortFields.stream()
                .map(sortField -> sortField.getProperty().replace("_", ":") + " " + sortField.getDirection())
                .collect(Collectors.toList()) : Collections.singletonList("createdAt desc"));
    }

    public static boolean templateWebScriptLocatorEnable() {
        try {
            String enable = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.TEMPLATE_WEB_SCRIPT_LOCATOR_ENABLE);
            return enable != null && !enable.trim().isEmpty() && Boolean.parseBoolean(enable);
        } catch (Exception ex) {
            //ignore
        }

        return false;
    }
}
