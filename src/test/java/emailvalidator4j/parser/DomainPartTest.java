package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.exception.DotAtStart;
import emailvalidator4j.parser.exception.InvalidEmail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DomainPartTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void dotAtStart() throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser();

        exception.expect(DotAtStart.class);

        parser.parse("@.atstart");
    }

    private DomainPart getDomainPartParser() {
        EmailLexer lexer = new EmailLexer();
        return new DomainPart(lexer);
    }
}
