package net.fina.ecm.alfresco.api.common.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnexpectedErrorRepresentation extends AbstractRepresentation {

    @JsonProperty("errorKey")
    private String errorKey;

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("briefSummary")
    private String briefSummary;

    @JsonProperty("stackTrace")
    private String stackTrace;

    @JsonProperty("descriptionURL")
    private String descriptionURL;

    @JsonProperty("logId")
    private String logId;

    public UnexpectedErrorRepresentation() {
    }

    public UnexpectedErrorRepresentation(int statusCode, String briefSummary) {
        this.statusCode = statusCode;
        this.briefSummary = briefSummary;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBriefSummary() {
        return briefSummary;
    }

    public void setBriefSummary(String briefSummary) {
        this.briefSummary = briefSummary;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDescriptionURL() {
        return descriptionURL;
    }

    public void setDescriptionURL(String descriptionURL) {
        this.descriptionURL = descriptionURL;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UnexpectedErrorRepresentation {, ");

        sb.append("    errorKey: ").append(toIndentedString(errorKey)).append(", ");
        sb.append("    statusCode: ").append(toIndentedString(statusCode)).append(", ");
        sb.append("    briefSummary: ").append(toIndentedString(briefSummary)).append(", ");
        sb.append("    stackTrace: ").append(toIndentedString(stackTrace)).append(", ");
        sb.append("    descriptionURL: ").append(toIndentedString(descriptionURL)).append(", ");
        sb.append("    logId: ").append(toIndentedString(logId)).append(", ");
        sb.append("}");
        return sb.toString();
    }


}
