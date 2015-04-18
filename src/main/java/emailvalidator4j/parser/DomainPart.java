package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Token;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainPart extends Parser {

    public static final int DOMAINPART_MAX_LENGTH = 255;

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

        this.doParseDomainPart();

        if (this.lexer.getPrevious().equals(Tokens.DOT)) {
            throw new DotAtEnd("");
        }

        if (this.lexer.getPrevious().equals(Tokens.SP)) {
            throw new CRLFAtEnd("");
        }

        if (this.lexer.lexedText().length() > DOMAINPART_MAX_LENGTH) {
            this.warnings.add(Warnings.RFC5322_DOMAIN_TOO_LONG);
        }
    }

    private void doParseDomainPart() throws InvalidEmail {
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
                this.parseLiteralPart();
            }

            this.checkLabelLength();

            if (this.isFWS()) {
                this.parseFWS();
            }

            this.lexer.next();
        } while (!this.lexer.isAtEnd());
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

    private void checkIPv6Tag(String literal) {
        int maxGroups = 8;
        int groupsCount = 0;

        Pattern colon = Pattern.compile(":");
        Pattern badChars = Pattern.compile("[^0-9A-Fa-f:]");
        Pattern doubleColon = Pattern.compile(".+::.+");
        String IPv6Tag = literal.substring(0, 6);
        String literalWithoutTag = literal.substring(6, literal.length());

        Matcher colonMatcher = colon.matcher(literalWithoutTag);
        while (colonMatcher.find()) {
            groupsCount++;
        }

        if (this.getWarnings().contains(Warnings.RFC5322_IPV6_START_WITH_COLON)) {
            groupsCount = groupsCount - 1;
        }
//        if ($colons === false) {
//            // We need exactly the right number of groups
//            if ($groupCount !== $maxGroups) {
//                $this->warnings[] = EmailValidator::RFC5322_IPV6_GRPCOUNT;
//            }
//            return;
//        }
//
//        if ($colons === 0 || $colons === (strlen($IPv6) - 2)) {
//            // RFC 4291 allows :: at the start or end of an address
//            //with 7 other groups in addition
//            ++$maxGroups;
//        }

        Matcher badCharMatcher = badChars.matcher(literalWithoutTag);
        if (badCharMatcher.find()) {
            this.warnings.add(Warnings.RFC5322_IPV6_BAD_CHAR);
        }

        if (this.lexer.getCurrent().equals(Tokens.COLON)) {
            this.warnings.add(Warnings.RFC5322_IPV6_END_WITH_COLON);
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
        } else if (groupsCount < maxGroups) {
            this.warnings.add(Warnings.RFC5322_IPV6_GROUP_COUNT);
        }


//
//        $IPv6       = substr($addressLiteral, 5);
//        //Daniel Marschall's new IPv6 testing strategy
//        $matchesIP  = explode(':', $IPv6);
//        $groupCount = count($matchesIP);
//        $colons     = strpos($IPv6, '::');
//
//
//        if ($colons === false) {
//            // We need exactly the right number of groups
//            if ($groupCount !== $maxGroups) {
//                $this->warnings[] = EmailValidator::RFC5322_IPV6_GRPCOUNT;
//            }
//            return;
//        }
//
//        if ($colons === 0 || $colons === (strlen($IPv6) - 2)) {
//            // RFC 4291 allows :: at the start or end of an address
//            //with 7 other groups in addition
//            ++$maxGroups;
//        }
//
    }

    private void parseLiteralPart() throws InvalidEmail {
        boolean IPv6Tag = false;
        String addressLiteral;
        do {
            if (this.lexer.isNextToken(Tokens.OPENBRACKET)) {
                throw new ExpectedDTEXT("OPENBRACKET");
            }
            this.lexer.next();

            if (this.isObsoleteDTEXT()) {
                this.warnings.add(Warnings.RFC5322_DOMAIN_LITERAL_OBSOLETE_DTEXT);
            }

            if (this.isFWS() && !this.lexer.getCurrent().equals(Tokens.LF)) {
//                $this->warnings[] = EmailValidator::CFWS_FWS;
                this.parseFWS();
            }

            if (this.lexer.getCurrent().equals(Tokens.IPV6TAG)) {
                IPv6Tag = true;
                if (this.lexer.isNextToken(Tokens.DOUBLECOLON)) {
                    this.warnings.add(Warnings.RFC5322_IPV6_START_WITH_COLON);
                }
            }

        } while(!this.lexer.isAtEnd() && !this.lexer.isNextToken(Tokens.CLOSEBRACKET));
        this.warnings.add(Warnings.RFC5321_ADDRESS_LITERAL);
        addressLiteral = this.lexer.lexedText().replace('[', '\0').replace(']', '\0');
        //Remove the initial @
        if (IPv6Tag) {
            this.checkIPv6Tag(addressLiteral.substring(1, addressLiteral.length()));
        } else {
            this.warnings.add(Warnings.RFC5322_DOMAIN_LITERAL);
        }
        this.lexer.next();
    }

    private boolean isObsoleteDTEXT() {
        return this.lexer.getCurrent().equals(new Token(Tokens.INVALID, "")) || this.lexer.getCurrent().equals(Tokens.LF);
    }
}
