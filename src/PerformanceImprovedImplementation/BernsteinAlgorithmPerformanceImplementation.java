package PerformanceImprovedImplementation;

import BaseTemplateElements.*;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.DependenciesLeftSidesGrouper;
import PerformanceImprovedImplementation.Grouping.GroupDependency;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Merging.GroupsAndBijectionsMerger;
import PerformanceImprovedImplementation.Merging.SetOfFunctionalDependencyImitatorBijectionsHolder;
import PerformanceImprovedImplementation.Structures.FinalImprovedSchemaSet;
import PerformanceImprovedImplementation.Structures.ListSet;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;

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
    public AlgorithmState getFirstStateFromGivenDependencies(Collection<FunctionalDependency> dependencies) {
        return new DependenciesOperationalSet(dependencies,true);
    }

    @Override
    public AlgorithmState eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        Set<FunctionalDependency> reducedLeftSideDependencies=new ListSet<>();
        DependenciesOperationalSet baseDependencies=(DependenciesOperationalSet) getStateOfAlgorithm();

        for (FunctionalDependency dependency : baseDependencies) {
            FunctionalDependency minimalLeftSideDependency=baseDependencies.removeExtraneousAttributesFromLeftSideOfDependency(dependency);
            reducedLeftSideDependencies.add(minimalLeftSideDependency);
        }
        return (AlgorithmState) reducedLeftSideDependencies;
    }

    @Override
    public AlgorithmState findMinimalCoverOfFunctionalDependencies() {
        Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes=(ListSet<FunctionalDependency>) getStateOfAlgorithm();
        DependenciesOperationalSet minimalCover=new DependenciesOperationalSet(functionalDependenciesWithReducedLeftAttributes);
        minimalCover.transformItselfIntoMinimalCover();
        return minimalCover;
    }

    @Override
    public AlgorithmState groupDependenciesByLeftSides() {
        Set<FunctionalDependency> minimalCover=(DependenciesOperationalSet) getStateOfAlgorithm();
        DependenciesLeftSidesGrouper groupingPerformer=new DependenciesLeftSidesGrouper(minimalCover);
        return (AlgorithmState) groupingPerformer.group();
    }

    @Override
    public AlgorithmState groupBijectionDependenciesAndMergeTheirGroups() {
        MapImitatorGroupsHolder initialGroups=(MapImitatorGroupsHolder) getStateOfAlgorithm();
        Set<DependenciesGrouping> actualInitialGroups= initialGroups.getGroups();
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
    public AlgorithmState removeTransitiveDependencies() {
        BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies=(BijectionDependenciesAndGroups) getStateOfAlgorithm();
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

    @Override
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies() {
        Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies=(Map<Attributes, GroupOfFunctionalDependencies>) getStateOfAlgorithm();
        Set<DependenciesGrouping> groups=((MapImitatorGroupsHolder)nonTransitiveDependencies).getGroups();
        Function<DependenciesGrouping,RelationalSchema> schemaCreator= group ->
                new RelationalSchema(new AttributesHashSet(
                        group.stream()
                                .flatMap(dependency->
                                        Stream.concat(dependency.getLeftAttributes().stream()
                                                ,dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,group.getAnyDependency().getLeftAttributes());


        Set<RelationalSchema> schemas=
                groups.stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalImprovedSchemaSet::new));

        return (AlgorithmState) schemas;
    }
}
