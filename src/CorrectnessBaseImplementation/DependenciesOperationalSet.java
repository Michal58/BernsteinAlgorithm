package CorrectnessBaseImplementation;

import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DependenciesOperationalSet extends HashSet<FunctionalDependency> implements CommonElements.DependenciesOperationalSet {
    public DependenciesOperationalSet(){
        super();
    }
    public DependenciesOperationalSet(Collection<FunctionalDependency> dependencies){
        super(dependencies);
    }

    public int addNewAttributesToBuildingClosureAndReturnTheirCount(Set<Attribute> buildingClosure){
        int startingCountOfAttributes=buildingClosure.size();
        for (FunctionalDependency currentDependency:this){
            if (buildingClosure.containsAll(currentDependency.getLeftAttributes()))
                buildingClosure.addAll(currentDependency.getRightAttributes());
        }
        int countOfAddedAttributes=buildingClosure.size()-startingCountOfAttributes;
        return countOfAddedAttributes;
    }

    public Attributes constructTransitiveClosure(Attributes baseAttributes){
        Set<Attribute> buildingClosure= new HashSet<>(baseAttributes);
        int addedAttributes;
        do
            addedAttributes=addNewAttributesToBuildingClosureAndReturnTheirCount(buildingClosure);
        while (addedAttributes!=0);
        return new AttributesHashSet(buildingClosure);
    }

    public boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency){
        Attributes transitiveClosure=constructTransitiveClosure(possibleDependency.getLeftAttributes());
        return transitiveClosure.containsAll(possibleDependency.getRightAttributes());
    }
}
