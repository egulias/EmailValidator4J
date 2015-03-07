package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.TokenInterface;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.DotAtStart;
import emailvalidator4j.parser.exception.ExpectedAT;
import emailvalidator4j.parser.exception.ExpectedATEXT;
import emailvalidator4j.parser.exception.InvalidEmail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalPart extends Parser {
    private boolean closingQuote = false;
    private boolean parseDQuote = true;

    LocalPart (EmailLexer lexer) {
        super(lexer);
    }
    public EmailLexer parse(String localpart) throws InvalidEmail {

        System.out.println(localpart);
        lexer.lex(localpart);
        if (this.lexer.getCurrent().equals(Tokens.DOT)) {
            throw new DotAtStart("Found DOT at start");
        }

        while (!this.lexer.getCurrent().equals(Tokens.AT)) {
            closingQuote = this.checkDoubleQuote(closingQuote);
            if (closingQuote && parseDQuote) {
                this.lexer.next();
                parseDQuote = this.parseDoubleQuote();
            }

            lexer.next();
        }

        return this.lexer;
    }

    private boolean parseDoubleQuote() throws InvalidEmail{
        boolean parseAgain = true;
        boolean setSpecialsWarning = true;

        List<TokenInterface> invalid =
                new ArrayList<TokenInterface>(Arrays.asList(Tokens.CR, Tokens.HTAB, Tokens.LF, Tokens.NUL));
        List<TokenInterface> special =
                new ArrayList<TokenInterface>(Arrays.asList(Tokens.CR, Tokens.HTAB, Tokens.LF));

        while (!this.lexer.getCurrent().equals(Tokens.DQUOTE) && !this.lexer.isAtEnd()) {
            parseAgain = false;

            if (special.contains(this.lexer.getCurrent()) && setSpecialsWarning) {
                this.getWarnings().add(Warnings.CFWS_FWS);
                setSpecialsWarning = false;
            }

            if (!this.escaped() && invalid.contains(this.lexer.getCurrent())) {
                throw new ExpectedATEXT("Invalid token without escaping");
            }
            this.lexer.next();
        }

        if (this.lexer.getPrevious().equals(Tokens.BACKSLASH)) {
           this.checkDoubleQuote(false);
        }

        if (!this.lexer.isNextToken(Tokens.AT)) {
            throw new ExpectedAT("Expected AT after quoted part");
        }

        return parseAgain;
    }
}
