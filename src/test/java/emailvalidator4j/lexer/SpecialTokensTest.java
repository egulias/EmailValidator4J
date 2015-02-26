package emailvalidator4j.lexer;

import org.junit.Assert;
import org.junit.Test;

public class SpecialTokensTest {
    @Test
    public void tokenIsEqualToSpecial() {
        Token token = new Token("AT", "@");
        Assert.assertTrue(SpecialTokens.AT.equals(token));
    }
}
