import java.util.ArrayList;
import java.util.List;

//todo:new
public class MyVisitor extends SysYParserBaseVisitor<Void> {
    List<Type> paramsTyList;
    String Btype;
    Type now;
    String FunctionNow;//现在在哪个函数内部
    Boolean right=true;//等号右侧是否有效
    Type LeftType;//等号左侧的type
    Boolean insideFuncParam=false;

    private Scope curScope=new Scope(null);
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {//FuncDef  → FuncType Ident '(' [FuncFParams] ')' Block
        String funcName = ctx.IDENT().getText();
        if (curScope.find(funcName)) { // curScope为当前的作用域
            OutputHelper.printSemanticError(ErrorType.REDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return null;
        }

        Type retType = VoidType.getVoidType();
        String typeStr = ctx.getChild(0).getText();
        if (typeStr.equals("int"))
            retType = IntType.getI32();     // 返回值类型为int32
        curScope=new Scope(curScope);
        paramsTyList=new ArrayList<Type>();
        if (ctx.funcFParams() != null) { // 如有入参，处理形参，添加形参信息等
            visit(ctx.funcFParams());
        }
        FunctionNow = ctx.IDENT().getText();
        FunctionType functionType = new FunctionType(retType, paramsTyList);
        //顶层作用域中压入此函数
        curScope.parent.put(funcName, functionType);
        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        for (int i = 0; i < ctx.funcFParam().size(); i ++) {
            visit(ctx.funcFParam(i));
        }
        return null;
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String btype = ctx.bType().getText();
        String funcParam = ctx.IDENT().getText();
        if(curScope.find(funcParam)) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
        }else {
            Type retType=IntType.getI32();
            int weidu=0;
            for(int i=0;i<ctx.getText().length();i++) {
                if(ctx.getText().charAt(i)=='[') {
                    weidu++;
                }
            }
            if(weidu!=0){
                retType=new ArrayType(retType,weidu);
            }
            paramsTyList.add(retType);
            curScope.put(funcParam, retType);
        }
        return null;
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        Btype=ctx.bType().getText();
        for(SysYParser.ConstDefContext c: ctx.constDef()) {
            visit(c);
        }
        return null;
    }//'const' bType constDef (',' constDef)* ';'

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        Type first=IntType.getI32();
        right=true;
        if(curScope.find(ctx.IDENT().getText())&&!(curScope.lookup(ctx.IDENT().getText()) instanceof FunctionType)) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            right=false;
        }else {
            if(ctx.constExp()!=null|| ctx.constExp().isEmpty()) {
                for(SysYParser.ConstExpContext c: ctx.constExp()) {
                    visit(c);
                }
                int weidu=ctx.constExp().size();
                if(weidu!=0){
                    first=new ArrayType(IntType.getI32(),weidu);
                }
            }
            if(first.equals(IntType.getI32())) {
                if(ctx.constInitVal()!=null) {
                    visit(ctx.constInitVal());
                    if(now!=IntType.getI32()) {
                        OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                    }
                }
            }
            curScope.put(ctx.IDENT().getText(),first);
        }

        return null;
    }//IDENT ('[' constExp ']')* '=' constInitVal

    @Override
    public Void visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        if(ctx.constExp()!=null) {
            visit(ctx.constExp());
        }else {
            now=new ArrayType(IntType.getI32(),1);
        }
        return null;
    }//constExp | '{' (constInitVal (',' constInitVal)*)? '}'




    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {

        // 判断当前块是否是函数体块（父节点是函数定义节点）
        boolean isFunctionBody = ctx.getParent() instanceof SysYParser.FuncDefContext;

        // 1. 创建新作用域（普通块需要新作用域，函数体块直接使用外层作用域）
        if(!isFunctionBody) {
            curScope = new Scope(curScope);
        }


        // 3. 处理块内语句
        ctx.blockItem().forEach(this::visit);

        // 4. 恢复作用域（仅普通块需要恢复）
        curScope = curScope.parent;

        return null;
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        Btype = ctx.bType().getText();
        for (int i = 0; i < ctx.varDef().size(); i ++) {
            visit(ctx.varDef(i)); // 依次visit def，即依次visit c=4 和 d=5
        }
        // return super.visitVarDecl(ctx);
        return null;
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        Type first=null;
        right=true;
        if((curScope.find(ctx.IDENT().getText())&&!(curScope.lookup(ctx.IDENT().getText()) instanceof FunctionType))||(curScope.isGlobalScope()&&curScope.find(ctx.IDENT().getText()))) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            right=false;
            return null;
        }else {
            if(ctx.getText().contains("[")) {
                int weidu=0;
                for(int i=0;i<ctx.getText().length();i++) {
                    if(ctx.getText().charAt(i)=='[') {
                        weidu++;
                    }
                    if(ctx.getText().charAt(i)=='=') {
                        break;
                    }
                }
                if(weidu==0) {
                    first=IntType.getI32();
                }else {
                    first=new ArrayType(IntType.getI32(),weidu);
                }
            }else{
                first=IntType.getI32();
            }
        }
        if(ctx.initVal()!=null&&right) {
            visit(ctx.initVal());
            if(first instanceof ArrayType) {

            }else if(now!=IntType.getI32()&&right) {
                OutputHelper.printSemanticError(ErrorType.INIT_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            }else if(ctx.initVal().getText().contains("{")&&right) {
                OutputHelper.printSemanticError(ErrorType.INIT_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            }
        }
        curScope.put(ctx.IDENT().getText(),first);
        return null;
    }

    @Override
    public Void visitInitVal(SysYParser.InitValContext ctx) {
        if (ctx.exp()!=null) {
            visit(ctx.exp());
        }
//        else{
//            now=new ArrayType(IntType.getI32(),1);
//        }
        return null;
    }//initVal: exp| '{' (initVal (',' initVal)*)? '}';

    @Override
    public Void visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        right=true;
        visit(ctx.lVal());
        Type first=now;
        LeftType=now;
        visit(ctx.exp());
        Type second=now;
        if(right) {
            if(first instanceof FunctionType) {
                OutputHelper.printSemanticError(ErrorType.FUN_VAR, ctx.getStart().getLine(), ctx.getText());
            }else if(first == null || second == null) {
                // 处理空值错误逻辑（如报错）
            }else if(!first.equals(second)) {
                if(first instanceof ArrayType && second instanceof ArrayType) {
                    if(((ArrayType) first).num_elements!=((ArrayType) second).num_elements){
                        OutputHelper.printSemanticError(ErrorType.INIT_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                    }
                }else {
                    OutputHelper.printSemanticError(ErrorType.INIT_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                }
            }
        }

        return null;
    }

    @Override
    public Void visitExpStmt(SysYParser.ExpStmtContext ctx) {
        right=true;
        if(ctx.exp()!=null) {
            visit(ctx.exp());
        }
        return null;
    }

    @Override
    public Void visitIfStmt(SysYParser.IfStmtContext ctx) {
        right=true;
        visit(ctx.cond());
        for (int i=0;i<ctx.stmt().size();i++) {
            visit(ctx.stmt(i));
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        right=true;
        FunctionType ft= (FunctionType) curScope.lookup(FunctionNow);
        now=VoidType.getVoidType();
        if(ctx.exp()!=null) {
            visit(ctx.exp());
        }
        if(!ft.getReturnType().equals(now)) {
            OutputHelper.printSemanticError(ErrorType.RET_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        right=true;
        visit(ctx.cond());
        visit(ctx.stmt());
        return null;
    }

    @Override
    public Void visitCond(SysYParser.CondContext ctx) {
        if (ctx.exp()!=null) {
            visit(ctx.exp());
        }else {
            visit(ctx.cond(0));
            Type first=now;
            if(first!=IntType.getI32()&&right) {
                OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                right=false;
                return null;
            }
            visit(ctx.cond(1));
            Type second=now;
            if(second!=IntType.getI32()&&right) {
                OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                right=false;
                return null;
            }
        }
        return null;
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        if (curScope.lookup(ctx.IDENT().getText()) == null&&right) {
            OutputHelper.printSemanticError(ErrorType.UNDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            right=false;
        } else {
            now = curScope.lookup(ctx.IDENT().getText());
            if ((ctx.exp().size() > 0 && now == IntType.getI32())&&right) {
                // 标量类型被下标访问（如 int a; a[0]）
                OutputHelper.printSemanticError(ErrorType.XB_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                right = false;
            } else if (now instanceof ArrayType) {
                ArrayType arrayType = (ArrayType) now;
                int definedDimensions = arrayType.num_elements;
                int accessedDimensions = 0;
                if(ctx.exp()!=null) {
                    accessedDimensions = ctx.exp().size();
                }

                if (accessedDimensions > definedDimensions) {
                    // 下标数量超过定义维度（如 int a[2]; a[1][2]）
                    OutputHelper.printSemanticError(ErrorType.XB_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                    right = false;
                } else if (accessedDimensions == definedDimensions) {
                    // 完全解引用为标量（如 int a[2][3]; a[1][2]）
                    now = IntType.getI32();
                } else {
                    // 部分解引用为子数组（如 int a[2][3]; a[1]）
                    int remainingDimensions = definedDimensions - accessedDimensions;
                    now = new ArrayType(IntType.getI32(), remainingDimensions);
                }
            } else if(now instanceof FunctionType && ctx.exp()!=null && !ctx.exp().isEmpty()&&right) {
                OutputHelper.printSemanticError(ErrorType.XB_NOT_MATCH, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                right = false;
            }
        }
        return null;
    }


    @Override
    public Void visitExp(SysYParser.ExpContext ctx) {
        if(ctx.IDENT() != null) {
            if(curScope.lookup(ctx.IDENT().getText())==null &&right) {
                OutputHelper.printSemanticError(ErrorType.UNDEF_FUNC, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                right=false;
            }else if(curScope.lookup(ctx.IDENT().getText())!=null&&!(curScope.lookup(ctx.IDENT().getText()) instanceof FunctionType)&&right) {
                OutputHelper.printSemanticError(ErrorType.VAR_FUN, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                right=false;
            }else if(curScope.lookup(ctx.IDENT().getText())instanceof FunctionType) {
                now = ((FunctionType) curScope.lookup(ctx.IDENT().getText())).getReturnType();
                List<Type> thisParamsTyList;
                insideFuncParam=true;
                if(ctx.funcRParams() != null) {
                    List<Type> get = ((FunctionType) curScope.lookup(ctx.IDENT().getText())).getParamTypes();
                    int size=0;
                    if(get==null) {
                        size=0;
                    }else {
                        size=get.size();
                    }
                    if(ctx.funcRParams().param().size()!=size) {
                        if(right){
                            OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                            right=false;
                            return null;
                        }
                    }else {
                        visit(ctx.funcRParams());
                        thisParamsTyList= paramsTyList;
                        if (!thisParamsTyList.isEmpty()) {
                            for (int i = 0; i < thisParamsTyList.size(); i++) {
                                Type zanshi =null;
                                if (get == null||get.size()<=i) {

                                } else{
                                    zanshi = get.get(i);
                                }
                                if (thisParamsTyList.get(i).equals(IntType.getI32())) {
                                    if (zanshi != IntType.getI32()&&right) {
                                        OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                                        right=false;
                                    }
                                } else if (thisParamsTyList.get(i) instanceof ArrayType) {
                                    int first = ((ArrayType) thisParamsTyList.get(i)).num_elements;
                                    if (zanshi == IntType.getI32()&&right) {
                                        OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                                        right=false;
                                    } else if (zanshi instanceof ArrayType) {
                                        int second = ((ArrayType) zanshi).num_elements;
                                        if (first != second &&right) {
                                            OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                                            right=false;
                                        }
                                    } else if (zanshi.equals(VoidType.getVoidType())&&right) {
                                        OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                                        right=false;
                                    }
                                } else if (thisParamsTyList.get(i) instanceof FunctionType&&right) {
                                    OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                                    right=false;
                                }
                            }
                        }
                    }
                }else{
                    List<Type> get = ((FunctionType) curScope.lookup(ctx.IDENT().getText())).getParamTypes();
                    int size=0;
                    if(get==null) {
                        size=0;
                    }else {
                        size=get.size();
                    }
                    if(size!=0&&right) {
                        OutputHelper.printSemanticError(ErrorType.FUNPARAM, ctx.getStart().getLine(), ctx.getText());
                        right=false;
                    }
                }
                now= ((FunctionType) curScope.lookup(ctx.IDENT().getText())).getReturnType();
                insideFuncParam=false;

            } else{
                now =curScope.lookup(ctx.IDENT().getText());
            }
        } else if(ctx.lVal() != null) {
            if(curScope.lookup(ctx.lVal().IDENT().getText())==null&&right) {
                OutputHelper.printSemanticError(ErrorType.UNDEF_VAR, ctx.lVal().IDENT().getSymbol().getLine(), ctx.lVal().IDENT().getText());
                right=false;
            } else{
                visit(ctx.lVal());
            }
        }else if(ctx.MUL() != null||ctx.DIV() != null||ctx.MOD() != null||ctx.MINUS() != null||ctx.PLUS() != null) {
            visit(ctx.exp(0));
            Type first=now;
            if(first != IntType.getI32()&&right) {
                OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                right=false;
                return null;
            }
            visit(ctx.exp(1));
            Type second=now;
            if(first!=second&&right) {
                OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                right=false;
            }
        }else if(ctx.number() != null) {
            now = IntType.getI32();
        }else if(ctx.unaryOp() != null) {
            now=null;
            visit(ctx.exp(0));
            if(now!=IntType.getI32()&&right) {
                OutputHelper.printSemanticError(ErrorType.EXP_NOT_MATCH, ctx.getStart().getLine(), ctx.getText());
                right=false;
            }
        }else if(ctx.exp() != null && !ctx.exp().isEmpty()) {
            visit(ctx.exp(0));
        }
        return null;
    }



    @Override
    public Void visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        List thisParamsTyList= paramsTyList;
        paramsTyList=new ArrayList<>();
        for (SysYParser.ParamContext context:ctx.param()){
            visit(context);
        }
            paramsTyList=thisParamsTyList;
        return null;
    }

    @Override
    public Void visitParam(SysYParser.ParamContext ctx) {
        visit(ctx.exp());
        paramsTyList.add(now);
        return null;
    }

}
