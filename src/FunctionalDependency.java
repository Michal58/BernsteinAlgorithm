import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public final class FunctionalDependency {
    private final HashSet<String> leftAttributes;
    private final HashSet<String> rightAttributes;
    public FunctionalDependency(Collection<String> leftAttributes,Collection<String> rightAttributes){
        this.leftAttributes=new HashSet<>(leftAttributes);
        this.rightAttributes=new HashSet<>(rightAttributes);
    }

    public HashSet<String> getLeftAttributes() {
        return leftAttributes;
    }

    public HashSet<String> getRightAttributes() {
        return rightAttributes;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FunctionalDependency
                && this.leftAttributes.equals(((FunctionalDependency) o).leftAttributes)
                && this.rightAttributes.equals(((FunctionalDependency) o).rightAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftAttributes, rightAttributes);
    }
}
