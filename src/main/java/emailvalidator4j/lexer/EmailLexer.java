package emailvalidator4j.lexer;

import emailvalidator4j.lexer.exception.TokenNotFound;
import emailvalidator4j.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailLexer {
    private boolean invalidTokens = false;
    private String lexedText = "";
    private int position = 0;
    private Optional<TokenInterface> current = Optional.empty();
    private final List<TokenInterface> tokens = new ArrayList<TokenInterface>();

    public void lex(String input) {
        Pattern pattern = Pattern.compile(
                "([a-zA-Z_]+[46]?)|([0-9]+)|(\r\n)|(::)|(\\s+?)|(.)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher matcher = pattern.matcher(input);

        this.reset();
        while(matcher.find()) {
            String text = input.substring(matcher.start(), matcher.end());
            TokenInterface token = Tokens.get(text);

            if (this.isInvalidToken(token)) {
                this.invalidTokens = true;
            }

            this.tokens.add(Tokens.get(text));
        }

        if (!this.tokens.isEmpty()) {
            this.current = Optional.of(this.tokens.get(this.position));
            this.lexedText += this.tokens.get(this.position).getText();
        }
    }

    /**
     * Warning! This method IS NOT side effect free, it changes lexer state.
     */
    private void reset() {
        this.lexedText = "";
        this.position = 0;
        this.current = Optional.empty();
        this.tokens.clear();
        this.invalidTokens = false;
    }

    private boolean isInvalidToken(TokenInterface token) {
        return token.getName().equals(Tokens.INVALID);
    }

    public TokenInterface getCurrent() {
        return this.current.orElse(new Token("", ""));
    }

    /**
     * Warning! This method IS NOT side effect free, it changes lexer state.
     */
    public void next() {
        this.position ++;
        if (!this.isAtEnd()) {
            this.current = Optional.of(this.tokens.get(this.position));
            this.lexedText += this.tokens.get(this.position).getText();
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

    /**
     * Warning! This method IS NOT side effect free, it changes lexer state.
     */
    public void moveTo(TokenInterface token) throws TokenNotFound {
        if (this.tokens.size() <= this.position + 1) {
            return;
        }

        while (this.position < this.tokens.size()) {
            if (token.equals(this.tokens.get(this.position))) {
                return;
            }
            this.next();
        }

        throw new TokenNotFound();
    }

    public TokenInterface getPrevious() {
        int previousPosition = this.position - 1 >= 0 ? this.position - 1 : 0;
        return this.tokens.get(previousPosition);
    }

    public boolean hasInvalidTokens() {
        return this.invalidTokens;
    }

    public String lexedText() {
        return this.lexedText;
    }

    @Override
    public String toString() {
        int tempPosition = this.position;
        String tokensTexts = "";

        while (tempPosition < this.tokens.size()) {
            tokensTexts += this.tokens.get(tempPosition).getText();
            tempPosition++;
        }

        return tokensTexts;
    }
}
