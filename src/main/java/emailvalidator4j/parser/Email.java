package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.DomainHyphen;
import emailvalidator4j.parser.exception.InvalidCharacters;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.NoLocalPart;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Email {
    private final EmailLexer lexer;
    private final LocalPart localPartParser;
    private final DomainPart domainPartParser;

    public Email(EmailLexer lexer) {
        this.lexer = lexer;
        this.localPartParser = new LocalPart(lexer);
        this.domainPartParser = new DomainPart(lexer);
    }

    public void parse(String email) throws InvalidEmail {

        String nonEmptyEmail = Optional.ofNullable(email).orElseThrow(() ->
                new InvalidEmail("Empty email"));
        this.validateLength(nonEmptyEmail);

        this.lexer.lex(nonEmptyEmail);

        if (!this.lexer.find(Tokens.AT)) {
            throw new NoLocalPart("No local part found");
        }

        if (this.lexer.hasInvalidTokens()) {
            throw new InvalidCharacters("Found invalid or malformed characters");
        }

        this.localPartParser.parse(email);
        this.domainPartParser.parse(this.lexer.toString());
    }

    private void validateLength(String email) throws InvalidEmail {
        int maxLength = LocalPart.RFC5321_LOCALPART_MAX_LENGTH + DomainPart.DOMAINPART_MAX_LENGTH + 1;

        if (email.length() > maxLength) {
            throw new InvalidEmail("email too long");
        }
    }

    public List getWarnings() {
        List<Warnings> warnings = this.localPartParser.getWarnings();
        warnings.addAll(this.domainPartParser.getWarnings());
        return warnings;
    }

    public String getDomainPart() {
        return this.domainPartParser.getParsed().replace("@", "");
    }
}
