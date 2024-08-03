package CorrectnessBaseImplementation;

import BaseTemplateElements.*;
import CorrectnessBaseImplementation.Structures.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BernsteinAlgorithmImplementation extends BernsteinAlgorithmTemplate {

    public BernsteinAlgorithmImplementation(Collection<FunctionalDependency> functionalDependencies) {
        super(functionalDependencies);
    }

    @Override
    public AlgorithmState getFirstStateFromGivenDependencies(Collection<FunctionalDependency> dependencies) {
        return new DependenciesOperationalSet(dependencies);
    }

    @Override
    public AlgorithmState eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        Set<FunctionalDependency> reducedLeftSideDependencies=new DependenciesOperationalSet();
        DependenciesOperationalSet baseDependencies=(DependenciesOperationalSet) getStateOfAlgorithm();

        for (FunctionalDependency dependency : baseDependencies) {
            FunctionalDependency copyOfDependency=dependency.getCopy();
            for (Attribute leftAttribute : dependency.getLeftAttributes()) {
                copyOfDependency.getLeftAttributes().remove(leftAttribute);
                boolean wasLeftAttributeExtraneous=baseDependencies.checkIfThereIsTransitiveDependency(copyOfDependency);
                if (!wasLeftAttributeExtraneous)
                    copyOfDependency.getLeftAttributes().add(leftAttribute);
            }
            reducedLeftSideDependencies.add(copyOfDependency);
        }
        return (AlgorithmState) reducedLeftSideDependencies;
    }

    public Set<FunctionalDependency> splitRightSideOfFunctionalDependency(FunctionalDependency dependencyToSplit){
        Set<FunctionalDependency> splitDependencies=new HashSet<>();
        for (Attribute rightAttribute : dependencyToSplit.getRightAttributes()) {
            Attributes rightAttributeAsSet=new AttributesHashSet(Collections.singletonList(rightAttribute));
            splitDependencies.add(new SimpleFunctionalDependency(dependencyToSplit.getLeftAttributes(),rightAttributeAsSet));
        }
        return splitDependencies;
    }

    public Set<FunctionalDependency> transformDependenciesWithSplittingRightSides(Set<FunctionalDependency> dependencies){
        Set<FunctionalDependency> dependenciesWithSplitRightSides=new HashSet<>();
        for (FunctionalDependency dependency : dependencies)
            dependenciesWithSplitRightSides.addAll(splitRightSideOfFunctionalDependency(dependency));
        return dependenciesWithSplitRightSides;
    }

    @Override
    public AlgorithmState findMinimalCoverOfFunctionalDependencies() {
        Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes=(DependenciesOperationalSet) getStateOfAlgorithm();
        Set<FunctionalDependency> doubleSideReducedDependencies=transformDependenciesWithSplittingRightSides(functionalDependenciesWithReducedLeftAttributes);
        DependenciesOperationalSet minimalCover=new DependenciesOperationalSet(doubleSideReducedDependencies);

        for (FunctionalDependency doubleSideReducedDependency : doubleSideReducedDependencies) {
            minimalCover.remove(doubleSideReducedDependency);
            boolean doesDependencyStillAppear= minimalCover.checkIfThereIsTransitiveDependency(doubleSideReducedDependency);
            if (!doesDependencyStillAppear)
                minimalCover.add(doubleSideReducedDependency);
        }
        return minimalCover;
    }

    @Override
    public AlgorithmState groupDependenciesByLeftSides() {
        Set<FunctionalDependency> minimalCover= (DependenciesOperationalSet) getStateOfAlgorithm();
        Map<Attributes,GroupOfFunctionalDependencies> leftAttributesDictionary=new GroupingAsDictionary();
        for (FunctionalDependency dependency : minimalCover) {
            if (!leftAttributesDictionary.containsKey(dependency.getLeftAttributes())) {
                Attributes newKey=new AttributesHashSet(dependency.getLeftAttributes());
                Set<FunctionalDependency> setOfSingleDependency=new HashSet<>(List.of(dependency));
                leftAttributesDictionary.put(newKey,new HashGroupOfFunctionalDependencies(setOfSingleDependency));
            }
            else {
                GroupOfFunctionalDependencies createdGroup=leftAttributesDictionary.get(dependency.getLeftAttributes());
                createdGroup.add(dependency);
            }
        }

        return (AlgorithmState) leftAttributesDictionary;
    }

    public void groupBijectionDependencies(Set<FunctionalDependency> groupOfBijections,FunctionalDependency leftToRightDependency,FunctionalDependency rightToLeftDependency){
        groupOfBijections.add(leftToRightDependency);
        groupOfBijections.add(rightToLeftDependency);
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
    public void mergeGroupsOfDependenciesByUpdatingGroupOfFirstAttributes(Map<Attributes,GroupOfFunctionalDependencies> groups, Attributes firstBijectionAttributes, Attributes secondBijectionAttributes){
        GroupOfFunctionalDependencies leftSideGroup=groups.get(firstBijectionAttributes);
        GroupOfFunctionalDependencies rightSideGroup=groups.get(secondBijectionAttributes);

        removeDependenciesWhichRightSidesArePartsOfOppositeBijectionAttributes(leftSideGroup,secondBijectionAttributes);
        removeDependenciesWhichRightSidesArePartsOfOppositeBijectionAttributes(rightSideGroup,firstBijectionAttributes);

        updateGroup(leftSideGroup,rightSideGroup);
        groups.remove(secondBijectionAttributes);
    }

    @Override
    public AlgorithmState groupBijectionDependenciesAndMergeTheirGroups() {
        //at this stage all dependencies at their right side have set of only one attribute
        Map<Attributes, GroupOfFunctionalDependencies> initialGroups=(GroupingAsDictionary) getStateOfAlgorithm();
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

    public void removeAllEmptyGroups(Collection<GroupOfFunctionalDependencies> groupsOfDependencies){
        groupsOfDependencies.removeIf(Set::isEmpty);
    }
    public void assignBijectionDependenciesToTheirGroups(Set<FunctionalDependency> bijectionDependencies,Map<Attributes,GroupOfFunctionalDependencies> allGroups){
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
    @Override
    public AlgorithmState removeTransitiveDependencies() {
        BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies=(BijectionDependenciesAndGroups) getStateOfAlgorithm();
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

    @Override
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies() {
        Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies=(GroupingAsDictionary) getStateOfAlgorithm();
        Function<Attributes,RelationalSchema> schemaCreator=attributes ->
                new RelationalSchema(new AttributesHashSet(
                        nonTransitiveDependencies
                                .get(attributes)
                                .stream()
                                .flatMap(dependency->Stream.concat(dependency.getLeftAttributes().stream(),dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                ,attributes);
        Set<RelationalSchema> schemas=
                nonTransitiveDependencies
                        .keySet()
                        .stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalSchemasSet::new));

        return (AlgorithmState) schemas;
    }
}
