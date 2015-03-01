package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.NoLocalPart;

public class EmailParser {
    private final EmailLexer lexer;

    EmailParser(EmailLexer lexer) {
        this.lexer = lexer;
    }

    public void parse(String email) throws InvalidEmail {
        this.lexer.lex(email);

        if (!this.lexer.find(Tokens.AT)) {
            throw new NoLocalPart();
        }
    }
}
