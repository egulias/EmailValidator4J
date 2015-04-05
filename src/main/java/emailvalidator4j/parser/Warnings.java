package emailvalidator4j.parser;

public enum Warnings {
    DEPRECATED_QP("Deprecated Quoted Pard"),
    CFWS_FWS("Folding White Space"),
    COMMENT("Email address with a comment"),
    DEPRECATED_CFWS_NEAR_AT("CFWS near @"),
    RFC5321_QUOTEDSTRING("Quoted string"),
    RFC5321_LOCALPART_TOO_LONG("Local part exceeds max length of 64, otherwise valid from RFC5322 2.1.1"),
    RFC5321_ADDRESS_LITERAL("Found address literal"),
    RFC5321_IPV6_DEPRECATED("Found deprecated address literal"),
    RFC5322_IPV6_MAX_GROUPS("Max groups reached"),
    RFC5322_IPV6_DOUBLE_COLON("Double :: in address literal"),
    RFC5322_DOMAIN_LITERAL_OBSOLETE_DTEXT("Obsolete DTEXT in domain literal"),
    DEPRECATED_COMMENT("Deprecated place for a comment");

    private String message;

    Warnings(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
