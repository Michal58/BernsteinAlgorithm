package BaseTemplateElements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class FunctionalDependency {
    private final Attributes leftAttributes;
    private final Attributes rightAttributes;
    public FunctionalDependency(Collection<String> leftAttributes,Collection<String> rightAttributes){
        this.leftAttributes=new Attributes(produceSetOfAttributes(leftAttributes));
        this.rightAttributes=new Attributes(produceSetOfAttributes(rightAttributes));
    }

    public Set<String> produceSetOfAttributes(Collection<String> attributes){
        return new HashSet<>(attributes);
    }

    public Attributes getLeftAttributes() {
        return leftAttributes;
    }

    public Attributes getRightAttributes() {
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

    @Override
    public String toString() {
        return String.format("%s->%s",leftAttributes,rightAttributes);
    }

    public FunctionalDependency getCopy(){
        return new FunctionalDependency(leftAttributes,rightAttributes);
    }
}
