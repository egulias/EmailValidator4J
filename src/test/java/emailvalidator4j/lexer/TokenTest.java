package emailvalidator4j.lexer;

import org.junit.Assert;
import org.junit.Test;

public class TokenTest {

    @Test
    public void tokenEqualsToken() {
        Token token1 = new Token("token1", "token1");
        Token token2 = new Token("token1", "token1");

        Assert.assertTrue(token1.equals(token2));
    }

    @Test
    public void tokenWithSameValueNotEqualsToken() {
        Token token1 = new Token("token1", "token1");
        Token token2 = new Token("token2", "token1");

        Assert.assertFalse(token1.equals(token2));
    }

    @Test
    public void tokenWithSameNameEqualsToken() {
        Token token1 = new Token("token1", "token1");
        Token token2 = new Token("token1", "token2");

        Assert.assertTrue(token1.equals(token2));
    }
}
