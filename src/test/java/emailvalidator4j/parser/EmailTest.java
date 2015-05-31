package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.NoLocalPart;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class EmailTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void emailHasNoLocalPart() throws InvalidEmail {
        EmailLexer lexer = mock(EmailLexer.class);

        when(lexer.find(Tokens.AT)).thenReturn(false);

        Email parser = new Email(lexer);
        exception.expect(NoLocalPart.class);
        parser.parse("nolocalpart.com");
    }

    @Test
    public void validEmailGetsParsedWithNoExceptions() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        Email parser = new Email(lexer);
        parser.parse("valid@email.com");
    }

    @Test
    public void invalidEmailGetsParsed() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        Email parser = new Email(lexer);

        exception.expect(InvalidEmail.class);
        parser.parse("inva@lid@email.com");
    }

    @Test
    public void canRetrieveDomainPart() throws InvalidEmail {
        EmailLexer lexer = new EmailLexer();
        Email parser = new Email(lexer);
        parser.parse("valid@email.com");

        Assert.assertTrue("got " + parser.getDomainPart(), parser.getDomainPart().equals("email.com"));
    }
}
