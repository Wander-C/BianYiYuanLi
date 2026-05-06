public class ArrayType extends Type {
    private final Type contained; // type of its elements, may be int or array
    final int num_elements;

    public ArrayType(Type contained, int num_elements) {
        this.contained = contained;
        this.num_elements = num_elements;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

    @Override
    public String getTypeName() {
        return "";
    }
}
