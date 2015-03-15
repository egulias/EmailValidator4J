package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.DotAtStart;
import emailvalidator4j.parser.exception.InvalidEmail;

public class DomainPart extends Parser {

    DomainPart (EmailLexer lexer) {
        super(lexer);
    }

    @Override
    public void parse(String domainPart) throws InvalidEmail {
        this.lexer.lex(domainPart);
        this.lexer.next();
        if (this.lexer.getCurrent().equals(Tokens.DOT)) {
            throw new DotAtStart("Dot at the beggining of the domain part");
        }
    }
}
