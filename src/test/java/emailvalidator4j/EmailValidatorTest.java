package emailvalidator4j;

import org.junit.Assert;
import org.junit.Test;

public class EmailValidatorTest {
    @Test
    public void isValidEmail() {
        EmailValidator validator = new EmailValidator();
        Assert.assertTrue("Is a valid email", validator.isValid("test@test.com"));
    }
}
