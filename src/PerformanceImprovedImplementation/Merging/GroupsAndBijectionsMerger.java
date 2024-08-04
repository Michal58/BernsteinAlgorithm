package PerformanceImprovedImplementation.Merging;

import BaseTemplateElements.Attributes;
import CommonElements.ListSet;
import BaseTemplateElements.FunctionalDependency;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.OperationalSet.DependenciesOperationalSet;
import PerformanceImprovedImplementation.Structures.MergedGroupsAndBijectionsAsListSets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupsAndBijectionsMerger {
    private Set<DependenciesGrouping> groups;
    private int[][] derivationMatrix;
    private DependenciesGrouping[] orderedGroups;
    private Set<? extends FunctionalDependency> getDerivableDependencies(FunctionalDependency dependencyToCheck, DependenciesOperationalSet allDependencies){
        allDependencies.getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(dependencyToCheck.getLeftAttributes());
        return allDependencies.getDerivableDependencies();
    }

    private void assignIndexesToGroupsAndOrderThem(){
        int i=0;
        orderedGroups=new DependenciesGrouping[groups.size()];
        for (DependenciesGrouping group : groups) {
            group.assignDerivationIndex(i);
            orderedGroups[i]=group;
            i++;
        }
    }

    private void clearDerivationMatrix(){
        Arrays.stream(derivationMatrix)
                .forEach(row->
                        Arrays.fill(row, 0));
    }

    private void markDerivationExistence(int indexOfDerivingGroup,int indexOfDerivedGroup){
        derivationMatrix[indexOfDerivedGroup][indexOfDerivingGroup]=1;
    }
    private void markDerivedDependencies(DependenciesGrouping derivingGroup,Set<GroupDependency> derivedDependencies){
        int derivingIndex=derivingGroup.getIndexInDerivationMatrix();
        for (GroupDependency derivedDependency : derivedDependencies)
            markDerivationExistence(derivingIndex,derivedDependency.getAssociatedGroup().getIndexInDerivationMatrix());
    }

    private void fillDerivationMatrix(DependenciesOperationalSet allDependencies){
        for (DependenciesGrouping group : groups) {
            GroupDependency anyDependencyFromGroup = group.get(0);
            Set<GroupDependency> derivableDependencies = (Set<GroupDependency>) getDerivableDependencies(anyDependencyFromGroup, allDependencies);

            markDerivedDependencies(group, derivableDependencies);
        }
    }

    private void createAndInsertBijectionDependencies(DependenciesGrouping mergingGroup, DependenciesGrouping mergedGroup, Set<GroupDependency> bijectionDependencies){
        GroupDependency anyMergingGroupDependency=mergingGroup.getAnyDependency();
        GroupDependency anyMergedGroupDependency=mergedGroup.getAnyDependency();

        GroupDependency leftToRightDependency=new GroupDependency(
                anyMergingGroupDependency.getLeftAttributes()
                , anyMergedGroupDependency.getLeftAttributes()
                , mergingGroup
        );
        GroupDependency rightToLeftDependency=new GroupDependency(
                anyMergedGroupDependency.getLeftAttributes()
                , anyMergingGroupDependency.getLeftAttributes()
                , mergingGroup
        );

        bijectionDependencies.add(leftToRightDependency);
        bijectionDependencies.add(rightToLeftDependency);
    }

    private void removeAttributesFromDependenciesOnRightSidesWhichBelongsToGivenAttributesOrRemoveWholeDependency(DependenciesGrouping group,Attributes givenAttributes){
        Iterator<GroupDependency> dependencyIterator=group.iterator();
        while (dependencyIterator.hasNext()){
            GroupDependency currentDependency=dependencyIterator.next();
            currentDependency.getRightAttributes().removeIf(givenAttributes::contains);
            if (currentDependency.getRightAttributes().isEmpty())
                dependencyIterator.remove();
        }
    }

    private void removeUnnecessaryDependenciesAfterCreatingBijections(DependenciesGrouping mergingGroup, DependenciesGrouping mergedGroup){
        Attributes mergingGroupAttributes=mergingGroup.getAnyDependency().getLeftAttributes();
        Attributes mergedGroupAttributes=mergedGroup.getAnyDependency().getLeftAttributes();

        removeAttributesFromDependenciesOnRightSidesWhichBelongsToGivenAttributesOrRemoveWholeDependency(mergingGroup,mergedGroupAttributes);
        removeAttributesFromDependenciesOnRightSidesWhichBelongsToGivenAttributesOrRemoveWholeDependency(mergedGroup,mergingGroupAttributes);
    }

    private void performMergingIntoGroupAndUpdatingBijectionsAndRemovingUnnecessaryDependencies(DependenciesGrouping group, Set<GroupDependency> bijectionDependencies){
        int groupDerivationIndex= group.getIndexInDerivationMatrix();
        for (int i = 0; i < derivationMatrix.length; i++) {
            boolean isThereBijection=
                    i!=groupDerivationIndex
                    && derivationMatrix[groupDerivationIndex][i]==1
                    && derivationMatrix[i][groupDerivationIndex]==1;
            if (isThereBijection){
                DependenciesGrouping secondaryGroup=orderedGroups[i];
                createAndInsertBijectionDependencies(group,secondaryGroup,bijectionDependencies);
                removeUnnecessaryDependenciesAfterCreatingBijections(group,secondaryGroup);
                group.mergeSecondGroupByAddingItsElementsWithMarkingItAsMerged(secondaryGroup);
            }
        }
    }
    public GroupsAndBijectionsMerger(Set<DependenciesGrouping> groups){
        this.groups=groups;
    }

    public void prepareGroupsAndDerivationMatrixForProcessing(){
        assignIndexesToGroupsAndOrderThem();
        int countOfGroups=groups.size();
        derivationMatrix=new int[countOfGroups][countOfGroups];
        clearDerivationMatrix();
    }

    public MergedGroupsAndBijectionsAsListSets mergeGroupsAndBijectionDependencies(){
        DependenciesOperationalSet setOfAllDependencies=
                new DependenciesOperationalSet(
                        groups.stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                        ,true);

        prepareGroupsAndDerivationMatrixForProcessing();

        fillDerivationMatrix(setOfAllDependencies);

        ListSet<GroupDependency> bijections=new ListSet<>();
        ListSet<DependenciesGrouping> mergedGroups=new ListSet<>();

        for (DependenciesGrouping group : groups) {
            if (!group.hasBecomeMerged()) {
                performMergingIntoGroupAndUpdatingBijectionsAndRemovingUnnecessaryDependencies(group, bijections);
                mergedGroups.add(group);
            }
        }

        return new MergedGroupsAndBijectionsAsListSets(bijections,mergedGroups);
    }
}
