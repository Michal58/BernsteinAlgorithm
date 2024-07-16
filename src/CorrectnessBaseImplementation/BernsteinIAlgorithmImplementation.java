package CorrectnessBaseImplementation;

import BaseTemplateElements.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BernsteinIAlgorithmImplementation extends BernsteinAlgorithmTemplate {

    public BernsteinIAlgorithmImplementation(Set<FunctionalDependency> functionalDependencies) {
        super(functionalDependencies);
    }

    @Override
    public Set<FunctionalDependency> eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        Set<FunctionalDependency> reducedLeftSideDependencies=new HashSet<>();
        Set<FunctionalDependency> actualDependencies=getFunctionalDependencies();
        RelationsOperator operator=new RelationsOperator(actualDependencies);

        for (FunctionalDependency dependency : actualDependencies) {
            FunctionalDependency copyOfDependency=dependency.getCopy();
            for (String leftAttribute : dependency.setOfLeftAttributes()) {
                copyOfDependency.setOfLeftAttributes().remove(leftAttribute);
                boolean areTransitiveClosuresTheSame=
                        operator.constructTransitiveClosure(copyOfDependency.getLeftAttributes())
                        .equals(operator.constructTransitiveClosure(dependency.getLeftAttributes()));
                if (!areTransitiveClosuresTheSame)
                    copyOfDependency.setOfLeftAttributes().add(leftAttribute);
            }
            reducedLeftSideDependencies.add(copyOfDependency);
        }
        return reducedLeftSideDependencies;
    }

    public Set<FunctionalDependency> splitRightSideOfFunctionalDependency(FunctionalDependency dependencyToSplit){
        Set<FunctionalDependency> splitDependencies=new HashSet<>();
        for (String rightAttribute : dependencyToSplit.setOfRightAttributes()) {
            Set<String> rightAttributeAsSet=new HashSet<>(Collections.singletonList(rightAttribute));
            splitDependencies.add(new FunctionalDependency(dependencyToSplit.setOfLeftAttributes(),rightAttributeAsSet));
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
        Set<FunctionalDependency> doubleSideSplitDependencies=transformDependenciesWithSplittingRightSides(functionalDependenciesWithReducedLeftAttributes);
        Set<FunctionalDependency> minimalCover=new HashSet<>(doubleSideSplitDependencies);
        for (FunctionalDependency doubleSideSplitDependency : doubleSideSplitDependencies) {
            minimalCover.remove(doubleSideSplitDependency);
            RelationsOperator modifiedDependenciesOperator=new RelationsOperator(minimalCover);
            Attributes removedDependencyClosure=modifiedDependenciesOperator.constructTransitiveClosure(doubleSideSplitDependency.getLeftAttributes());
            boolean doesDependencyStillAppear= removedDependencyClosure
                    .setOfAttributes()
                    .containsAll(doubleSideSplitDependency.setOfRightAttributes());
            if (!doesDependencyStillAppear)
                minimalCover.add(doubleSideSplitDependency);
        }
        return minimalCover;
    }

    @Override
    public Map<Attributes, GroupOfFunctionalDependencies> groupMinimalCoverByLeftSides(Set<FunctionalDependency> minimalCover) {
        Map<Attributes,GroupOfFunctionalDependencies> leftAttributesDictionary=new HashMap<>();
        for (FunctionalDependency dependency : minimalCover) {
            if (!leftAttributesDictionary.containsKey(dependency.getLeftAttributes())) {
                Attributes newKey=new Attributes(dependency.getLeftAttributes().setOfAttributes());
                Set<FunctionalDependency> setOfSingleDependency=new HashSet<>(List.of(dependency));
                leftAttributesDictionary.put(newKey,new GroupOfFunctionalDependencies(setOfSingleDependency));
            }
            else {
                GroupOfFunctionalDependencies createdGroup=leftAttributesDictionary.get(dependency.getLeftAttributes());
                createdGroup.functionalDependencies().add(dependency);
            }
        }

        return leftAttributesDictionary;
    }

    public void groupBijectionDependencies(Set<FunctionalDependency> groupOfBijections,FunctionalDependency leftToRightDependency,FunctionalDependency rightToLeftDependency){
        groupOfBijections.add(leftToRightDependency);
        groupOfBijections.add(rightToLeftDependency);
    }

    public void removeDependenciesWithRightSidesWhichArePartsOfBijectionAttributes(Collection<FunctionalDependency> dependenciesFromGroup,Attributes bijectionAttributes){
        Iterator<FunctionalDependency> dependencyIterator=dependenciesFromGroup.iterator();
        while (dependencyIterator.hasNext()) {
            FunctionalDependency currentDependency=dependencyIterator.next();
            boolean isRightSideOfCurrentDependencyPartOfBijectionAttributes =
                    bijectionAttributes.setOfAttributes().containsAll(currentDependency.setOfRightAttributes());
            if (isRightSideOfCurrentDependencyPartOfBijectionAttributes)
                dependencyIterator.remove();
        }
    }

    public void updateGroup(GroupOfFunctionalDependencies firstGroup, GroupOfFunctionalDependencies secondGroup){
        firstGroup.functionalDependencies().addAll(secondGroup.functionalDependencies());
    }
    public void mergeGroupsOfDependenciesByUpdatingGroupOfFirstAttributes(Map<Attributes,GroupOfFunctionalDependencies> groups, Attributes firstBijectionAttributes, Attributes secondBijectionAttributes){
        GroupOfFunctionalDependencies leftSideGroup=groups.get(firstBijectionAttributes);
        GroupOfFunctionalDependencies rightSideGroup=groups.get(secondBijectionAttributes);

        removeDependenciesWithRightSidesWhichArePartsOfBijectionAttributes(leftSideGroup.functionalDependencies(),secondBijectionAttributes);
        removeDependenciesWithRightSidesWhichArePartsOfBijectionAttributes(rightSideGroup.functionalDependencies(),firstBijectionAttributes);

        updateGroup(leftSideGroup,rightSideGroup);
        groups.remove(secondBijectionAttributes);
    }

    @Override
    public BijectionDependenciesAndGroups groupBijectionDependenciesAndMergeTheirGroups(Map<Attributes, GroupOfFunctionalDependencies> initialGroups) {
        //at this stage all dependencies at their right side have set of only one attribute
        Set<FunctionalDependency> bijectionDependencies=new HashSet<>();

        LinkedList<Attributes> leftSides= new LinkedList<>(initialGroups.keySet());
        Iterator<Attributes> iteratorOfLeftSides=leftSides.iterator();

        Set<FunctionalDependency> allDependencies= initialGroups
                .values()
                .stream()
                .flatMap(group -> group.functionalDependencies().stream())
                .collect(Collectors.toSet());
        RelationsOperator operator=new RelationsOperator(allDependencies);

        while (iteratorOfLeftSides.hasNext()){
            Attributes potentialBijectionLeftSide=iteratorOfLeftSides.next();
            iteratorOfLeftSides.remove();
            for (Attributes potentialBijectionRightSide : leftSides) {
                FunctionalDependency leftToRightDependency=
                        new FunctionalDependency(potentialBijectionLeftSide.setOfAttributes(),potentialBijectionRightSide.setOfAttributes());
                boolean isThereDependencyFromLeftToRight=operator.checkIfThereIsTransitiveDependency(leftToRightDependency);
                FunctionalDependency rightToLeftDependency=
                        new FunctionalDependency(potentialBijectionRightSide.setOfAttributes(),potentialBijectionLeftSide.setOfAttributes());
                boolean isThereDependencyFromRightToLeft=operator.checkIfThereIsTransitiveDependency(rightToLeftDependency);
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
        groupsOfDependencies.removeIf(group->group.functionalDependencies().isEmpty());
    }
    public void assignBijectionDependenciesToTheirGroups(Set<FunctionalDependency> bijectionDependencies,Map<Attributes,GroupOfFunctionalDependencies> allGroups){
        for (FunctionalDependency bijectionDependency : bijectionDependencies) {
            Attributes currentLeftAttributes=bijectionDependency.getLeftAttributes();
            Attributes currentRightAttributes=bijectionDependency.getRightAttributes();

            // at this point there must exist group which have identical left or right side like in bijection dependency

            if (allGroups.containsKey(currentLeftAttributes))
                allGroups.get(currentLeftAttributes).functionalDependencies().add(bijectionDependency);
            else
                allGroups.get(currentRightAttributes).functionalDependencies().add(bijectionDependency);
        }
        removeAllEmptyGroups(allGroups.values());
    }
    @Override
    public Map<Attributes,GroupOfFunctionalDependencies> removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies) {
        Set<FunctionalDependency> bijectionDependencies=potentiallyPossibleTransitiveDependencies.bijectionsDependencies();
        Set<FunctionalDependency> allDependencies=
                potentiallyPossibleTransitiveDependencies
                        .groupsOfFunctionalDependencies()
                        .values()
                        .stream()
                        .flatMap(group->group.functionalDependencies().stream())
                        .collect(Collectors.toSet());
        allDependencies.addAll(bijectionDependencies);

        RelationsOperator operator=new RelationsOperator(allDependencies);
        for (GroupOfFunctionalDependencies groupOfDependencies : potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies().values()) {
            Iterator<FunctionalDependency> dependenciesInGroupIterator = groupOfDependencies.functionalDependencies().iterator();
            while (dependenciesInGroupIterator.hasNext()) {
                FunctionalDependency currentDependency = dependenciesInGroupIterator.next();
                allDependencies.remove(currentDependency);
                boolean areDependenciesKept = operator.checkIfThereIsTransitiveDependency(currentDependency);
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
                                .functionalDependencies()
                                .stream()
                                .flatMap(dependency->Stream.concat(dependency.setOfLeftAttributes().stream(),dependency.setOfRightAttributes().stream()))
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
