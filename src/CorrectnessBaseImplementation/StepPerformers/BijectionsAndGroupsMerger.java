package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.DependenciesOperationalSet;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;
import CorrectnessBaseImplementation.Structures.SimpleFunctionalDependency;

import java.util.*;
import java.util.stream.Collectors;

public class BijectionsAndGroupsMerger {
    public void groupBijectionDependencies(Set<FunctionalDependency> groupOfBijections,FunctionalDependency leftToRightDependency,FunctionalDependency rightToLeftDependency){
        groupOfBijections.add(leftToRightDependency);
        groupOfBijections.add(rightToLeftDependency);
    }

    public void mergeGroupsOfDependenciesByUpdatingGroupOfFirstAttributes(Map<Attributes,GroupOfFunctionalDependencies> groups, Attributes firstBijectionAttributes, Attributes secondBijectionAttributes){
        GroupOfFunctionalDependencies leftSideGroup=groups.get(firstBijectionAttributes);
        GroupOfFunctionalDependencies rightSideGroup=groups.get(secondBijectionAttributes);

        removeDependenciesWhichRightSidesArePartsOfOppositeBijectionAttributes(leftSideGroup,secondBijectionAttributes);
        removeDependenciesWhichRightSidesArePartsOfOppositeBijectionAttributes(rightSideGroup,firstBijectionAttributes);

        updateGroup(leftSideGroup,rightSideGroup);
        groups.remove(secondBijectionAttributes);
    }

    public void removeDependenciesWhichRightSidesArePartsOfOppositeBijectionAttributes(Collection<FunctionalDependency> dependenciesFromGroup, Attributes oppositeBijectionAttributes){
        Iterator<FunctionalDependency> dependencyIterator=dependenciesFromGroup.iterator();
        while (dependencyIterator.hasNext()) {
            FunctionalDependency currentDependency=dependencyIterator.next();
            boolean isRightSideOfCurrentDependencyPartOfBijectionAttributes =
                    oppositeBijectionAttributes.containsAll(currentDependency.getRightAttributes());
            if (isRightSideOfCurrentDependencyPartOfBijectionAttributes)
                dependencyIterator.remove();
        }
    }

    public void updateGroup(GroupOfFunctionalDependencies firstGroup, GroupOfFunctionalDependencies secondGroup){
        firstGroup.addAll(secondGroup);
    }

    public BijectionDependenciesAndGroups groupBijectionDependenciesAndMergeTheirGroups(Map<Attributes, GroupOfFunctionalDependencies> initialGroups) {
        //at this stage all dependencies at their right side have set of only one attribute
        Set<FunctionalDependency> bijectionDependencies=new HashSet<>();

        LinkedList<Attributes> leftSides= new LinkedList<>(initialGroups.keySet());
        Iterator<Attributes> iteratorOfLeftSides=leftSides.iterator();

        DependenciesOperationalSet allDependencies= initialGroups
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(DependenciesOperationalSet::new));

        while (iteratorOfLeftSides.hasNext()){
            Attributes potentialBijectionLeftSide=iteratorOfLeftSides.next();
            iteratorOfLeftSides.remove();
            for (Attributes potentialBijectionRightSide : leftSides) {
                FunctionalDependency leftToRightDependency=
                        new SimpleFunctionalDependency(potentialBijectionLeftSide,potentialBijectionRightSide);
                boolean isThereDependencyFromLeftToRight=allDependencies.checkIfThereIsTransitiveDependency(leftToRightDependency);
                FunctionalDependency rightToLeftDependency=
                        new SimpleFunctionalDependency(potentialBijectionRightSide,potentialBijectionLeftSide);
                boolean isThereDependencyFromRightToLeft=allDependencies.checkIfThereIsTransitiveDependency(rightToLeftDependency);
                boolean isThereBijection=isThereDependencyFromLeftToRight&&isThereDependencyFromRightToLeft;
                if (isThereBijection){
                    groupBijectionDependencies(bijectionDependencies,leftToRightDependency,rightToLeftDependency);
                    mergeGroupsOfDependenciesByUpdatingGroupOfFirstAttributes(initialGroups,potentialBijectionLeftSide,potentialBijectionRightSide);
                }
            }
        }

        BijectionDependenciesAndGroups mergedGroupsAndBijections=new BijectionDependenciesAndGroups(bijectionDependencies, initialGroups);
        return mergedGroupsAndBijections;
    }
}
