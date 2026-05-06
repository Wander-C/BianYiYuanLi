import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class LexerErrorListener extends BaseErrorListener {
    private final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        msg = msg.substring(msg.length()-2 ,msg.length()-1);
        errors.add("Error type A at Line " + line + ": Mysterious character \"" + msg+"\".");
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void print() {
        int i=0;
        for (String error : errors) {
            i++;
            if (i < errors.size()) {
                System.err.println(error);
            }else {
                System.err.print(error);
            }
        }
    }
}
