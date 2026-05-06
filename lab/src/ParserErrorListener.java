import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.ArrayList;
import java.util.List;

public class ParserErrorListener extends BaseErrorListener {

    private static final List<String> errors = new ArrayList<>();
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        // 输出符合实验要求的错误信息
        errors.add("Error type B at Line " + line + ": " + msg);

    }
    public static boolean hasErrors() {
        return !errors.isEmpty();
    }
    public static void print() {
        int i=0;
        for (String error : errors) {
            i++;
            if (i < errors.size()) {
                System.out.println(error);
            }else {
                System.out.print(error);
            }
        }
    }
}
