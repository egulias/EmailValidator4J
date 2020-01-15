package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Token;
import emailvalidator4j.lexer.TokenInterface;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class LocalPart extends Parser {
    private boolean closingQuote = false;
    private boolean parseDQuote = true;
    public static int RFC5321_LOCALPART_MAX_LENGTH = 64;

    LocalPart (EmailLexer lexer) {
        super(lexer);
    }

    @Override
    public void parse(String localpart) throws InvalidEmail {
        lexer.lex(localpart);
        int localPartOpenedParenthesis = 0;
        if (this.lexer.getCurrent().equals(Tokens.DOT)) {
            throw new DotAtStart("Found DOT at start");
        }

        while (!this.lexer.getCurrent().equals(Tokens.AT) && !this.lexer.isAtEnd()) {
            closingQuote = this.checkDoubleQuote(closingQuote);
            if (closingQuote && parseDQuote) {
                this.lexer.next();
                parseDQuote = this.parseDoubleQuote();
            }

            if (this.lexer.getCurrent().equals(Tokens.OPENPARETHESIS)) {
                this.parseComment();
                localPartOpenedParenthesis += getOpenedParenthesis();
            }

            if (this.lexer.getCurrent().equals(Tokens.CLOSEPARENTHESIS)) {
                if(localPartOpenedParenthesis == 0) {
                    throw new UnclosedComment("Missing closing parenthesis");
                }
                localPartOpenedParenthesis--;
            }

            this.checkConsecutiveDots();
            this.checkForInvalidToken(closingQuote);

            if (this.isFWS()) {
                this.parseFWS();
            }

            if (this.lexer.getPrevious().equals(Tokens.BACKSLASH)) {
                if (!this.escaped()) {
                    throw new ExpectedATEXT("Found BACKSLASH");
                }
            }

            lexer.next();
        }

        if (this.lexer.getPrevious().equals(Tokens.DOT)) {
            throw new DotAtEnd("Dot at the end of localpart");
        }

        this.checkRFC5321Length();
    }

    private void checkRFC5321Length() {
        if (this.lexer.lexedText().length() > RFC5321_LOCALPART_MAX_LENGTH) {
            this.warnings.add(Warnings.RFC5321_LOCALPART_TOO_LONG);
        }
    }

    private void checkForInvalidToken(boolean closingQuote) throws InvalidEmail {
        List<TokenInterface> forbidden = new ArrayList<TokenInterface>(Arrays.asList(
                Tokens.COMMA,
                Tokens.CLOSEBRACKET,
                Tokens.OPENBRACKET,
                Tokens.GREATERTHAN,
                Tokens.LOWERTHAN,
                Tokens.COLON,
                Tokens.SEMICOLON
        ));

        if (forbidden.contains(this.lexer.getCurrent()) && !closingQuote) {
            throw new ExpectedATEXT(String.format("Found %s, expeted ATEXT", this.lexer.getCurrent().getName()));
        }
    }

    private boolean parseDoubleQuote() throws InvalidEmail {
        boolean parseAgain = true;
        boolean setSpecialsWarning = true;

        List<TokenInterface> invalid =
                new ArrayList<TokenInterface>(Arrays.asList(Tokens.CR, Tokens.HTAB, Tokens.LF, Tokens.NUL));
        List<TokenInterface> special =
                new ArrayList<TokenInterface>(Arrays.asList(Tokens.CR, Tokens.HTAB, Tokens.LF));

        while (!this.lexer.getCurrent().equals(Tokens.DQUOTE) && !this.lexer.isAtEnd()) {
            parseAgain = false;

            if (special.contains(this.lexer.getCurrent()) && setSpecialsWarning) {
                this.warnings.add(Warnings.CFWS_FWS);
                setSpecialsWarning = false;
            }

            if (!this.escaped() && invalid.contains(this.lexer.getCurrent())) {
                throw new ExpectedATEXT("Invalid token without escaping");
            }

            this.lexer.next();
        }

        if (this.lexer.getPrevious().equals(Tokens.BACKSLASH)) {
           this.checkDoubleQuote(false);
        } else if (!this.lexer.isNextToken(Tokens.AT)) {
            throw new ExpectedAT("Expected AT after quoted part");
        }

        return parseAgain;
    }
}
