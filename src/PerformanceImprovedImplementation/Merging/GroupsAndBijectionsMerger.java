package PerformanceImprovedImplementation.Merging;

import BaseTemplateElements.Attributes;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import BaseTemplateElements.FunctionalDependency;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Structures.ListSet;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupsAndBijectionsMerger {
    private Set<DependenciesGrouping> groups;
    private int[][] derivationMatrix;
    private DependenciesGrouping[] orderedGroups;
    private Set<?> getDerivableDependencies(FunctionalDependency dependencyToCheck, DependenciesOperationalSet allDependencies){
        allDependencies.getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(dependencyToCheck.getLeftAttributes());
        return allDependencies.getDerivableDependencies();
    }

    private void assignIndexesToGroupsAndOrderThem(Set<DependenciesGrouping> groups){
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

    private void fillDerivationMatrix(Set<DependenciesGrouping> placedGroups, DependenciesOperationalSet allDependencies){
        for (DependenciesGrouping group : placedGroups) {
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
                ,anyMergedGroupDependency.getLeftAttributes()
                , mergingGroup
        );
        GroupDependency rightToLeftDependency=new GroupDependency(
                anyMergedGroupDependency.getLeftAttributes()
                ,anyMergingGroupDependency.getLeftAttributes()
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

    private void performMergingIntoGroupAndAddingBijectionDependenciesAndRemovingUnnecessaryDependencies(DependenciesGrouping group, Set<GroupDependency> bijectionDependencies){
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

    public BijectionDependenciesAndGroups mergeGroupsAndBijectionDependencies(){
        DependenciesOperationalSet setOfAllDependencies=
                new DependenciesOperationalSet(
                        groups.stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()),true);

        assignIndexesToGroupsAndOrderThem(groups);
        int countOfGroups=groups.size();
        derivationMatrix=new int[countOfGroups][countOfGroups];
        clearDerivationMatrix();

        fillDerivationMatrix(groups,setOfAllDependencies);

        Set<GroupDependency> bijections=new ListSet<>();
        Set<DependenciesGrouping> mergedGroups=new ListSet<>();

        for (DependenciesGrouping group : groups) {
            if (!group.checkIfHasBecomeMerged()) {
                performMergingIntoGroupAndAddingBijectionDependenciesAndRemovingUnnecessaryDependencies(group, bijections);
                mergedGroups.add(group);
            }
        }

        return new BijectionDependenciesAndGroups(new SetOfFunctionalDependencyImitatorBijectionsHolder(bijections),new MapImitatorGroupsHolder(mergedGroups));
    }
}
