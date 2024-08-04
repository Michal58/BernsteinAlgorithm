package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.DependenciesOperationalSet;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TransitiveDependenciesRemover {
    public void removeAllEmptyGroups(Collection<GroupOfFunctionalDependencies> groupsOfDependencies){
        groupsOfDependencies.removeIf(Set::isEmpty);
    }
    public void assignBijectionDependenciesToTheirGroups(Set<FunctionalDependency> bijectionDependencies, Map<Attributes,GroupOfFunctionalDependencies> allGroups){
        for (FunctionalDependency bijectionDependency : bijectionDependencies) {
            Attributes currentLeftAttributes=bijectionDependency.getLeftAttributes();
            Attributes currentRightAttributes=bijectionDependency.getRightAttributes();

            // at this point there must exist group which have identical left or right side like in bijection dependency

            if (allGroups.containsKey(currentLeftAttributes))
                allGroups.get(currentLeftAttributes).add(bijectionDependency);
            else
                allGroups.get(currentRightAttributes).add(bijectionDependency);
        }
        removeAllEmptyGroups(allGroups.values());
    }
    public AlgorithmState removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies) {
        Set<FunctionalDependency> bijectionDependencies=potentiallyPossibleTransitiveDependencies.bijectionsDependencies();
        DependenciesOperationalSet allDependencies=
                potentiallyPossibleTransitiveDependencies
                        .groupsOfFunctionalDependencies()
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(DependenciesOperationalSet::new));
        allDependencies.addAll(bijectionDependencies);

        for (GroupOfFunctionalDependencies groupOfDependencies : potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies().values()) {
            Iterator<FunctionalDependency> dependenciesInGroupIterator = groupOfDependencies.iterator();
            while (dependenciesInGroupIterator.hasNext()) {
                FunctionalDependency currentDependency = dependenciesInGroupIterator.next();
                allDependencies.remove(currentDependency);
                boolean areDependenciesKept = allDependencies.checkIfThereIsTransitiveDependency(currentDependency);
                if (!areDependenciesKept)
                    allDependencies.add(currentDependency);
                else
                    dependenciesInGroupIterator.remove();
            }
        }

        assignBijectionDependenciesToTheirGroups(bijectionDependencies,potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies());
        return (AlgorithmState) potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies();
    }
}
