import org.antlr.v4.runtime.tree.*;
import java.util.Stack;

public class MyListenerImpl extends CalculatorBaseListener {
    private Stack<Double> stack = new Stack<>();

    public double getResult() {
        return stack.pop();
    }

    @Override
    public void exitParenExpression(CalculatorParser.ParenExpressionContext ctx) {
        // 括号表达式的结果已经在栈中，无需额外操作
    }

    @Override
    public void exitMulDivExpression(CalculatorParser.MulDivExpressionContext ctx) {
        double right = stack.pop();
        double left = stack.pop();
        if (ctx.op.getText().equals("*")) {
            stack.push(left * right);
        } else {
            stack.push(left / right);
        }
    }

    @Override
    public void exitAddSubExpression(CalculatorParser.AddSubExpressionContext ctx) {
        double right = stack.pop();
        double left = stack.pop();
        if (ctx.op.getText().equals("+")) {
            stack.push(left + right);
        } else {
            stack.push(left - right);
        }
    }

    @Override
    public void exitNumberExpression(CalculatorParser.NumberExpressionContext ctx) {
        double number = Double.parseDouble(ctx.NUMBER().getText());
        stack.push(number);
    }
}