package PerformanceImprovedImplementation.PerformanceAdaptedStructures;

import BaseTemplateElements.FunctionalDependency;

public class DependencyOwningSpecificAttribute {
    private FunctionalDependency associatedDependency;
    private int countOfAssociationsOfClosureWithLeftSideAttributes;
    public DependencyOwningSpecificAttribute(FunctionalDependency associatedDependency){
        this.associatedDependency=associatedDependency;
        countOfAssociationsOfClosureWithLeftSideAttributes=0;
    }

    public void signalizeItsAttributeWasAssociatedWithClosure(){
        countOfAssociationsOfClosureWithLeftSideAttributes++;
    }

    public boolean isDerivableFromCurrentClosure(){
        return associatedDependency.getLeftAttributes().size()==countOfAssociationsOfClosureWithLeftSideAttributes;
    }

    public FunctionalDependency getAssociatedDependency() {
        return associatedDependency;
    }
}
