import org.antlr.v4.runtime.tree.*;

public class MyCalculatorVisitorImpl extends CalculatorBaseVisitor<Double> {
    @Override
    public Double visitParenExpression(CalculatorParser.ParenExpressionContext ctx) {
        // 返回括号内表达式的值
        return visit(ctx.expression());
    }

    @Override
    public Double visitMulDivExpression(CalculatorParser.MulDivExpressionContext ctx) {
        // 计算乘除法
        double left = visit(ctx.expression(0)); // 左操作数
        double right = visit(ctx.expression(1)); // 右操作数
        if (ctx.op.getText().equals("*")) {
            return left * right;
        } else {
            return left / right;
        }
    }

    @Override
    public Double visitAddSubExpression(CalculatorParser.AddSubExpressionContext ctx) {
        // 计算加减法
        double left = visit(ctx.expression(0)); // 左操作数
        double right = visit(ctx.expression(1)); // 右操作数
        if (ctx.op.getText().equals("+")) {
            return left + right;
        } else {
            return left - right;
        }
    }

    @Override
    public Double visitNumberExpression(CalculatorParser.NumberExpressionContext ctx) {
        // 返回数字值
        return Double.parseDouble(ctx.NUMBER().getText());
    }
}