import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Map<String, Type> symbolTable = new HashMap<>();
    final Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
    }

    // 严格检查当前作用域
    public boolean find(String name) {
        if(symbolTable.containsKey(name)) {
            return true;
        }
        return false;
    }

    // 原子性添加操作
    public void put(String name, Type type) {
        symbolTable.put(name, type);
    }

    // 递归查找（用于变量解析）
    public Type lookup(String name) {
        Type type = symbolTable.get(name);
        if (type != null) {
            return type;
        }
        return parent != null ? parent.lookup(name) : null;
    }

    // 作用域层级判断
    public boolean isGlobalScope() {
        return parent == null;
    }
}
