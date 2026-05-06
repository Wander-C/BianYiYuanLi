public abstract class Type {

    // 类型比较抽象方法
    public abstract boolean equals(Object other);

    // 可添加其他公共方法，如获取类型名称等
    public abstract String getTypeName();
}