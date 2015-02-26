package emailvalidator4j.lexer;

import java.util.HashMap;

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

    private static final HashMap<String, TokenInterface> tokensMap = new HashMap<String, TokenInterface>();

    private final String name;
    private final String text;

    private Tokens(String name, String text) {
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
        return token != null ? token : new Token(value, value);
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
