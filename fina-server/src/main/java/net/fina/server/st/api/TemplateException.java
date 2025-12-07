package net.fina.server.st.api;

public class TemplateException extends Exception {

    /**
     * Exception types
     */
    public enum Reason {

        /* Reason instance */
        UNEXPECTED_EXCEPTION("net.fina.templateException.unexpectedException"),
        DAMAGED_CERTIFICATE("net.fina.templateException.unexpectedException"),
        UNIDENTIFIED_CERTIFICATE("net.fina.templateException.unidentifiedException");

        private final String messageUrl;

        /**
         * Creates the instance of the type
         */
        private Reason(String messageUrl) {
            this.messageUrl = messageUrl;
        }

        public String getCode() {
            return messageUrl;
        }
    }

    /**
     * A type of current exception
     */
    private Reason reason;
    private String[] params;

    /**
     * Default Constructor
     */
    public TemplateException() {
    }

    /**
     * @param message
     */
    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Reason reason) {
        this.reason = reason;
    }

    /**
     * Creates the exception of given type
     */
    public TemplateException(Throwable throwable, Reason reason) {
        super(throwable);
        this.reason = reason;
    }

    /**
     * Creates the exception of given type and parameters
     */
    public TemplateException(Throwable throwable, Reason reason, String[] params) {
        super(throwable);
        this.reason = reason;
        this.params = params;
    }

    public TemplateException(Reason reason, String[] params) {
        this.reason = reason;
        this.params = params;
    }

    /**
     * Returns the exception message url
     */
    public String getMessageUrl() {
        return reason.messageUrl;
    }

    public Reason getType() {
        return this.reason;
    }

    public String[] getParams() {
        return params;
    }
}