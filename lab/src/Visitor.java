import org.antlr.v4.runtime.tree.*;

import java.util.ArrayList;
import java.util.List;

public class Visitor extends SysYParserBaseVisitor<Void> {
    private final StringBuilder formattedCode = new StringBuilder();
    private int indentLevel = 0;
    private boolean inBlock = false;

    public String getFormattedCode() {
        return formattedCode.toString().trim();
    }

    private void applyIndent() {
        for (int i = 0; i < indentLevel; i++) {
            formattedCode.append("    ");
        }
    }

    @Override
    public Void visit(ParseTree tree) {
        tree.accept(this);
        return null;
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        applyIndent();
        formattedCode.append(ctx.CONST().getText()).append(" ");
        visit(ctx.bType());
        List<SysYParser.ConstDefContext> params = new ArrayList<>();
        params=ctx.constDef();
        for (SysYParser.ConstDefContext param : params) {
            visit(param);
            formattedCode.append(", ");
        }
        formattedCode.deleteCharAt(formattedCode.length()-1);
        formattedCode.deleteCharAt(formattedCode.length()-1);
        formattedCode.append(ctx.SEMICOLON());
        formattedCode.append("\n");
        return null;
    }//4 'const' BType ConstDef { ',' ConstDef } ';'

    @Override
    public Void visitBType(SysYParser.BTypeContext ctx) {
        formattedCode.append(ctx.getText()).append(' ');
        return null;
    }//5 'int'


    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        formattedCode.append(ctx.IDENT().getText());
        if(ctx.getText().contains("[")) {
            List<SysYParser.ConstExpContext> params = new ArrayList<>();
            params=ctx.constExp();
            for (SysYParser.ConstExpContext param : params) {
                formattedCode.append('[');
                visit(param);
                formattedCode.append(']');
            }
        }
        formattedCode.append(' ').append(ctx.ASSIGN().getText()).append(' ');
        visit(ctx.constInitVal());
        return null;
    }//6 Ident { '[' ConstExp ']' } '=' ConstInitVal

    public Void visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        if (ctx.constExp() != null) {
            // 处理简单的ConstExp情况
            visit(ctx.constExp());
        } else if (ctx.getText().contains("{")) {
            // 处理复合结构的情况
            formattedCode.append("{");
            indentLevel++;
            // 遍历子ConstInitVal
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if (ctx.getChild(i) instanceof SysYParser.ConstInitValContext) {
                    visit((SysYParser.ConstInitValContext) ctx.getChild(i));
                    if (i < ctx.getChildCount() - 2) {
                        formattedCode.append(", ");
                    }
                }
            }
            indentLevel--;
            formattedCode.append("}");
        }
        return null;
    }//7 ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'todo

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
//        formattedCode.append("这里有一个变量声明");
        applyIndent();
        visit(ctx.bType());
        if(ctx.getText().contains(",")) {
            for (SysYParser.VarDefContext para : ctx.varDef()) {
                visit(para);
                formattedCode.append(", ");
            }
            formattedCode.deleteCharAt(formattedCode.length()-1);
            formattedCode.deleteCharAt(formattedCode.length()-1);
        }else {
            visit(ctx.varDef(0));
        }
        visit(ctx.SEMICOLON());
        formattedCode.append("\n");
        return null;
    }//8.VarDecl  → BType VarDef { ',' VarDef } ';'



    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        visit(ctx.IDENT());
