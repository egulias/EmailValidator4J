package emailvalidator4j;

import emailvalidator4j.parser.Email;

public interface ValidationStrategy {
    Boolean isValid(String email, Email parser);
}
