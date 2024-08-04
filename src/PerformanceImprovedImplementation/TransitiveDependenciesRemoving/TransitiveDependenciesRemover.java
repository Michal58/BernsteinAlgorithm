package PerformanceImprovedImplementation.TransitiveDependenciesRemoving;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Merging.SetOfFunctionalDependencyImitatorBijectionsHolder;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;
import PerformanceImprovedImplementation.Structures.ListSet;

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
    public AlgorithmState removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies) {
        Set<GroupDependency> bijections=((SetOfFunctionalDependencyImitatorBijectionsHolder)potentiallyPossibleTransitiveDependencies.bijectionsDependencies()).getHeldDependencies();
        Set<DependenciesGrouping> groups=((MapImitatorGroupsHolder)potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies()).getGroups();

        Set<FunctionalDependency> allGroupDependencies=
                groups.stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ListSet::new));

        DependenciesOperationalSet allDependenciesWithBijections=
                new DependenciesOperationalSet(allGroupDependencies);
        allDependenciesWithBijections.addAll(bijections);

        allDependenciesWithBijections.minimizeCoverOfGivenDependencies(allGroupDependencies);

        assignBackBijectionsToTheirGroups(bijections);

        return new MapImitatorGroupsHolder(groups);
    }
}
