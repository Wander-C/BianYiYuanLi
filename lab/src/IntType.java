public class IntType extends Type {
    private final int bits; // 位宽

    // 私有构造器实现单例模式
    public IntType(int bits) { this.bits = bits; }

    // 提供常用实例
    private static final IntType I32 = new IntType(32);

    public static IntType getI32() { return I32; }

    @Override
    public boolean equals(Object other) {
        return this == other; // 单例实例可直接比较地址
    }

    @Override
    public String getTypeName() {
        return "i" + bits;
    }
}