import org.antlr.v4.runtime.*;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        LexerErrorListener errorListener = new LexerErrorListener();
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        sysYLexer.removeErrorListeners();
        sysYLexer.addErrorListener(errorListener);

        List<? extends Token> tokens = sysYLexer.getAllTokens();

        if (errorListener.hasErrors()) {
            errorListener.printLexerErrorInformation();
        } else {
            Vocabulary vocab = sysYLexer.getVocabulary();
            for (Token t : tokens) {
                String text = t.getText();
                if(vocab.getSymbolicName(t.getType()).equals("INTEGER_CONST")){
                    if(text.startsWith("0x")){
                        text = text.substring(2);
                        while (text.startsWith("0")){
                            text = text.substring(1);
                        }
                    }else if(text.startsWith("0")||text.length()!=1){
                        while (text.startsWith("0")){
                            text = text.substring(1);
                        }
                    }
                }
                System.out.printf("%s %s at Line %d.\n",
                        vocab.getSymbolicName(t.getType()),
                        text,
                        t.getLine(),
                        t.getCharPositionInLine());
            }
        }
    }
}
