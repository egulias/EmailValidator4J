package emailvalidator4j.parser;

public enum Warnings {
    DEPRECATED_QP("Deprecated Quoted Pard"),
    CFWS_FWS("Folding White Space"),
    COMMENT("Email address with a comment"),
    DEPRECATED_CFWS_NEAR_AT("CFWS near @");

    private String message;

    Warnings(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
