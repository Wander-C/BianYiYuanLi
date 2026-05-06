import java.util.List;

public class FunctionType extends Type {
    private final Type returnType;
    private final List<Type> paramTypes;

    public FunctionType(Type returnType, List<Type> paramTypes) {
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FunctionType)) return false;
        FunctionType o = (FunctionType) other;
        return returnType.equals(o.returnType) &&
                paramTypes.equals(o.paramTypes);
    }

    @Override
    public String getTypeName() {
        return returnType.getTypeName() ;
    }

    // 添加访问方法
    public Type getReturnType() { return returnType; }
    public List<Type> getParamTypes() { return paramTypes; }
}