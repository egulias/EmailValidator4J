package emailvalidator4j.parser;

import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.lexer.Token;
import emailvalidator4j.lexer.TokenInterface;
import emailvalidator4j.lexer.Tokens;
import emailvalidator4j.parser.exception.*;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DomainPart extends Parser {

    private static final int DOMAINPART_MAX_LENGTH = 255;
    private static final int LABEL_MAX_LENGTH = 63;
    private final HashSet<TokenInterface> notAllowedTokens = new HashSet<TokenInterface>(2) {{
        add(Tokens.BACKSLASH);
        add(Tokens.SLASH);
    }};

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
            this.warnings.add(Warnings.RFC5321_DOMAIN_TOO_LONG);
        }
    }

    private void doParseDomainPart() throws InvalidEmail {
        int domainPartOpenedParenthesis = 0;
        boolean openBrackets = false;
        do {
            if (this.lexer.getCurrent().equals(Tokens.HYPHEN)) {
                throw new DomainHyphen("Found -  in domain part");
            }

            if (this.lexer.getCurrent().equals(Tokens.SEMICOLON)) {
                throw new ExpectedATEXT("Expected ATEXT");
            }

            checkNotAllowedChars(this.lexer.getCurrent());

            if (this.lexer.getCurrent().equals(Tokens.OPENPARETHESIS)) {
                if (this.lexer.getPrevious().equals(Tokens.AT)) {
                    this.warnings.add(Warnings.DEPRECATED_COMMENT);
                }

                this.parseComment();
                domainPartOpenedParenthesis += getOpenedParenthesis();
                this.lexer.next();
                if (this.lexer.getPrevious().equals(Tokens.CLOSEPARENTHESIS)) {
                    domainPartOpenedParenthesis--;
                }

            }

            if (this.lexer.getCurrent().equals(Tokens.CLOSEPARENTHESIS)) {
                if (domainPartOpenedParenthesis <= 0) {
                    throw new UnclosedComment("Missing closing parenthesis");
                }
                domainPartOpenedParenthesis--;
            }

            this.checkConsecutiveDots();
            this.checkExceptions();

            if (isDomainLiteral(openBrackets)) {
                openBrackets = true;
                this.parseLiteralPart();
            }

            this.checkLabelLength();

            if (this.isFWS()) {
                this.parseFWS();
            }

            this.lexer.next();
        } while (!this.lexer.isAtEnd());
    }

    private boolean isDomainLiteral(boolean openBrackets) throws UnclosedDomainLiteral {
        if (this.lexer.getCurrent().equals(Tokens.CLOSEBRACKET) && !openBrackets) {
            throw new UnclosedDomainLiteral("Missing open bracket [");
        }

        return this.lexer.getCurrent().equals(Tokens.OPENBRACKET);
    }

    private void checkNotAllowedChars(TokenInterface token) throws DomainNotAllowedCharacter {
        if (notAllowedTokens.contains(token)) {
            throw new DomainNotAllowedCharacter(
                    String.format("%s is not allowed in domain part", token.getName())
            );
        }

    }

    private void checkLabelLength() {
        if (this.lexer.getCurrent().equals(Tokens.DOT) &&
                this.lexer.getPrevious().equals(Tokens.get(Tokens.GENERIC)) &&
                this.lexer.getPrevious().getText().length() > LABEL_MAX_LENGTH
                ) {
            this.warnings.add(Warnings.RFC1035_LABEL_TOO_LONG);
        }
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

        if (this.lexer.getCurrent().equals(Tokens.get("GENERIC")) && this.lexer.isNextToken(Tokens.get("GENERIC"))) {
            this.lexer.next();
            throw new ConsecutiveGeneric("Found " + this.lexer.getCurrent().getText());
        }
    }

    private void checkIPv6Tag(String literal) {
        int maxGroups = 8;
        int groupsCount = 0;

        Pattern colon = Pattern.compile(":[0-9A-Fa-f]{4}");
        Pattern badChars = Pattern.compile("[^0-9A-Fa-f:]");
        Pattern doubleColon = Pattern.compile(".+::.+");
        Pattern startOrEndWithDoubleColon = Pattern.compile("(^::)|(.*::$)");
        String IPv6Tag = literal.substring(0, 6);
        String literalWithoutTag = literal.substring(5, literal.length());

        Matcher colonMatcher = colon.matcher(literalWithoutTag);
        while (colonMatcher.find()) {
            groupsCount++;
        }

        if (this.getWarnings().contains(Warnings.RFC5322_IPV6_START_WITH_COLON)) {
//            groupsCount = groupsCount - 1;
            return;
        }

        Matcher badCharMatcher = badChars.matcher(literalWithoutTag);
        if (badCharMatcher.find()) {
            this.warnings.add(Warnings.RFC5322_IPV6_BAD_CHAR);
            groupsCount++;
        }

        if (this.lexer.getCurrent().equals(Tokens.COLON)) {
            this.warnings.add(Warnings.RFC5322_IPV6_END_WITH_COLON);
            return;
        }

        Matcher doubleColonMatcher = doubleColon.matcher(literalWithoutTag);
        if (doubleColonMatcher.find()) {
            this.warnings.add(Warnings.RFC5322_IPV6_DOUBLE_COLON);
            //TODO review this. This warning should be compatible with others? http://www.rfcreader.com/#rfc5321_line1934
            return;
        }

        Matcher startOrEndWithDoubleColonMatcher = startOrEndWithDoubleColon.matcher(literalWithoutTag);
        boolean startsOrEndsWithDoubleColon = startOrEndWithDoubleColonMatcher.find();

        if (startsOrEndsWithDoubleColon) {
            groupsCount ++;
        }

        if (groupsCount != maxGroups) {
            this.warnings.add(Warnings.RFC5322_IPV6_GROUP_COUNT);
        }

        if (groupsCount > maxGroups) {
            this.warnings.add(Warnings.RFC5322_IPV6_MAX_GROUPS);
        } else if (groupsCount == maxGroups && startsOrEndsWithDoubleColon) {
            this.warnings.add(Warnings.RFC5321_IPV6_DEPRECATED);
        }
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

    public String getParsed() {
        return this.lexer.lexedText();
    }
}
