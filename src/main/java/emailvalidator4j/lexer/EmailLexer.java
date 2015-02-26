package emailvalidator4j.lexer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailLexer {
    private Optional<TokenInterface> current = Optional.empty();
    private ArrayList<TokenInterface> tokens = new ArrayList<TokenInterface>(1);

    public void lex(String input) {
        Pattern pattern = Pattern.compile("([a-zA-Z_]+[46]?)|([0-9]+)|(\r\n)|(::)|(\\s+?)|(.)");
        Matcher matcher = pattern.matcher(input);

        while(matcher.find()) {
            this.tokens.add(Tokens.get(input.substring(matcher.start(), matcher.end())));
        }

        if (!this.tokens.isEmpty()) {
            this.current = Optional.of(this.tokens.get(0));
        }
    }

    public TokenInterface getCurrent() {
        return this.current.orElse(new Token("", ""));
    }
}
