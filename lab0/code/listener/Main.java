import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) {
        String input = "3 + 5 * (2 - 8)";
        CharStream charStream = CharStreams.fromString(input);
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression(); // 解析表达式

        // 打印语法树
        System.out.println(tree.toStringTree(parser));

        // 创建访问器
        // MyCalculatorVisitorImpl visitor = new MyCalculatorVisitorImpl();
        // CalculatorParser.ExpressionContext expr = parser.expression();
        // double result = expr.accept(visitor);

        // 创建监听器
        MyListenerImpl listener = new MyListenerImpl();

        // 遍历语法树并触发监听器
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        // 输出结果
        // System.out.println("Result: " + result);
        System.out.println("Result: " + listener.getResult());
    }
}