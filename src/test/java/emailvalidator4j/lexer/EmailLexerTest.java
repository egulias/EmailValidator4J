package emailvalidator4j.lexer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class EmailLexerTest {
    @DataProvider
    public static Object[][] tokensStringsProvider() {
        return new Object[][] {
                {"@", Tokens.AT},
                {"(", Tokens.OPENPARETHESIS},
                {")", Tokens.CLOSEPARENTHESIS},
                {"<", Tokens.LOWERTHAN},
                {">", Tokens.GREATERTHAN},
                {"[", Tokens.OPENBRACKET},
                {"]", Tokens.CLOSEBRACKET},
                {":", Tokens.COLON},
                {";", Tokens.SEMICOLON},
                {"@", Tokens.AT},
                {"\\", Tokens.BACKSLASH},
                {"/", Tokens.SLASH},
                {",", Tokens.COMMA},
                {".", Tokens.DOT},
                {"\"", Tokens.DQUOTE},
                {"-", Tokens.HYPHEN},
                {"::", Tokens.DOUBLECOLON},
                {" ", Tokens.SP},
                {"\t", Tokens.HTAB},
                {"\r", Tokens.CR},
                {"\n", Tokens.LF},
                {"\r\n", Tokens.CRLF},
                {"IPv6", Tokens.IPV6TAG},
                {"{", Tokens.OPENQBRACKET},
                {"}", Tokens.CLOSEQBRACKET},
                {"\0", Tokens.NUL}
        };
    }

    @Test
    @UseDataProvider("tokensStringsProvider")
    public void lexerHasToken(String value, TokenInterface expected) {
        EmailLexer lexer = new EmailLexer();
        lexer.lex(value);

        Assert.assertTrue(expected.equals(lexer.getCurrent()));
    }
}
