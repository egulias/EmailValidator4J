package emailvalidator4j.parser;

public enum Warnings {
    DEPRECATED_QP("Deprecated Quoted Part"),
    CFWS_FWS("Folding White Space"),
    COMMENT("Email address with a comment"),
    DEPRECATED_CFWS_NEAR_AT("CFWS near @"),
    RFC5321_QUOTEDSTRING("Quoted string"),
    RFC5321_LOCALPART_TOO_LONG("Local part exceeds max length of 64, otherwise valid from RFC5322 2.1.1"),
    RFC5321_ADDRESS_LITERAL("Found address literal"),
    RFC5322_DOMAIN_LITERAL("Found domain literal"),
    RFC5321_IPV6_DEPRECATED("Found deprecated address literal"),
    RFC5322_IPV6_MAX_GROUPS("Max groups reached"),
    RFC5322_IPV6_DOUBLE_COLON("Double :: in address literal"),
    RFC5322_DOMAIN_LITERAL_OBSOLETE_DTEXT("Obsolete DTEXT in domain literal"),
    RFC5322_IPV6_START_WITH_COLON("Additional colon after IPv6 tag"),
    RFC5322_IPV6_END_WITH_COLON("Colon found at the end"),
    RFC5322_IPV6_BAD_CHAR("Literal contains an invlaid char"),
    RFC5322_IPV6_GROUP_COUNT("Unexpected number of groups"),
    RFC5321_DOMAIN_TOO_LONG("Domain exceeds 255 maximum length"),
    RFC1035_LABEL_TOO_LONG("Domain label exceeds 63 maximum length"),
    DEPRECATED_COMMENT("Deprecated place for a comment");

    private String message;

    Warnings(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
