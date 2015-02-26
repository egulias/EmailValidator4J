package emailvalidator4j.lexer;

import java.util.HashMap;

public enum Tokens implements TokenInterface {
    SLASH("SLASH", "/"),
    AT("AT", "@"),
    OPENPARETHESIS("OPENPARENTHESIS", "(");

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
