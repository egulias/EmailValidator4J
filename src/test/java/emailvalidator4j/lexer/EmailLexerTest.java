package emailvalidator4j.lexer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import emailvalidator4j.lexer.exception.TokenNotFound;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(DataProviderRunner.class)
public class EmailLexerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @DataProvider
    public static Object[][] tokensStringsProvider() {
        return new Object[][]{
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

    @Test
    public void moveToExistingToken() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar.com");
        lexer.moveTo(Tokens.AT);
        Assert.assertTrue(lexer.getCurrent().equals(Tokens.AT));
    }

    @Test
    public void moveToUnexistingToken() throws TokenNotFound {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foobar.com");
        exception.expect(TokenNotFound.class);
        lexer.moveTo(Tokens.AT);
    }

    @Test
    public void toStringReturnsLeftTokens() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("foo@bar.com");
        lexer.moveTo(Tokens.AT);
        Assert.assertTrue("@bar.com".equals(lexer.toString()));
    }

    @Test
    @UseDataProvider("atextExamples")
    public void atextParsing(String atext) {
        EmailLexer lexer = new EmailLexer();
        lexer.lex(atext);
        TokenInterface token = lexer.getCurrent();
        Assert.assertEquals(Tokens.GENERIC, token.getName());
        Assert.assertEquals(atext, token.getText());

    }

    @Test
    @UseDataProvider("invalidUTF8StringsProvider")
    public void invalidUTF8CharsAreInvalidTokens(String utf8String) throws Exception {
        EmailLexer lexer = new EmailLexer();
        lexer.lex(utf8String.concat("@bar.com"));
        Assert.assertTrue(
                utf8String.concat("@bar.com") + " has INVALID tokens",
                lexer.getCurrent().equals(Tokens.get(utf8String))
        );
    }

    /*
     *    atext           =   UTF8-non-ascii /
     *                        ALPHA / DIGIT /    ; Printable US-ASCII
     *                        "!" / "#" /        ;  characters not including
     *                        "$" / "%" /        ;  specials.  Used for atoms.
     *                        "&" / "'" /
     *                        "*" / "+" /
     *                        "-" / "/" /
     *                        "=" / "?" /
     *                        "^" / "_" /
     *                        "`" / "{" /
     *                        "|" / "}" /
     *                        "~"
     */
    @DataProvider
    public static Object[][] atextExamples() {
        return new Object[][] {
                {"a"},
                {"1"},
                {"!"},
                {"\uD83D"},
                {"aa"},
                {"a1"},
                {"a!"},
                {"a\uD83D"},
                {"aaa"},
                {"a1a"},
                {"a!a"},
                {"a\uD83D!"},
                {"aaa\uD83D"},
                {"a1a\uD83D"},
                {"a!a\uD83D"},
                {"a\uD83Da\uD83D"},
        };
    }

    @DataProvider
    public static Object[][] invalidUTF8StringsProvider() throws Exception {
        ArrayList<ArrayList<String>> invalidStrings = new ArrayList<>();
        Pattern pattern1 = Pattern.compile("(?=\\p{Cc})(?=[^\\t\\n\\n\\r])",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("\\x{0000}", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        for (int i = 0; i < 0x100; i++) {
            String utf8String = utf8Char(i);
            Matcher matcher1 = pattern1.matcher(utf8String);
            Matcher matcher2 = pattern2.matcher(utf8String);
            if (matcher1.find() && !matcher2.find()){
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(utf8String);
                invalidStrings.add(tmp);
            }
        }
        String[][] array = new String[invalidStrings.size()][];
        for (int i = 0; i < invalidStrings.size(); i++) {
            ArrayList<String> row = invalidStrings.get(i);
            array[i] = row.toArray(new String[row.size()]);
        }

        return array;
    }

    private static String utf8Char(int codePoint) throws Exception {
        String utf8String = "";

        if (codePoint < 0 || 0x10FFFF < codePoint || (0xD800 <= codePoint && codePoint <= 0xDFFF)) {
            return  utf8String;
        } else if (codePoint < 0x80) {
            byte b[] = {(byte) codePoint};
            utf8String = new String(b, StandardCharsets.UTF_8);
        } else if (codePoint < 0x800) {
            byte b[] = {(byte) (0x1C0 | codePoint >> 6), (byte) (0x80 | codePoint & 0x3F)};
            utf8String = new String(b, StandardCharsets.UTF_8);
        } else if (codePoint < 0x10000) {
            byte b[] = {(byte)(0xE0 | codePoint >> 12), (byte)(0x80 | codePoint >> 6 & 0x3F), (byte)(0x80 | codePoint & 0x3F)};
            utf8String = new String(b, StandardCharsets.UTF_8);
        } else {
            byte b[] = {
                    (byte)(0xF0 | codePoint >> 18),
                    (byte)(0x80 | codePoint >> 12 & 0x3F),
                    (byte)(0x80 | codePoint >> 6 & 0x3F),
                    (byte)(0x80 | codePoint  & 0x3F)
            };
            utf8String = new String(b, StandardCharsets.UTF_8);
        }
        return utf8String;
    }

    @Test
    public void hasInvalidTokens() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex((char) 1 + "@bar.com");
        Assert.assertTrue(lexer.hasInvalidTokens());
    }

    @Test
    public void buildTextFromTokensUntilCurrentPosition() {
        EmailLexer lexer = new EmailLexer();
        lexer.lex("test(comment)@example.com");
        Assert.assertTrue(lexer.lexedText(), "test".equals(lexer.lexedText()));
    }
}
