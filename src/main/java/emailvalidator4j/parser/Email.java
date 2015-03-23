package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.DomainHyphen;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.NoLocalPart;

import java.util.ArrayList;

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
        this.lexer.lex(email);

        if (!this.lexer.find(Tokens.AT)) {
            throw new NoLocalPart("No local part found");
        }

//        String[] parts = email.split("@");
//        String domainPart = '@' + parts[parts.length - 1];
//        String localPart = this.composeLocalPart(parts);
//        System.out.println(localPart);

        this.localPartParser.parse(email);
        this.domainPartParser.parse(email);
    }

    private String composeLocalPart(String[] parts) {
        String localPart = "";
        for (int i = 0; i < parts.length - 1; i++) {
            localPart += parts[i] + '@';
        }

        return localPart;
    }
}
