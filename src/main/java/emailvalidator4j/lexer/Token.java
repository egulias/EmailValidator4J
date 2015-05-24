package emailvalidator4j.lexer;

public class Token implements TokenInterface {

    private String name;
    private String text;

    public Token(String name, String text) {
        this.name = name;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || !TokenInterface.class.isInstance(o)) return false;
        TokenInterface token = (TokenInterface) o;

        return this.name.equals(token.getName());
    }

    public String getName () {
        return this.name;
    }

    public String getText () {
        return this.text;
    }
}
