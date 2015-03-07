package emailvalidator4j.lexer;

import org.junit.Assert;
import org.junit.Test;

public class TokensTest {
    @Test
    public void tokenIsEqualToSpecial() {
        Token token = new Token("AT", "@");
        Assert.assertTrue(Tokens.AT.equals(token));
    }

    @Test
    public void tokenIsRetrieved() {
        Token token = new Token("AT", "@");
        Assert.assertTrue(token.equals(Tokens.get("@")));
    }

    @Test
    public void tokenGenericTokenIsReturnedWhenNoTokenFound() {
        Token token = new Token("GENERIC", "text");
        Assert.assertTrue(token.equals(Tokens.get("text")));
    }
}
