package BaseTemplateElements;

import java.util.Collection;
import java.util.Objects;

public abstract class FunctionalDependency {
    private Attributes leftAttributes;
    private Attributes rightAttributes;
    public enum ConstructorOverloader {
        TO_REASSIGN
    }

    public FunctionalDependency(){
        leftAttributes=null;
        rightAttributes=null;
    }

    public FunctionalDependency(FunctionalDependency dependencyToReassign,Object ignoredAny){
        assignAttributesOfOtherDependency(dependencyToReassign);
    }

    public FunctionalDependency(FunctionalDependency dependencyToCopy){
        FunctionalDependency copyOfDependency=dependencyToCopy.getCopy();
        assignAttributesOfOtherDependency(copyOfDependency);
    }

    public FunctionalDependency(Collection<String> leftAttributes,Collection<String> rightAttributes){
        this.leftAttributes= produceSetOfAttributesFromStrings(leftAttributes);
        this.rightAttributes= produceSetOfAttributesFromStrings(rightAttributes);
    }

    public FunctionalDependency(Attributes leftAttributes,Attributes rightAttributes){
        this.leftAttributes=leftAttributes.shallowCopy();
        this.rightAttributes=rightAttributes.shallowCopy();
    }
    public void assignAttributesOfOtherDependency(FunctionalDependency otherDependency){
        this.leftAttributes= otherDependency.getLeftAttributes();
        this.rightAttributes= otherDependency.getRightAttributes();
    }

    public abstract Attributes produceSetOfAttributesFromStrings(Collection<String> attributes);

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

    public abstract FunctionalDependency getCopy();
}
