package emailvalidator4j.lexer;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Tokens implements TokenInterface {
    OPENPARETHESIS("OPENPARETHESIS", "("),
    CLOSEPARENTHESIS("CLOSEPARENTHESIS", ")"),
    LOWERTHAN("LOWERTHAN", "<"),
    GREATERTHAN("GREATERTHAN", ">"),
    OPENBRACKET("OPENBRACKET", "["),
    CLOSEBRACKET("CLOSEBRACKET", "]"),
    COLON("COLON", ":"),
    SEMICOLON("SEMICOLON", ";"),
    AT("AT", "@"),
    BACKSLASH("BACKSLASH", "\\"),
    SLASH("SLASH", "/"),
    COMMA("COMMA", ","),
    DOT("DOT", "."),
    DQUOTE("DQUOTE", "\""),
    HYPHEN("HYPHEN", "-"),
    DOUBLECOLON("DOUBLECOLON", "::"),
    SP("SP", " "),
    HTAB("HTAB", "\t"),
    CR("CR", "\r"),
    LF("LF", "\n"),
    CRLF("CRLF", "\r\n"),
    IPV6TAG("IPV6TAG", "IPv6"),
    OPENQBRACKET("OPENQBRACKET", "{"),
    CLOSEQBRACKET("CLOSEQBRACKET", "}"),
    NUL("NUL", "\0");

    public static final String GENERIC = "GENERIC";
    public static final String INVALID = "INVALID";

    private static final HashMap<String, TokenInterface> tokensMap = new HashMap<String, TokenInterface>();
    private static final Pattern invalidUTF8 = Pattern.compile("\\p{Cc}+",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    private final String name;
    private final String text;

    Tokens(String name, String text) {
        this.name = name;
        this.text = text;
    }

    static {
        for (Tokens tok : Tokens.values()) {
            tokensMap.put(tok.text, tok);
        }
    }

    public static TokenInterface get(String value) {
        final TokenInterface token = tokensMap.get(value);
        if (null != token) {
            return token;
        }

        if (isUTF8Invalid(value)) {
            return new Token(INVALID, value);
        }

        return new Token(GENERIC, value);

    }

    private static boolean isUTF8Invalid(String match) {
        return invalidUTF8.matcher(match).find();
    }

    public boolean equals(TokenInterface that) {
        return this.name.equals(that.getName()) && this.text.equals(that.getText());
    }

    public String getName () {
        return this.name;
    }

    public String getText () {
        return this.text;
    }
}
