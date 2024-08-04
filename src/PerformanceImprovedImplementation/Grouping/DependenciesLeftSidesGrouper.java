package PerformanceImprovedImplementation.Grouping;


import BaseTemplateElements.FunctionalDependency;
import CommonElements.ListSet;

import java.util.Set;
import java.util.stream.Collectors;

public class DependenciesLeftSidesGrouper {
    private final Set<FunctionalDependency> dependencies;
    public DependenciesLeftSidesGrouper(Set<FunctionalDependency> dependencies){
        this.dependencies=dependencies;
    }
    public GroupDependency keepOrCreateGroupDependency(FunctionalDependency dependency){
        return dependency instanceof GroupDependency?
                (GroupDependency) dependency:
                new GroupDependency(dependency);
    }

    public ListSet<DependenciesGrouping> group(){
        DependenciesGrouping firstLeftAttributesGroup=
                dependencies.stream()
                .map(this::keepOrCreateGroupDependency)
                .collect(Collectors.toCollection(DependenciesGrouping::new));

        ListSet<DependenciesGrouping> currentDependenciesGroupings= new ListSet<>();
        currentDependenciesGroupings.add(firstLeftAttributesGroup);

        ListSet<DependenciesGrouping> readyGroups=new ListSet<>();

        while (!currentDependenciesGroupings.isEmpty()){
            DependenciesGrouping nextGroupForExtraction=currentDependenciesGroupings.removeFirst();
            nextGroupForExtraction.transformIntoReadyGroupingOfFirstDependency(currentDependenciesGroupings);
            readyGroups.add(nextGroupForExtraction);
        }

        return readyGroups;
    }
}
