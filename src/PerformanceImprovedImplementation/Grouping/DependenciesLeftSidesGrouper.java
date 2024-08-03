package PerformanceImprovedImplementation.Grouping;


import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import BaseTemplateElements.GroupOfFunctionalDependencies;
import PerformanceImprovedImplementation.PerformanceAdaptedStructures.ListSet;

import java.util.Map;
import java.util.Set;

public class DependenciesLeftSidesGrouper {
    private final Set<FunctionalDependency> dependencies;
    public DependenciesLeftSidesGrouper(Set<FunctionalDependency> dependencies){
        this.dependencies=dependencies;
    }
    public Map<Attributes, GroupOfFunctionalDependencies> group(){
        DependenciesGrouping firstLeftAttributesGroup=new DependenciesGrouping();
        dependencies.forEach(dependency ->
                firstLeftAttributesGroup.add(dependency instanceof GroupDependency?
                        (GroupDependency) dependency:
                        new GroupDependency(dependency)));

        ListSet<DependenciesGrouping> currentDependenciesGroupings= new ListSet<>();
        currentDependenciesGroupings.add(firstLeftAttributesGroup);

        Set<DependenciesGrouping> readyGroups=new ListSet<>();

        while (!currentDependenciesGroupings.isEmpty()){
            DependenciesGrouping groupToBeExtracted=currentDependenciesGroupings.removeFirst();
            groupToBeExtracted.transformIntoReadyGroupingOfFirstDependency(currentDependenciesGroupings);
            readyGroups.add(groupToBeExtracted);
        }

        return new MapImitatorGroupsHolder(readyGroups);
    }
}
