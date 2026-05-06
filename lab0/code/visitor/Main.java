import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) {
        List<Pair<String, Integer>> pairs = Arrays.asList(
            new Pair<String, Integer>("1 + 1", 2),
            new Pair<String, Integer>("1 + 2 * 3", 7)
        );

        for(var testcase : pairs) {
            var charStream = CharStreams.fromString(testcase.a);
            ExpressionLexer lexer = new ExpressionLexer(charStream);
            ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
            ExpressionParser.ProgContext prog = parser.prog();

            Integer integer = prog.accept(new MyExpressionVisitor());
            if(integer.equals(testcase.b)) {
                System.out.println(integer);
            }
        }
    }
}
