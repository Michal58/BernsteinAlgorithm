package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

public class GroupDependency extends FunctionalDependency{
    private final FunctionalDependency actualDependency;
    private DependenciesGrouping associatedGroup;
    public GroupDependency(FunctionalDependency actualDependency) {
        super(NullContent.CONFIRMATION);
        this.actualDependency =actualDependency;
    }
    public GroupDependency(FunctionalDependency actualDependency, DependenciesGrouping associatedGroup){
        super(NullContent.CONFIRMATION);
        this.actualDependency=actualDependency;
        this.associatedGroup=associatedGroup;
    }

    public GroupDependency(Attributes leftAttributes,Attributes rightAttributes,DependenciesGrouping associatedGroup){
        super(NullContent.CONFIRMATION);
        actualDependency=new FunctionalDependency(leftAttributes,rightAttributes);
        this.associatedGroup=associatedGroup;
    }

    public boolean hasDependencySpecificAttributeOnLeftSide(String specificAttribute){
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
