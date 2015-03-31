package emailvalidator4j.parser;

public enum Warnings {
    DEPRECATED_QP("Deprecated Quoted Pard"),
    CFWS_FWS("Folding White Space"),
    COMMENT("Email address with a comment"),
    DEPRECATED_CFWS_NEAR_AT("CFWS near @"),
    RFC5321_QUOTEDSTRING("Quoted string"),
    RFC5321_LOCALPART_TOO_LONG("Local part exceeds max length of 64, otherwise valid from RFC5322 2.1.1"),
    DEPRECATED_COMMENT("Deprecated place for a comment");

    private String message;

    Warnings(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
