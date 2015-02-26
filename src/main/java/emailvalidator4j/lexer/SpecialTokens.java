package emailvalidator4j.lexer;

public enum SpecialTokens implements TokenInterface {
    AT (new Token("AT", "@"));

    private TokenInterface token;
    SpecialTokens (TokenInterface token) {
        this.token = token;
    }

    public boolean equals (TokenInterface that) {
        return this.token.equals(that);
    }

    public String getName () {
        return this.token.getName();
    }

    public String getText () {
        return this.token.getText();
    }
}
