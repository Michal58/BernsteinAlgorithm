package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.Collection;

public class GroupDependency extends FunctionalDependency{
    private DependenciesGrouping associatedGroup;
    public GroupDependency(FunctionalDependency actualDependency) {
        super(actualDependency, ConstructorOverloader.TO_REASSIGN);
    }

    @Override
    public Attributes produceSetOfAttributesFromStrings(Collection<String> attributes) {
        throw new UnsupportedOperationException();
    }

    public GroupDependency(FunctionalDependency actualDependency, DependenciesGrouping associatedGroup){
        super(actualDependency,ConstructorOverloader.TO_REASSIGN);
        this.associatedGroup=associatedGroup;
    }

    public GroupDependency(Attributes leftAttributes,Attributes rightAttributes,DependenciesGrouping associatedGroup){
        super(leftAttributes,rightAttributes);
        this.associatedGroup=associatedGroup;
    }

    public boolean hasDependencySpecificAttributeOnLeftSide(Attribute specificAttribute){
        return getLeftAttributes().contains(specificAttribute);
    }

    public void assignGroup(DependenciesGrouping groupToAssociate){
        associatedGroup=groupToAssociate;
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof GroupDependency && super.equals(o);
    }

    @Override
    public FunctionalDependency getCopy(){
        return new GroupDependency(this,associatedGroup);
    }

    public DependenciesGrouping getAssociatedGroup() {
        return associatedGroup;
    }
}
