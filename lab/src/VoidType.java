public class VoidType extends Type {
    // 单例模式
    private static final VoidType INSTANCE = new VoidType();
    private VoidType() {}

    public static VoidType getVoidType() { return INSTANCE; }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public String getTypeName() {
        return "void";
    }
}

