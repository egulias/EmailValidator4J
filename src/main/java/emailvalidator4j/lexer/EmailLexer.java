package emailvalidator4j.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailLexer {
    private TokenInterface current;

    public void lex(String input) {
        Pattern pattern = Pattern.compile("([a-zA-Z_]+[46]?)|([0-9]+)|(\r\n)|(::)|(\\s+?)|(.)");
        Matcher matcher = pattern.matcher(input);

        while(matcher.find()) {
            input.substring(matcher.start(), matcher.end());
        }
    }

    public TokenInterface getCurrent() {
        return this.current;
    }
}