//        formattedCode.append("这里可能有维数");
        if (ctx.getText().contains("[")) {
            for (SysYParser.ConstExpContext c: ctx.constExp()) {
                formattedCode.append("[");
                visit(c);
                formattedCode.append("]");
            }
        }
        if(ctx.initVal() != null) {
            formattedCode.append(" = ");
            visit(ctx.initVal());
        }
        return null;
    }//10.VarDef  → Ident { '[' ConstExp ']' } '=' InitVal

    @Override
    public Void visitInitVal(SysYParser.InitValContext ctx) {
        if(ctx.exp() != null) {
            visit(ctx.exp());
        }else {
            formattedCode.append("{");
            for (SysYParser.InitValContext param : ctx.initVal()) {
                visit(param);
                formattedCode.append(", ");
            }
            if(formattedCode.charAt(formattedCode.length()-1)==' ') {
                formattedCode.deleteCharAt(formattedCode.length()-1);
                formattedCode.deleteCharAt(formattedCode.length()-1);
            }
            formattedCode.append("}");
        }
        return null;
    }//11.InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'todo

    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        if(formattedCode.length()>0) {
            formattedCode.append("\n");
        }
        applyIndent();
        formattedCode.append(ctx.funcType().getText()).append(' ')
                .append(ctx.IDENT().getText()).append("(");
        // Check if funcFParams is not null before visiting
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
        }
        formattedCode.append(") ");
        inBlock = true;
        visit(ctx.block());
        inBlock = false;
        return null;
    }//12.FuncDef  → FuncType Ident '(' [FuncFParams] ')' Block

    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        if(ctx.getText().contains(",")) {
            List<SysYParser.FuncFParamContext> a= ctx.funcFParam();
            for (SysYParser.FuncFParamContext param : a) {
                visit(param);
                formattedCode.append(", ");
            }
            formattedCode.deleteCharAt(formattedCode.length()-1);
            formattedCode.deleteCharAt(formattedCode.length()-1);
        }else {
            visit(ctx.funcFParam(0));
        }
        return null;
    }//14.FuncFParams → FuncFParam { ',' FuncFParam }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        visit(ctx.bType());
        formattedCode.append(ctx.IDENT().getText());

        // 检查是否存在数组维度
        if (ctx.getText().contains("[")) {
            // 获取所有数组维度节点
            if(ctx.exp() != null) {
                List<SysYParser.ExpContext> arrayDims = ctx.exp();
                formattedCode.append("[]");
                for (SysYParser.ExpContext dim : arrayDims) {
                    if (dim != null) {
                        formattedCode.append('[');
                        visit(dim);
                        formattedCode.append(']');
                    }
                }
            }
        }
        return null;
    }//15.FuncFParam  → BType Ident ['[' ']' { '[' Exp ']' }]todo

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        if(formattedCode.charAt(formattedCode.length()-1)=='\n') {
            applyIndent();
        }
        formattedCode.append("{\n");
        indentLevel++;
        for (int i = 0; i < ctx.getChildCount()-2; i++) {
            visit(ctx.blockItem(i));
        }
        indentLevel--;
        applyIndent();
        formattedCode.append("}\n");
        return null;
    }//16.Block  → '{' { BlockItem } '}'

    @Override
    public Void visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        applyIndent();
        visit(ctx.lVal());
        formattedCode.append(" = ");
        visit(ctx.exp());
        formattedCode.append(";\n");
        return null;
    }//18.赋值语句

    @Override
    public Void visitExpStmt(SysYParser.ExpStmtContext ctx) {
        applyIndent();
        if(ctx.exp() != null) {
            visit(ctx.exp());
        }
        formattedCode.append(";\n");
        return null;
    }//19.[Exp] ';

    @Override
    public Void visitBlockStmt(SysYParser.BlockStmtContext ctx) {
        visit(ctx.block());
        return null;
    }//20.| Block

    @Override
    public Void visitIfStmt(SysYParser.IfStmtContext ctx) {
        if(formattedCode.charAt(formattedCode.length()-1)=='\n') {
            applyIndent();
        }
        formattedCode.append("if (");
        visit(ctx.cond());
        formattedCode.append(")");
        if(ctx.stmt(0).getText().charAt(0)=='{') {
            formattedCode.append(" ");
            visit(ctx.stmt(0));
        }else {
            indentLevel++;
            formattedCode.append("\n");
            visit(ctx.stmt(0));
            indentLevel--;
        }
        if (ctx.ELSE()!=null) {
            applyIndent();
            formattedCode.append("else");
            if(ctx.stmt(1).getText().startsWith("if")) {
                formattedCode.append(" ");
                visit(ctx.stmt(1));
            }else if(ctx.stmt(1).getText().charAt(0)=='{') {
                formattedCode.append(" ");
                visit(ctx.stmt(1));
            }else {
                indentLevel++;
                formattedCode.append("\n");
                visit(ctx.stmt(1));
                indentLevel--;
            }
        }
        return null;
    }//21.if语句if' (' Cond ')' Stmt [ 'else' Stmt ]

    @Override
    public Void visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        applyIndent();
        formattedCode.append("while (");
        visit(ctx.cond());
        formattedCode.append(")");
        if(ctx.stmt().getText().charAt(0)!='{') {
            formattedCode.append("\n");
            indentLevel++;
            visit(ctx.stmt());
            indentLevel--;
        }else {
            formattedCode.append(" ");
            visit(ctx.stmt());
        }
        return null;
    }//22.'while' '(' Cond ')' Stmt

    @Override
    public Void visitBreakStmt(SysYParser.BreakStmtContext ctx) {
        applyIndent();
        formattedCode.append("break;\n");
        return null;
    }//23.break语句;
    @Override
    public Void visitContinueStmt(SysYParser.ContinueStmtContext ctx) {
        applyIndent();
        formattedCode.append("continue;\n");
        return null;
    }//24.continue语句

    @Override
    public Void visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        applyIndent();
        formattedCode.append("return");
        if (ctx.exp() != null) {  // 更准确的表达式存在判断
            formattedCode.append(" ");
            visit(ctx.exp());     // 访问表达式
            formattedCode.append(";\n");
        } else {
            formattedCode.append(";\n"); // 无表达式直接补全分号
        }
        return null;

    }//25.return语句return' [Exp] ';'

    @Override
    public Void visitExp(SysYParser.ExpContext ctx) {
//        // 处理括号表达式
        if (ctx.L_PAREN() != null&&ctx.exp() != null &&ctx.IDENT() == null) {
//            formattedCode.append("这里是个（exp）");
            formattedCode.append("(");
            List<SysYParser.ExpContext> a=ctx.exp();
            for(SysYParser.ExpContext exp : a) {
                visit(exp);
            }
            formattedCode.append(")");
        }
        else if (ctx.lVal() != null) {
//             formattedCode.append("访问子树");
            visit(ctx.lVal()); // 递归访问lVal子树
        }// 处理一元运算符
        // 处理数字字面量
        else if (ctx.number() != null) {
            visit(ctx.number()); // 递归访问number子树
        }
        else if (ctx.IDENT() != null && ctx.L_PAREN() != null) {
//             formattedCode.append("这里是个函数");
            formattedCode.append(ctx.IDENT().getText()).append("(");
            if (ctx.funcRParams() != null) visit(ctx.funcRParams()); // 处理参数
            formattedCode.append(")");
        }else if (ctx.unaryOp() != null) {
            formattedCode.append(ctx.unaryOp().getText());
            visit(ctx.exp(0)); // 访问一元表达式
        }
//        // 处理二元运算符（确保子节点存在）
        else  {
//            formattedCode.append("这里有二元运算");
            visit(ctx.exp(0)); // 左操作数
//             formattedCode.append("前面为左操作数");
            formattedCode.append(" ").append(ctx.getChild(1).getText()).append(" ");
            visit(ctx.exp(1)); // 右操作数
        }
//        formattedCode.append("结束");
//
//
        return null;
    }

    @Override
    public Void visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        visit(ctx.param(0));
        if(ctx.param().size()>1) {
            for(int i=1;i<ctx.param().size();i++) {
                formattedCode.append(", ");
                visit(ctx.param(i));
            }
        }
        return null;
    }//param (COMMA param)*

    @Override
    public Void visitParam(SysYParser.ParamContext ctx) {
        visit(ctx.exp());
        return null;
    }//exp;

    @Override
    public Void visitCond(SysYParser.CondContext ctx) {
        if(ctx.exp() != null) {
            visit(ctx.exp());
        }else {
            visit(ctx.cond(0));
            formattedCode.append(" ").append(ctx.getChild(1).getText()).append(" ");
            visit(ctx.cond(1));

        }
        return null;
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        formattedCode.append(ctx.IDENT().getText());
        if(ctx.exp() != null) {
            for(SysYParser.ExpContext exp : ctx.exp()) {
                formattedCode.append("[");
                visit(exp);
                formattedCode.append("]");
            }
        }

        return null;
    }



    @Override
    public Void visitTerminal(TerminalNode node) {
        // 添加EOF过滤逻辑
        if (node.getSymbol().getType() != SysYParser.EOF) {
            String text = node.getText();
            formattedCode.append(text);
        }
        return null;
    }


    @Override
    public Void visitErrorNode(ErrorNode node) {
        return null;
    }
}