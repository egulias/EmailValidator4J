package emailvalidator4j;

import emailvalidator4j.parser.Email;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class EmailValidatorStrategiesTest {

   @Test
   public void validEmailAndInvalidStrategyIsInvalidEmail() {
      String validEmail = "test@example.com";

      ValidationStrategy alwaysFalseStrategy = mock(ValidationStrategy.class);
      when(alwaysFalseStrategy.isValid(validEmail, mock(Email.class))).thenReturn(false);

      EmailValidator validator = new EmailValidator(Arrays.asList(alwaysFalseStrategy));

      Assert.assertFalse(validator.isValid(validEmail));
   }

   @Test
   public void validEmailAndValidStrategyIsValidEmail() {
      String validEmail = "test@example.com";

      ValidationStrategy alwaysTrueStrategy = mock(ValidationStrategy.class);
      when(alwaysTrueStrategy.isValid(anyString(), any(Email.class))).thenReturn(true);

      EmailValidator validator = new EmailValidator(Arrays.asList(alwaysTrueStrategy));

      Assert.assertTrue(validator.isValid(validEmail));
   }

   @Test
   public void invalidEmailAndValidStrategyIsInvalidEmail() {
      String validEmail = "test@@example.com";

      ValidationStrategy alwaysTrueStrategy = mock(ValidationStrategy.class);
      when(alwaysTrueStrategy.isValid(validEmail, mock(Email.class))).thenReturn(true);

      EmailValidator validator = new EmailValidator(Arrays.asList(alwaysTrueStrategy));

      Assert.assertFalse(validator.isValid(validEmail));
   }
}
