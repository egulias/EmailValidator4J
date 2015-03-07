package emailvalidator4j.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailLexer {
    private int position = 0;
    private Optional<TokenInterface> current = Optional.empty();
    private final List<TokenInterface> tokens = new ArrayList<TokenInterface>();

    public void lex(String input) {
        Pattern pattern = Pattern.compile("([a-zA-Z_]+[46]?)|([0-9]+)|(\r\n)|(::)|(\\s+?)|(.)");
        Matcher matcher = pattern.matcher(input);

        this.reset();
        while(matcher.find()) {
            this.tokens.add(Tokens.get(input.substring(matcher.start(), matcher.end())));
        }

        if (!this.tokens.isEmpty()) {
            this.current = Optional.of(this.tokens.get(this.position));
        }
    }

    private void reset() {
        this.position = 0;
        this.current = Optional.empty();
        this.tokens.clear();
    }

    public TokenInterface getCurrent() {
        return this.current.orElse(new Token("", ""));
    }

    public void next() {
        this.position ++;
        if (!this.isAtEnd()) {
            this.current = Optional.of(this.tokens.get(this.position));
        }
    }

    public boolean isAtEnd() {
        return this.position >= this.tokens.size();
    }

    public boolean isNextToken(TokenInterface token) {
        if (this.tokens.size() <= this.position + 1) {
            return false;
        }
        return this.tokens.get(this.position + 1).equals(token);
    }

    public boolean isNextToken(List<TokenInterface> tokens) {
        for (TokenInterface token : tokens) {
            if (isNextToken(token)) {
                return true;
            }
        }
        return false;
    }

    public boolean find(TokenInterface token) {
        if (this.tokens.size() <= this.position + 1) {
            return false;
        }

        int lookahead = this.position + 1;

        while (lookahead < this.tokens.size()) {
            if (token.equals(this.tokens.get(lookahead))) {
                return true;
            }
            lookahead++;
        }
        return false;
    }

    public TokenInterface getPrevious() {
        int previousPosition = this.position - 1 >= 0 ?this.position - 1 : 0;
        return this.tokens.get(previousPosition);
    }
}
