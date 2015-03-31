package emailvalidator4j.parser;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.exception.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class LocalPartTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void foundDotAtStart() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expectMessage("Found DOT at start");
        exception.expect(DotAtStart.class);

        parser.parse(".atstart@");
    }

    @Test
    public void unescapedInvalidTokenInsideDQUOTE() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(ExpectedATEXT.class);
        exception.expectMessage("Invalid token without escaping");

        parser.parse(String.format("Lat%ss\rtart%s@", "\"", "\""));
    }

    @Test
    public void escapedClosingDQUOTE() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(UnclosedDQUOTE.class);
        exception.expectMessage("Unclosed DQUOTE");

        parser.parse(String.format("at%sstart%s%s@", "\"", "\\", "\""));
    }

    @Test
    public void expectedATAfterQuotedPart() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(ExpectedAT.class);
        exception.expectMessage("Expected AT after quoted part");

        parser.parse("at\"start\"test@");
    }

    @Test
    public void expectedATEXTAfterCFWS() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(ATEXTAfterComment.class);

        parser.parse("at(start)test@");
    }

    @Test
    public void unclosedComment() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(UnclosedComment.class);
        exception.expectMessage("Unclosed Comment");

        parser.parse("at(starttest@");
    }

    @Test
    public void consecutiveDots() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        LocalPart parser = new LocalPart(lexer);

        exception.expect(ConsecutiveDots.class);
        exception.expectMessage("Consecutive dots");

        parser.parse("with..dots@");
    }

    @Test
    public void dotAtEnd() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();

        exception.expect(DotAtEnd.class);
        exception.expectMessage("Dot at the end of localpart");

        parser.parse("withdot.@");
    }

    @Test
    @UseDataProvider("invalidLocalPartText")
    public void invalidToken(String invalidText) throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(ExpectedATEXT.class);

        parser.parse(String.format("found%sat@",invalidText));
    }

    @DataProvider
    public static Object[][] invalidLocalPartText() {
        return new Object[][]{
                {","},
                {"<"},
                {">"},
                {"["},
                {"]"},
                {":"},
                {";"},
                {"\\"},
        };
    }

    @Test
    public void consecutiveCRLF() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(ConsecutiveCRLF.class);

        parser.parse("test\r\n\r\nat@");
    }

    @Test
    public void CRLFAtEnd() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(CRLFAtEnd.class);

        parser.parse("test\r\nat@");
    }

    @Test
    public void CRWithoutLF() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(CRWithoutLF.class);

        parser.parse("test\rat@");
    }

    @Test
    public void ATEXTAfterCFWS() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(ATEXTAfterCFWS.class);

        parser.parse("test\r\n at@");
    }

    @Test
    public void expectedCTEXT() throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        exception.expect(ExpectedCTEXT.class);

        parser.parse("test\r\n \n@");
    }

    @Test
    @UseDataProvider("localPartWithWarnings")
    public void localPartHasWarnings(String localPart, List<Warnings> warnings) throws InvalidEmail {
        LocalPart parser = this.getLocalPartParser();
        parser.parse(localPart);
        System.out.println(parser.getWarnings());

        Assert.assertTrue("Expected warning " + warnings.toString(), parser.getWarnings().equals(warnings));
    }

    @DataProvider
    public static Object[][] localPartWithWarnings() {
        return new Object[][]{
                {"example @", Arrays.asList(Warnings.DEPRECATED_CFWS_NEAR_AT)},
                {"example(comment)@", Arrays.asList(Warnings.COMMENT, Warnings.DEPRECATED_CFWS_NEAR_AT)},
                {"\"quoted\"@", Arrays.asList(Warnings.RFC5321_QUOTEDSTRING)},
                {String.format("\"%s%s\"@", "\\", "\t"), Arrays.asList(Warnings.RFC5321_QUOTEDSTRING, Warnings.CFWS_FWS)},
                {"too_long_localpart_too_long_localpart_too_long_localpart_too_long_localpart@",
                        Arrays.asList(Warnings.RFC5321_LOCALPART_TOO_LONG)},

//                {"example@ example.com", Arrays.asList(Warnings.DEPRECATED_CFWS_NEAR_AT)},
        };
    }

    private LocalPart getLocalPartParser() {
        EmailLexer lexer = new EmailLexer();
        return new LocalPart(lexer);
    }
}
