package emailvalidator4j;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class EmailValidatorTest {

    @DataProvider
    public static Object[][] invalidEmailProvider() {
        return new Object[][]{
                {"nolocalpart.com"},
        };
    }

    @Test
    @UseDataProvider("invalidEmailProvider")
    public void isInvalidEmail(String email) {
        EmailValidator validator = new EmailValidator();
        Assert.assertFalse(validator.isValid(email));
    }

    @DataProvider
    public static Object[][] validEmailsProvider() {
        return new Object[][] {
                {"example@example.com"},
                {"example@example.co.uk"},
                {"example_underscore@example.fr"},
                {"example@localhost"},
                {"exam\\'ple@example.com"},
                {"exam\\ ple@example.com"},
                {"example((example))@fakedfake.co.uk"},
                {"example@faked(fake).co.uk"},
                {"example+@example.com"},
                {"инфо@письмо.рф"},
                {"\"username\"@example.com"},
                {"\"user,name\"@example.com"},
                {"\"user name\"@example.com"},
                {"\"user@name\"@example.com"},
                {"\"\\a\"@iana.org"},
                {"\"test\\ test\"@iana.org"},
                {"\"\"@iana.org"},
                {String.format("\"\\%s\"@iana.org", "\"")},
        };
    }

    @Test
    @UseDataProvider("validEmailsProvider")
    public void isValidEmail(String validEmail) {
        EmailValidator validator = new EmailValidator();
        Assert.assertTrue(validEmail + " is a valid email", validator.isValid(validEmail));
    }
}
