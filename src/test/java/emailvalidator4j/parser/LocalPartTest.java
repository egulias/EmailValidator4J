package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.exception.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
}
