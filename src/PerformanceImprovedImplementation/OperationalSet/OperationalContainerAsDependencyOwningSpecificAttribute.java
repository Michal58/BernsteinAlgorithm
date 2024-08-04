package PerformanceImprovedImplementation.OperationalSet;

import BaseTemplateElements.FunctionalDependency;

public class OperationalContainerAsDependencyOwningSpecificAttribute {
    private FunctionalDependency associatedDependency;
    private int countOfAssociationsOfClosureWithLeftSideAttributes;
    public OperationalContainerAsDependencyOwningSpecificAttribute(FunctionalDependency associatedDependency){
        this.associatedDependency=associatedDependency;
        countOfAssociationsOfClosureWithLeftSideAttributes=0;
    }

    public void signalizeItsLeftAttributeWasAssociatedWithClosure(){
        countOfAssociationsOfClosureWithLeftSideAttributes++;
    }

    public boolean isDerivableFromCurrentClosure(){
        return associatedDependency.getLeftAttributes().size()==countOfAssociationsOfClosureWithLeftSideAttributes;
    }

    public FunctionalDependency getAssociatedDependency() {
        return associatedDependency;
    }
}
