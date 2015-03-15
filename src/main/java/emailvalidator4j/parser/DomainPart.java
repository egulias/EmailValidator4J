package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.*;

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

        if (this.lexer.getCurrent().equals(Tokens.HYPHEN)) {
            throw new DomainHyphen("Found -  in domain part");
        }

        String parsedDomain = this.doParseDomainPart();
    }

    private String doParseDomainPart() throws InvalidEmail {
        String domain = "";
        do {
//            System.out.println(this.lexer.getCurrent().getName());
            if (this.lexer.getCurrent().equals(Tokens.SEMICOLON)) {
                throw new ExpectedATEXT("Expected ATEXT");
            }

            if (this.lexer.getCurrent().equals(Tokens.SLASH)) {
                throw new DomainNotAllowedCharacter(
                        String.format("%s is not allowed in domain part", this.lexer.getCurrent().getName())
                );
            }

            if (this.lexer.getCurrent().equals(Tokens.OPENPARETHESIS)) {
                if (this.lexer.getPrevious().equals(Tokens.AT)) {
                    this.warnings.add(Warnings.DEPRECATED_COMMENT);
                }

                this.parseComment();
                this.lexer.next();
            }

            this.checkConsecutiveDots();
            this.checkExceptions();

            if (this.isFWS()) {
                this.parseFWS();
            }

//
//            $prev = $this->lexer->getPrevious();
//
//            if ($this->hasBrackets()) {
//                $this->parseDomainLiteral();
//            }
//
//            $this->checkLabelLength($prev);
//
//            if ($this->isFWS()) {
//                $this->parseFWS();
//            }
//
//            $domain .= $this->lexer->token['value'];
//            $this->lexer->moveNext();
            this.lexer.next();
        } while (!this.lexer.isAtEnd());

        return domain;

    }

    private void checkExceptions() throws InvalidEmail {
        if (this.lexer.getCurrent().equals(Tokens.COMMA)) {
            throw new DomainNotAllowedCharacter(
                    String.format("%s is not allowed in domain part", this.lexer.getCurrent().getName())
            );
        }

        if (this.lexer.getCurrent().equals(Tokens.AT)) {
            throw new ConsecutiveAT("Consecuitive AT found");
        }

        if (this.lexer.getCurrent().equals(Tokens.OPENBRACKET) && !this.lexer.getPrevious().equals(Tokens.AT)) {
            throw new ExpectedATEXT("Found OPENBRACKET");
        }

//
//        if ($this->lexer->token['type'] === EmailLexer::S_OPENQBRACKET && $prev['type'] !== EmailLexer::S_AT) {
//            throw new \InvalidArgumentException('ERR_EXPECTING_ATEXT');
//        }
//
//        if ($this->lexer->token['type'] === EmailLexer::S_HYPHEN && $this->lexer->isNextToken(EmailLexer::S_DOT)) {
//            throw new \InvalidArgumentException('ERR_DOMAINHYPHENEND');
//        }
//
//        if ($this->lexer->token['type'] === EmailLexer::S_BACKSLASH
//                && $this->lexer->isNextToken(EmailLexer::GENERIC)) {
//            throw new \InvalidArgumentException('ERR_EXPECTING_ATEXT');
//        }
    }
}
