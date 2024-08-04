package PerformanceImprovedImplementation.TransitiveDependenciesRemoving;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.FunctionalDependency;
import CommonElements.ListSet;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;
import PerformanceImprovedImplementation.Structures.MergedGroupsAndBijectionsAsListSets;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TransitiveDependenciesRemover {
    public void assignBackBijectionsToTheirGroups(Set<GroupDependency> bijections){
        for (GroupDependency bijection : bijections) {
            DependenciesGrouping associatedGroup=bijection.getAssociatedGroup();
            associatedGroup.add(bijection);
        }
    }
    public AlgorithmState removeTransitiveDependencies(MergedGroupsAndBijectionsAsListSets potentiallyPossibleTransitiveDependencies) {
        ListSet<GroupDependency> bijections=potentiallyPossibleTransitiveDependencies.bijectionsDependencies();
        ListSet<DependenciesGrouping> groups=potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies();

        Set<FunctionalDependency> allGroupDependencies=
                groups.stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ListSet::new));

        DependenciesOperationalSet allDependenciesWithBijections=
                new DependenciesOperationalSet(allGroupDependencies);
        allDependenciesWithBijections.addAll(bijections);

        allDependenciesWithBijections.minimizeCoverOfGivenDependencies(allGroupDependencies);

        assignBackBijectionsToTheirGroups(bijections);

        return groups;
    }
}
