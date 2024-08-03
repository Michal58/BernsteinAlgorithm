package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.Attribute;
import PerformanceImprovedImplementation.Structures.ListSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class DependenciesGrouping extends ListSet<GroupDependency> {
    private final static int INDEX_NOT_ASSIGNED=-1;
    private boolean hasBecomeMerged;
    private int indexInDerivationMatrix;
    public DependenciesGrouping(){
        super();
        indexInDerivationMatrix=INDEX_NOT_ASSIGNED;
        hasBecomeMerged=false;
    }
    public DependenciesGrouping(Collection<GroupDependency> dependencies){
        super(dependencies);
        indexInDerivationMatrix=INDEX_NOT_ASSIGNED;
        hasBecomeMerged=false;
        assignAllDependenciesToThisGroup(dependencies);
    }
    public void assignAllDependenciesToThisGroup(Collection<? extends GroupDependency> dependencies){
        dependencies.forEach(dependency ->
                dependency.assignGroup(this));
    }

    @Override
    public boolean add(GroupDependency groupDependency) {
        groupDependency.assignGroup(this);
        return super.add(groupDependency);
    }

    @Override
    public boolean addAll(Collection<? extends GroupDependency> c) {
        assignAllDependenciesToThisGroup(c);
        return super.addAll(c);
    }
    public DependenciesGrouping removeDependenciesNotHavingSpecificAttribute(Attribute specificAttribute){
        DependenciesGrouping removedDependencies=new DependenciesGrouping();
        Iterator<GroupDependency> allDependenciesIterator=iterator();
        while (allDependenciesIterator.hasNext()) {
            GroupDependency currentDependency=allDependenciesIterator.next();
            if (!currentDependency.hasDependencySpecificAttributeOnLeftSide(specificAttribute)){
                allDependenciesIterator.remove();
                removedDependencies.add(currentDependency);
            }
        }

        return removedDependencies;
    }

    public void transformIntoReadyGroupingOfFirstDependency(Set<DependenciesGrouping> containerForNonMatchingGroups){
        GroupDependency firstDependency=get(0);
        for (Attribute leftAttribute : firstDependency.getLeftAttributes()) {
            DependenciesGrouping nonMatchingGroup=this.removeDependenciesNotHavingSpecificAttribute(leftAttribute);
            if (!nonMatchingGroup.isEmpty())
                containerForNonMatchingGroups.add(nonMatchingGroup);
        }
    }

    public void assignDerivationIndex(int index){
        this.indexInDerivationMatrix=index;
    }

    private void markAsMerged(){
        hasBecomeMerged=true;
    }
    public int getIndexInDerivationMatrix() {
        return indexInDerivationMatrix;
    }

    public boolean checkIfHasBecomeMerged(){
        return hasBecomeMerged;
    }
    public void assignAsMerged(){
        hasBecomeMerged=true;
    }
    public void mergeSecondGroupByAddingItsElementsWithMarkingItAsMerged(DependenciesGrouping groupToMerge){
        groupToMerge.markAsMerged();
        addAll(groupToMerge);
    }

    public GroupDependency getAnyDependency(){
        return get(0);
    }
}
