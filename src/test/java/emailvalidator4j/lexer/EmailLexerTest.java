package emailvalidator4j.lexer;

import org.junit.Assert;
import org.junit.Test;

public class EmailLexerTest {
    @Test
    public void lexerHasToken() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("@");

        Assert.assertTrue(Tokens.AT.equals(lexer.getCurrent()));
    }
}
