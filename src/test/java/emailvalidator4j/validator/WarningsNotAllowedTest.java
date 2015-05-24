package emailvalidator4j.validator;

import emailvalidator4j.parser.Email;
import emailvalidator4j.parser.Warnings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class WarningsNotAllowedTest {

    @Test
    public void returnFalseWhenParserHasWarnings() {
        WarningsNotAllowed validator = new WarningsNotAllowed();
        Email parser = mock(Email.class);
        when(parser.getWarnings()).thenReturn(Collections.singletonList(Warnings.COMMENT));
        Assert.assertFalse(validator.isValid("email", parser));
    }

    @Test
    public void returnTrueWhenParserHasNoWarnings() {
        WarningsNotAllowed validator = new WarningsNotAllowed();
        Email parser = mock(Email.class);
        when(parser.getWarnings()).thenReturn(Collections.emptyList());
        Assert.assertTrue(validator.isValid("email", parser));
    }
}
