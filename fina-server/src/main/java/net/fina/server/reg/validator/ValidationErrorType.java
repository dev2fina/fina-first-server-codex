package net.fina.server.reg.validator;

/**
 * Created by baaka
 * Date 10/25/2015.
 */
public enum ValidationErrorType {
    REQUIRED_FIELD("VALUE IS REQUIRED : "),
    COMPARISON("COMPARISON VIOLATION : "),
    DATE_FORMAT("INVALID DATE FORMAT : "),
    NUMBER_FORMAT("INVALID NUMBER FORMAT : "),
    FORMULA("FORMULA VIOLATION : "),
    OTHER("ERROR : "),
    LENGTH("INVALID COLUMN LENGTH ");

    private final String message;

    ValidationErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
