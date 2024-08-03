package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.Structures.SimpleFunctionalDependency;

import java.util.Collection;

public class GroupDependency extends FunctionalDependency{
    private final FunctionalDependency actualDependency;
    private DependenciesGrouping associatedGroup;
    public GroupDependency(FunctionalDependency actualDependency) {
        super();
        this.actualDependency =actualDependency;
    }

    @Override
    public Attributes produceSetOfAttributesFromStrings(Collection<String> attributes) {
        throw new UnsupportedOperationException();
    }

    public GroupDependency(FunctionalDependency actualDependency, DependenciesGrouping associatedGroup){
        super();
        this.actualDependency=actualDependency;
        this.associatedGroup=associatedGroup;
    }

    public GroupDependency(Attributes leftAttributes,Attributes rightAttributes,DependenciesGrouping associatedGroup){
        super();
        actualDependency=new SimpleFunctionalDependency(leftAttributes,rightAttributes);
        this.associatedGroup=associatedGroup;
    }

    public boolean hasDependencySpecificAttributeOnLeftSide(Attribute specificAttribute){
        return getLeftAttributes().contains(specificAttribute);
    }

    public void assignGroup(DependenciesGrouping groupToAssociate){
        associatedGroup=groupToAssociate;
    }

    @Override
    public Attributes getLeftAttributes() {
        return actualDependency.getLeftAttributes();
    }

    @Override
    public Attributes getRightAttributes() {
        return actualDependency.getRightAttributes();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GroupDependency && actualDependency.equals(o);
    }

    @Override
    public int hashCode() {
        return actualDependency.hashCode();
    }

    @Override
    public String toString() {
        return actualDependency.toString();
    }

    public FunctionalDependency getCopy(){
        return new GroupDependency(actualDependency.getCopy(),associatedGroup);
    }

    public DependenciesGrouping getAssociatedGroup() {
        return associatedGroup;
    }
}
