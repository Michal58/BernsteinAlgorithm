package CorrectnessBaseImplementation;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OperationalFunctionalDependencies extends HashSet<FunctionalDependency>{
    public OperationalFunctionalDependencies(){
        super();
    }
    public OperationalFunctionalDependencies(Collection<FunctionalDependency> dependencies){
        super(dependencies);
    }

    public int addNewAttributesToBuildingClosureAndReturnTheirCount(Set<String> buildingClosure){
        int startingCountOfAttributes=buildingClosure.size();
        for (FunctionalDependency currentDependency:this){
            if (buildingClosure.containsAll(currentDependency.getLeftAttributes()))
                buildingClosure.addAll(currentDependency.getRightAttributes());
        }
        int countOfAddedAttributes=buildingClosure.size()-startingCountOfAttributes;
        return countOfAddedAttributes;
    }

    public Attributes constructTransitiveClosure(Attributes baseAttributes){
        Set<String> buildingClosure= new HashSet<>(baseAttributes);
        int addedAttributes;
        do
            addedAttributes=addNewAttributesToBuildingClosureAndReturnTheirCount(buildingClosure);
        while (addedAttributes!=0);
        return new Attributes(buildingClosure);
    }

    public boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency){
        Attributes transitiveClosure=constructTransitiveClosure(possibleDependency.getLeftAttributes());
        return transitiveClosure.containsAll(possibleDependency.getRightAttributes());
    }
}
