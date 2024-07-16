package CorrectnessBaseImplementation;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

public class RelationsOperator {
    private Set<FunctionalDependency> functionalDependencies;
    public RelationsOperator(Set<FunctionalDependency> functionalDependencies){
        this.functionalDependencies=functionalDependencies;
    }

    public int addNewAttributesToBuildingClosureAndReturnTheirCount(Set<String> buildingClosure){
        int startingCountOfAttributes=buildingClosure.size();
        for (FunctionalDependency currentDependency:functionalDependencies){
            if (buildingClosure.containsAll(currentDependency.setOfLeftAttributes()))
                buildingClosure.addAll(currentDependency.setOfRightAttributes());
        }
        int countOfAddedAttributes=buildingClosure.size()-startingCountOfAttributes;
        return countOfAddedAttributes;
    }
    public Attributes constructTransitiveClosure(Attributes baseAttributes){
        Set<String> buildingClosure= new HashSet<>(baseAttributes.setOfAttributes());
        int addedAttributes;
        do
            addedAttributes=addNewAttributesToBuildingClosureAndReturnTheirCount(buildingClosure);
        while (addedAttributes!=0);
        return new Attributes(buildingClosure);
    }

    public boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency){
        Attributes transitiveClosure=constructTransitiveClosure(possibleDependency.getLeftAttributes());
        return transitiveClosure.setOfAttributes().containsAll(possibleDependency.setOfRightAttributes());
    }


}
