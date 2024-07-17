package CorrectnessBaseImplementation;

import BaseTemplateElements.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BernsteinAlgorithmImplementation extends BernsteinAlgorithmTemplate {

    public BernsteinAlgorithmImplementation(Set<FunctionalDependency> functionalDependencies) {
        super(functionalDependencies);
    }

    @Override
    public Set<FunctionalDependency> getSetOfFunctionalDependencies(Collection<FunctionalDependency> dependencies) {
        return new OperationalFunctionalDependencies(dependencies);
    }

    @Override
    public Set<FunctionalDependency> eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        Set<FunctionalDependency> reducedLeftSideDependencies=new OperationalFunctionalDependencies();
        OperationalFunctionalDependencies baseDependencies=(OperationalFunctionalDependencies) getFunctionalDependencies();

        for (FunctionalDependency dependency : baseDependencies) {
            FunctionalDependency copyOfDependency=dependency.getCopy();
            for (String leftAttribute : dependency.getLeftAttributes()) {
                copyOfDependency.getLeftAttributes().remove(leftAttribute);
                boolean wasLeftAttributeExtraneous=baseDependencies.checkIfThereIsTransitiveDependency(copyOfDependency);
                if (!wasLeftAttributeExtraneous)
                    copyOfDependency.getLeftAttributes().add(leftAttribute);
            }
            reducedLeftSideDependencies.add(copyOfDependency);
        }
        return reducedLeftSideDependencies;
    }

    public Set<FunctionalDependency> splitRightSideOfFunctionalDependency(FunctionalDependency dependencyToSplit){
        Set<FunctionalDependency> splitDependencies=new HashSet<>();
        for (String rightAttribute : dependencyToSplit.getRightAttributes()) {
            Set<String> rightAttributeAsSet=new HashSet<>(Collections.singletonList(rightAttribute));
            splitDependencies.add(new FunctionalDependency(dependencyToSplit.getLeftAttributes(),rightAttributeAsSet));
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
    public Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes) {
        Set<FunctionalDependency> doubleSideReducedDependencies=transformDependenciesWithSplittingRightSides(functionalDependenciesWithReducedLeftAttributes);
        OperationalFunctionalDependencies minimalCover=new OperationalFunctionalDependencies(doubleSideReducedDependencies);

        for (FunctionalDependency doubleSideReducedDependency : doubleSideReducedDependencies) {
            minimalCover.remove(doubleSideReducedDependency);
            boolean doesDependencyStillAppear= minimalCover.checkIfThereIsTransitiveDependency(doubleSideReducedDependency);
            if (!doesDependencyStillAppear)
                minimalCover.add(doubleSideReducedDependency);
        }
        return minimalCover;
    }

    @Override
    public Map<Attributes, GroupOfFunctionalDependencies> groupDependenciesByLeftSides(Set<FunctionalDependency> minimalCover) {
        Map<Attributes,GroupOfFunctionalDependencies> leftAttributesDictionary=new HashMap<>();
        for (FunctionalDependency dependency : minimalCover) {
            if (!leftAttributesDictionary.containsKey(dependency.getLeftAttributes())) {
                Attributes newKey=new Attributes(dependency.getLeftAttributes());
                Set<FunctionalDependency> setOfSingleDependency=new HashSet<>(List.of(dependency));
                leftAttributesDictionary.put(newKey,new GroupOfFunctionalDependencies(setOfSingleDependency));
            }
            else {
                GroupOfFunctionalDependencies createdGroup=leftAttributesDictionary.get(dependency.getLeftAttributes());
                createdGroup.add(dependency);
            }
        }

        return leftAttributesDictionary;
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
    public BijectionDependenciesAndGroups groupBijectionDependenciesAndMergeTheirGroups(Map<Attributes, GroupOfFunctionalDependencies> initialGroups) {
        //at this stage all dependencies at their right side have set of only one attribute
        Set<FunctionalDependency> bijectionDependencies=new HashSet<>();

        LinkedList<Attributes> leftSides= new LinkedList<>(initialGroups.keySet());
        Iterator<Attributes> iteratorOfLeftSides=leftSides.iterator();

        OperationalFunctionalDependencies allDependencies= initialGroups
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(OperationalFunctionalDependencies::new));

        while (iteratorOfLeftSides.hasNext()){
            Attributes potentialBijectionLeftSide=iteratorOfLeftSides.next();
            iteratorOfLeftSides.remove();
            for (Attributes potentialBijectionRightSide : leftSides) {
                FunctionalDependency leftToRightDependency=
                        new FunctionalDependency(potentialBijectionLeftSide,potentialBijectionRightSide);
                boolean isThereDependencyFromLeftToRight=allDependencies.checkIfThereIsTransitiveDependency(leftToRightDependency);
                FunctionalDependency rightToLeftDependency=
                        new FunctionalDependency(potentialBijectionRightSide,potentialBijectionLeftSide);
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
        groupsOfDependencies.removeIf(HashSet::isEmpty);
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
    public Map<Attributes,GroupOfFunctionalDependencies> removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies) {
        Set<FunctionalDependency> bijectionDependencies=potentiallyPossibleTransitiveDependencies.bijectionsDependencies();
        OperationalFunctionalDependencies allDependencies=
                potentiallyPossibleTransitiveDependencies
                        .groupsOfFunctionalDependencies()
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(OperationalFunctionalDependencies::new));
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
        return potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies();
    }

    @Override
    public Set<RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies) {
        Function<Attributes,RelationalSchema> schemaCreator=attributes ->
                new RelationalSchema(new Attributes(
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
                        .collect(Collectors.toSet());

        return schemas;
    }
}
