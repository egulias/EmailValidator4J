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

    @Test
    public void isValidEmail() {
        EmailValidator validator = new EmailValidator();
        Assert.assertTrue("Is a valid email", validator.isValid("test@test.com"));
    }
}
