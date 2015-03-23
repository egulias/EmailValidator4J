package emailvalidator4j;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.Email;
import emailvalidator4j.parser.exception.InvalidEmail;

public final class EmailValidator {
    private final Email parser = new Email(new EmailLexer());

    public boolean isValid(String email) {
        try {
            this.parser.parse(email);
        } catch (InvalidEmail invalidEmail) {
            return false;
        }

        return true;
    }
}
