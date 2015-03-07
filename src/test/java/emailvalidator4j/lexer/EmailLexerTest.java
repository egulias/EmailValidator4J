package emailvalidator4j.lexer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void hasToken(String value, TokenInterface expected) {
        EmailLexer lexer = new EmailLexer();
        lexer.lex(value);

        Assert.assertTrue(expected.equals(lexer.getCurrent()));
    }

    @Test
    public void itCanBeMoved() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        lexer.next();

        Assert.assertTrue(lexer.getCurrent().equals(Tokens.AT));
    }

    @Test
    public void itFinishes() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        while (!lexer.isAtEnd()) {
            lexer.next();
        }

        Assert.assertTrue(lexer.isAtEnd());
    }

    @Test
    public void isNextToken() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        Assert.assertTrue(lexer.isNextToken(Tokens.AT));
    }

    @Test
    public void isNotNextToken() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        Assert.assertFalse(lexer.isNextToken(Tokens.SP));
    }

    @Test
    public void isNextTokenOnTheEdge() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        lexer.next();
        lexer.next();
        Assert.assertFalse(lexer.isNextToken(Tokens.get("bar")));
    }

    @Test
    public void isNextTokenBeforeEnd() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        lexer.next();
        Assert.assertTrue(lexer.isNextToken(Tokens.get("bar")));
    }

    @Test
    public void isNextTokenAnyOf() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar");
        Assert.assertTrue(lexer.isNextToken(new ArrayList<TokenInterface>(Arrays.asList(Tokens.AT, Tokens.SP))));
    }

    @Test
    public void tokenExists() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar.com");
        Assert.assertTrue(lexer.find(Tokens.DOT));
    }

    @Test
    public void isResetWhenLexing() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar.com");
        lexer.lex("baz@bar.com");
        Assert.assertTrue(lexer.getCurrent().equals(Tokens.get("baz")));
    }

    @Test
    public void hasPreviousToken() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar.com");
        lexer.next();
        Assert.assertTrue(lexer.getPrevious().equals(Tokens.get("foo")));
    }
}
