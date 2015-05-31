package emailvalidator4j.parser;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.exception.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class DomainPartTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @UseDataProvider("invalidDomainParts")
    public void invalidPartThrowsException(Class type, String domainPart) throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser();
        exception.expect(type);
        parser.parse(domainPart);
    }

    @DataProvider
    public static Object[][] invalidDomainParts() {
        //No existence of Tokens.AT is controlled in a superior class
        return new Object[][]{
                {DotAtStart.class, "@.atstart"},
                {DomainHyphen.class, "@-atstart"},
                {ExpectedATEXT.class, "@;atstart"},
                {DomainNotAllowedCharacter.class, "@/atstart"},
                {ConsecutiveDots.class, "@at..start"},
                {ConsecutiveCRLF.class, "@test\r\n\r\nat"},
                {CRLFAtEnd.class, "@test\r\nat"},
                {CRWithoutLF.class, "@test\rat"},
                {ATEXTAfterCFWS.class, "@test\r\n at"},
                {ExpectedCTEXT.class, "@test\r\n \n"},
                {UnclosedComment.class, "@a(comment"},
                {DomainNotAllowedCharacter.class, "@a,start"},
                {ConsecutiveAT.class, "@@start"},
                {ExpectedATEXT.class, "@at[start"},
                {DomainHyphen.class, "@atstart-.com"},
                {ExpectedATEXT.class, "@atst\\art.com"},
        };
    }

    @Test
    @UseDataProvider("invalidDomainLiteralParts")
    public void invalidDomainLiteralExceptions(Class type, String literalPart) throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser();
        exception.expect(type);
        parser.parse(literalPart);
    }

    @DataProvider
    public static Object[][] invalidDomainLiteralParts() {
        return new Object[][]{
                {ExpectedDTEXT.class, "@[[127.0.0.1]"},
                {CRWithoutLF.class, "@[\r127.0.0.1]"}
        };
    }

    @Test
    @UseDataProvider("domainPartWithWarnings")
    public void domainPartHasWarnings(String domainPart, List<Warnings> warnings) throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser();
        parser.parse(domainPart);

        Assert.assertTrue(parser.getWarnings().toString().concat(" expected ->").concat(warnings.toString()), warnings.equals(parser.getWarnings()));
    }

    @DataProvider
    public static Object[][] domainPartWithWarnings() {
        return new Object[][]{
                {"@ example.com", Arrays.asList(Warnings.DEPRECATED_CFWS_NEAR_AT)},
                {"@example(comment).com", Arrays.asList(Warnings.COMMENT)},
                {"@domaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolong" +
                        "domaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolong" +
                        "domaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolongdomaintoolong" +
                        ".com",
                        Arrays.asList(Warnings.RFC1035_LABEL_TOO_LONG, Warnings.RFC5321_DOMAIN_TOO_LONG)},
                {"@[127.0.0.1]", Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5322_DOMAIN_LITERAL)},
                {"@[IPv6:2001:0db8:85a3:0000:0000:8a2e:0370:7334]", Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL)},
                {"@[IPv6:2001:0db8:85a3:0000:0000:8a2e:0370::]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5321_IPV6_DEPRECATED)},
                {"@[IPv6:2001:0db8:85a3:0000:0000:8a2e:0370:7334::]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL,  Warnings.RFC5322_IPV6_GROUP_COUNT,
                                Warnings.RFC5322_IPV6_MAX_GROUPS)},
                {"@[IPv6:1::1::1]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5322_IPV6_DOUBLE_COLON)},
                {"@[\n]",
                        Arrays.asList(Warnings.RFC5322_DOMAIN_LITERAL_OBSOLETE_DTEXT, Warnings.RFC5321_ADDRESS_LITERAL,
                                Warnings.RFC5322_DOMAIN_LITERAL)},
                {"@[IPv6::2001:0db8:85a3:0000:0000:8a2e:0370:7334]",
                        Arrays.asList(Warnings.RFC5322_IPV6_START_WITH_COLON, Warnings.RFC5321_ADDRESS_LITERAL)},
                {"@[IPv6:z001:0db8:85a3:0000:0000:8a2e:0370:7334]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5322_IPV6_BAD_CHAR)},
                {"@[IPv6:2001:0db8:85a3:0000:0000:8a2e:0370:]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5322_IPV6_END_WITH_COLON)},
                {"@[IPv6:1111:2222:3333:4444:5555:6666:7777]",
                        Arrays.asList(Warnings.RFC5321_ADDRESS_LITERAL, Warnings.RFC5322_IPV6_GROUP_COUNT)},
        };
    }

    @Test
    public void lexedDomainIsExposed() throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser();
        parser.parse("@email.com");

        Assert.assertTrue("got " + parser.getParsed(), parser.getParsed().equals("@email.com"));
    }

    private DomainPart getDomainPartParser() {
        EmailLexer lexer = new EmailLexer();
        return new DomainPart(lexer);
    }
}
