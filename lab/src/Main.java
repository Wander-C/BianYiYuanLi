import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
            System.exit(1); // 修复问题1
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);

        // 创建 Lexer 并配置错误监听
        SysYLexer sysYLexer = new SysYLexer(input);
        sysYLexer.removeErrorListeners();
        sysYLexer.addErrorListener(new LexerErrorListener());

        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
        sysYParser.removeErrorListeners();
        sysYParser.addErrorListener(new ParserErrorListener());

        // 仅解析一次
        ParseTree tree = sysYParser.program();

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        if (OutputHelper.hasErrors()) {
            OutputHelper.print();
        } else {
            System.err.print("No semantic errors in the program!");
        }
    }
}
