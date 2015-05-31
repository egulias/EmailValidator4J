package emailvalidator4j.validator;

import emailvalidator4j.parser.Email;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class MXRecordTest {
    @Test
    public void testHostnameHasMXRecord() {
        MXRecord validator = new MXRecord();
        Email parser = mock(Email.class);
        when(parser.getDomainPart()).thenReturn("egulias.com");

        Assert.assertTrue(validator.isValid("test@egulias.com", parser));
    }

    @Test
    public void testHostnameHasNoMXRecord() {
        MXRecord validator = new MXRecord();
        Email parser = mock(Email.class);
        when(parser.getDomainPart()).thenReturn("example.com");

        Assert.assertFalse(validator.isValid("test@example.com", parser));
    }
}
