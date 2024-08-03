package PerformanceImprovedImplementation;

import BaseTemplateElements.*;
import CorrectnessBaseImplementation.BernsteinAlgorithmImplementation;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.DependenciesLeftSidesGrouper;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Merging.GroupsAndBijectionsMerger;
import PerformanceImprovedImplementation.Merging.SetOfFunctionalDependencyImitatorBijectionsHolder;
import PerformanceImprovedImplementation.PerformanceAdaptedStructures.ListSet;
import PerformanceImprovedImplementation.PerformanceAdaptedStructures.OperationalHashBasedSetOfFunctionalDependencies;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BernsteinAlgorithmPerformanceImplementation extends BernsteinAlgorithmTemplate {

    public BernsteinAlgorithmPerformanceImplementation(Collection<FunctionalDependency> functionalDependencies) {
        super(functionalDependencies);
    }

    @Override
    public Set<FunctionalDependency> getSetOfFunctionalDependencies(Collection<FunctionalDependency> dependencies) {
        return new OperationalHashBasedSetOfFunctionalDependencies(dependencies,true);
    }

    @Override
    public Set<FunctionalDependency> eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        Set<FunctionalDependency> reducedLeftSideDependencies=new ListSet<>();
        OperationalHashBasedSetOfFunctionalDependencies baseDependencies=(OperationalHashBasedSetOfFunctionalDependencies) getFunctionalDependencies();

        for (FunctionalDependency dependency : baseDependencies) {
            FunctionalDependency minimalLeftSideDependency=baseDependencies.removeExtraneousAttributesFromLeftSideOfDependency(dependency);
            reducedLeftSideDependencies.add(minimalLeftSideDependency);
        }
        return reducedLeftSideDependencies;
    }

    @Override
    public Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes) {
        OperationalHashBasedSetOfFunctionalDependencies minimalCover=new OperationalHashBasedSetOfFunctionalDependencies(functionalDependenciesWithReducedLeftAttributes);
        minimalCover.transformItselfIntoMinimalCover();
        return minimalCover;
    }

    @Override
    public Map<Attributes, GroupOfFunctionalDependencies> groupDependenciesByLeftSides(Set<FunctionalDependency> minimalCover) {
        DependenciesLeftSidesGrouper groupingPerformer=new DependenciesLeftSidesGrouper(minimalCover);
        return groupingPerformer.group();
    }

    @Override
    public BijectionDependenciesAndGroups groupBijectionDependenciesAndMergeTheirGroups(Map<Attributes, GroupOfFunctionalDependencies> initialGroups) {
        Set<DependenciesGrouping> actualInitialGroups=((MapImitatorGroupsHolder)initialGroups).getGroups();
        GroupsAndBijectionsMerger merger=new GroupsAndBijectionsMerger(actualInitialGroups);
        return merger.mergeGroupsAndBijectionDependencies();
    }

    public void assignBackBijectionsToTheirGroups(Set<GroupDependency> bijections){
        for (GroupDependency bijection : bijections) {
            DependenciesGrouping associatedGroup=bijection.getAssociatedGroup();
            associatedGroup.add(bijection);
        }
    }
    @Override
    public Map<Attributes, GroupOfFunctionalDependencies> removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies) {
        Set<GroupDependency> bijections=((SetOfFunctionalDependencyImitatorBijectionsHolder)potentiallyPossibleTransitiveDependencies.bijectionsDependencies()).getHeldDependencies();
        Set<DependenciesGrouping> groups=((MapImitatorGroupsHolder)potentiallyPossibleTransitiveDependencies.groupsOfFunctionalDependencies()).getGroups();

        Set<FunctionalDependency> allGroupDependencies=
                groups.stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ListSet::new));

        OperationalHashBasedSetOfFunctionalDependencies allDependenciesWithBijections=
                new OperationalHashBasedSetOfFunctionalDependencies(allGroupDependencies);
        allDependenciesWithBijections.addAll(bijections);

        allDependenciesWithBijections.minimizeCoverOfGivenDependencies(allGroupDependencies);

        assignBackBijectionsToTheirGroups(bijections);

        return new MapImitatorGroupsHolder(groups);
    }

    @Override
    public Set<RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies) {
        Set<DependenciesGrouping> groups=((MapImitatorGroupsHolder)nonTransitiveDependencies).getGroups();
        Function<DependenciesGrouping,RelationalSchema> schemaCreator= group ->
                new RelationalSchema(new Attributes(
                        group.stream()
                                .flatMap(dependency->
                                        Stream.concat(dependency.getLeftAttributes().stream()
                                                ,dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,group.getAnyDependency().getLeftAttributes());


        Set<RelationalSchema> schemas=
                groups.stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(ListSet::new));

        return schemas;
    }
}
