package CorrectnessBase;

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
            if (buildingClosure.containsAll(currentDependency.getLeftAttributes()))
                buildingClosure.addAll(currentDependency.getRightAttributes());
        }
        int countOfAddedAttributes=buildingClosure.size()-startingCountOfAttributes;
        return countOfAddedAttributes;
    }
    public Set<String> constructTransitiveClosure(Set<String> baseAttributes){
        Set<String> buildingClosure= new HashSet<>(baseAttributes);
        int addedAttributes;
        do
            addedAttributes=addNewAttributesToBuildingClosureAndReturnTheirCount(buildingClosure);
        while (addedAttributes!=0);
        return buildingClosure;
    }


}
