package emailvalidator4j.lexer;

public class Token implements TokenInterface {

    private String name;
    private String text;

    public Token(String name, String text) {
        this.name = name;
        this.text = text;
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
