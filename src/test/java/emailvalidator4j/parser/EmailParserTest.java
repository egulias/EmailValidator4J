package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.NoLocalPart;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class EmailParserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void emailHasNoLocalPart() throws InvalidEmail {
        EmailLexer lexer = mock(EmailLexer.class);

        when(lexer.find(Tokens.AT)).thenReturn(false);

        EmailParser parser = new EmailParser(lexer);
        exception.expect(NoLocalPart.class);
        parser.parse("nolocalpart.com");
    }
}
