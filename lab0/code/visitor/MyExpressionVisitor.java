

public class MyExpressionVisitor extends ExpressionBaseVisitor<Integer> {

    @Override
    public Integer visitProg(ExpressionParser.ProgContext ctx) {
        ExpressionParser.ExprContext expr = ctx.expr();
        return visit(expr);
    }
    
    @Override
    public Integer visitAddSub(ExpressionParser.AddSubContext ctx) {
        Integer left = visit(ctx.left);
        Integer right = visit(ctx.right);
        String op = ctx.op.getText();
        if (op.equals("+")) {
            return left + right;
        } else if (op.equals("-")) {
            return left - right;
        }
        
        return 0;
    }

    @Override
    public Integer visitMulDiv(ExpressionParser.MulDivContext ctx) {
        Integer left = visit(ctx.left);
        Integer right = visit(ctx.right);
        String op = ctx.op.getText();
        if (op.equals("*")) {
            return left * right;
        } else if (op.equals("/")) {
            return left / right;
        }
        
        return 0;
    }

    @Override
    public Integer visitParen(ExpressionParser.ParenContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitInt(ExpressionParser.IntContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }
}