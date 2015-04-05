package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (this.lexer.getPrevious().equals(Tokens.DOT)) {
            throw new DotAtEnd("");
        }

        if (this.lexer.getPrevious().equals(Tokens.SP)) {
            throw new CRLFAtEnd("");
        }
    }

    private String doParseDomainPart() throws InvalidEmail {
        String domain = "";
        do {
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

            if (this.lexer.getCurrent().equals(Tokens.OPENBRACKET)) {
                this.checkIPv6Warnings();
                this.parseLiteralPart();
            }

            this.checkLabelLength();

            if (this.isFWS()) {
                this.parseFWS();
            }

//            $domain .= $this->lexer->token['value'];
            this.lexer.next();
        } while (!this.lexer.isAtEnd());

        return domain;

    }

    private void checkLabelLength() {
//        if ($this->lexer->token['type'] === EmailLexer::S_DOT &&
//                $prev['type'] === EmailLexer::GENERIC &&
//                strlen($prev['value']) > 63
//                ) {
//            $this->warnings[] = EmailValidator::RFC5322_LABEL_TOOLONG;
//        }
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

        if (this.lexer.getCurrent().equals(Tokens.HYPHEN) && this.lexer.isNextToken(Tokens.DOT)) {
            throw new DomainHyphen("Hypen found near dot");
        }

        if (this.lexer.getCurrent().equals(Tokens.BACKSLASH) && this.lexer.isNextToken(Tokens.get("GENERIC"))) {
            throw new ExpectedATEXT("Found BACKSLASH");
        }
    }

    private void checkIPv6Warnings() {

//        if ($this->lexer->isNextToken(EmailLexer::S_COLON)) {
//            $this->warnings[] = EmailValidator::RFC5322_IPV6_COLONSTRT;
//        }
//        if ($this->lexer->isNextToken(EmailLexer::S_IPV6TAG)) {
//            $lexer = clone $this->lexer;
//            $lexer->moveNext();
//            if ($lexer->isNextToken(EmailLexer::S_DOUBLECOLON)) {
//                $this->warnings[] = EmailValidator::RFC5322_IPV6_COLONSTRT;
//            }
//        }
    }

    private void checkIPv6Tag(String literal) {
        int maxGroups = 8;
        int groupsCount = 0;

        Pattern colon = Pattern.compile(":");
        Pattern badChars = Pattern.compile("^[0-9A-Fa-f]{0,4}$");
        Pattern doubleColon = Pattern.compile(".+::.+");
        String IPv6Tag = literal.substring(0, 6);
        String literalWithoutTag = literal.substring(6, literal.length());

        Matcher colonMatcher = colon.matcher(literalWithoutTag);
        while (colonMatcher.find()) {
            groupsCount++;
        }

        Matcher doubleColonMatcher = doubleColon.matcher(literalWithoutTag);
        if (doubleColonMatcher.find()) {
            this.warnings.add(Warnings.RFC5322_IPV6_DOUBLE_COLON);
            //TODO review this. This warning should be compatible with others? http://www.rfcreader.com/#rfc5321_line1934
            return;
        }

        if (groupsCount > maxGroups) {
            this.warnings.add(Warnings.RFC5322_IPV6_MAX_GROUPS);
        } else if (groupsCount == maxGroups) {
            this.warnings.add(Warnings.RFC5321_IPV6_DEPRECATED);
        }


//        $prev = $this->lexer->getPrevious();
//        if ($prev['type'] === EmailLexer::S_COLON) {
//            $this->warnings[] = EmailValidator::RFC5322_IPV6_COLONEND;
//        }
//
//        $IPv6       = substr($addressLiteral, 5);
//        //Daniel Marschall's new IPv6 testing strategy
//        $matchesIP  = explode(':', $IPv6);
//        $groupCount = count($matchesIP);
//        $colons     = strpos($IPv6, '::');
//
//        if (count(preg_grep('/^[0-9A-Fa-f]{0,4}$/', $matchesIP, PREG_GREP_INVERT)) !== 0) {
//            $this->warnings[] = EmailValidator::RFC5322_IPV6_BADCHAR;
//        }
//
//        if ($colons === false) {
//            // We need exactly the right number of groups
//            if ($groupCount !== $maxGroups) {
//                $this->warnings[] = EmailValidator::RFC5322_IPV6_GRPCOUNT;
//            }
//            return;
//        }
//
//        if ($colons !== strrpos($IPv6, '::')) {
//            $this->warnings[] = EmailValidator::RFC5322_IPV6_2X2XCOLON;
//            return;
//        }
//
//        if ($colons === 0 || $colons === (strlen($IPv6) - 2)) {
//            // RFC 4291 allows :: at the start or end of an address
//            //with 7 other groups in addition
//            ++$maxGroups;
//        }
//
//        if ($groupCount > $maxGroups) {
//            $this->warnings[] = EmailValidator::RFC5322_IPV6_MAXGRPS;
//        } elseif ($groupCount === $maxGroups) {
//            $this->warnings[] = EmailValidator::RFC5321_IPV6DEPRECATED;
//        }
    }

    private void parseLiteralPart() throws InvalidEmail {
        boolean IPv6Tag = false;
        String addressLiteral = "";
        do {
            if (this.lexer.isNextToken(Tokens.OPENBRACKET)) {
                throw new ExpectedDTEXT("OPENBRACKET");
            }

            if (this.isFWS()) {
//                $this->warnings[] = EmailValidator::CFWS_FWS;
                this.parseFWS();
            }

            if (this.lexer.getCurrent().equals(Tokens.BACKSLASH)) {
//                $this->warnings[] = EmailValidator::RFC5322_DOMLIT_OBSDTEXT;
//                $this->validateQuotedPair();
            }

            if (this.lexer.getCurrent().equals(Tokens.IPV6TAG)) {
                IPv6Tag = true;
            }

//            addressLiteral += this.lexer.getCurrent().getText();

            this.lexer.next();
        } while(!this.lexer.isAtEnd() && !this.lexer.isNextToken(Tokens.CLOSEBRACKET));
        this.warnings.add(Warnings.RFC5321_ADDRESS_LITERAL);
        addressLiteral = this.lexer.lexedText().replace('[', '\0').replace(']', '\0');
        //Remove the initial @
        this.checkIPv6Tag(addressLiteral.substring(1, addressLiteral.length()));
//        do {
//
//            if ($this->lexer->token['type'] === EmailLexer::INVALID ||
//                    $this->lexer->token['type'] === EmailLexer::C_DEL   ||
//                            $this->lexer->token['type'] === EmailLexer::S_LF
//                    ) {
//                $this->warnings[] = EmailValidator::RFC5322_DOMLIT_OBSDTEXT;
//            }
//
//        } while ($this->lexer->moveNext());
//
//        $addressLiteral = str_replace('[', '', $addressLiteral);
//        $addressLiteral = $this->checkIPV4Tag($addressLiteral);
//
//        if (false === $addressLiteral) {
//            return $addressLiteral;
//        }
//
//        if (!$IPv6TAG) {
//            $this->warnings[] = EmailValidator::RFC5322_DOMAINLITERAL;
//            return $addressLiteral;
//        }
//
//        $this->warnings[] = EmailValidator::RFC5321_ADDRESSLITERAL;
//
//        $this->checkIPV6Tag($addressLiteral);
//
//        return $addressLiteral;
    }

}
