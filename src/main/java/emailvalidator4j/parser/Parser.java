package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.TokenInterface;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.ConsecutiveDots;
import emailvalidator4j.parser.exception.InvalidEmail;
import emailvalidator4j.parser.exception.UnclosedComment;
import emailvalidator4j.parser.exception.UnclosedDQUOTE;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    private List<Warnings> warnings = new ArrayList<Warnings>();
    protected EmailLexer lexer;

    public Parser(EmailLexer lexer) {
        this.lexer = lexer;
    }

    public abstract EmailLexer parse(String part) throws InvalidEmail;

    public List<Warnings> getWarnings() {
        return this.warnings;
    }

    protected void validateQuotedPair() {
//        if (!(this.lexer.getCurrent().equals(Tokens.D) === EmailLexer::INVALID
//                || $this->lexer->token['type'] === EmailLexer::C_DEL)) {
//            throw new \InvalidArgumentException('ERR_EXPECTING_QPAIR');
//        }

        this.warnings.add(Warnings.DEPRECATED_QP);
    }

    protected boolean escaped() {
        return this.lexer.getPrevious().equals(Tokens.BACKSLASH) && !this.lexer.equals(Tokens.get("GENERIC"));
    }

    protected void parseComment() throws InvalidEmail {
        this.checkUnclosedComment();
        this.warnings.add(Warnings.COMMENT);

        while (!this.lexer.getCurrent().equals(Tokens.CLOSEPARENTHESIS)) {
//            this.warnEscaping();
            this.lexer.next();
        }

//        if (this.lexer.isNextToken(array(EmailLexer::GENERIC, EmailLexer::S_EMPTY))) {
//            throw new \InvalidArgumentException('ERR_EXPECTING_ATEXT');
//        }

        if (this.lexer.isNextToken(Tokens.AT)) {
            this.warnings.add(Warnings.DEPRECATED_CFWS_NEAR_AT);
        }
    }

    protected void checkUnclosedComment() throws InvalidEmail {
        if (!this.lexer.find(Tokens.CLOSEPARENTHESIS)) {
            throw new UnclosedComment("Unclosed Comment");
        }
    }

    protected void checkConsecutiveDots() throws InvalidEmail {
        if (this.lexer.getCurrent().equals(Tokens.DOT) && this.lexer.isNextToken(Tokens.DOT)) {
            throw new ConsecutiveDots("Consecutive dots");
        }
    }


    protected boolean checkDoubleQuote(boolean hasClosingQuote) throws InvalidEmail {
        if (!this.lexer.getCurrent().equals(Tokens.DQUOTE)) {
            return hasClosingQuote;
        }

        if (hasClosingQuote) {
            return hasClosingQuote;
        }

        if (this.lexer.isNextToken(Tokens.get("GENERIC")) && this.lexer.getPrevious().equals(Tokens.get("GENERIC"))) {
//            throw new \InvalidArgumentException('ERR_EXPECTING_ATEXT');
        }
//
//        $this.warnings[] = EmailValidator::RFC5321_QUOTEDSTRING;
//        try {
        hasClosingQuote = this.lexer.find(Tokens.DQUOTE);
        if (!hasClosingQuote) {
            throw new UnclosedDQUOTE("Unclosed DQUOTE");
        }
        return hasClosingQuote;
    }
}
