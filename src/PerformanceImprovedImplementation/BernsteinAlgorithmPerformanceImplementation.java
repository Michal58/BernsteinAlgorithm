package PerformanceImprovedImplementation;

import BaseTemplateElements.*;
import CommonElements.ListSetOfDependencies;
import CorrectnessBaseImplementation.StepPerformers.ExtraneousAttributesEliminator;
import CorrectnessBaseImplementation.Structures.BijectionDependenciesAndGroups;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.DependenciesLeftSidesGrouper;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Merging.GroupsAndBijectionsMerger;
import PerformanceImprovedImplementation.SchemasCreating.SchemasCreator;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;
import PerformanceImprovedImplementation.TransitiveDependenciesRemoving.TransitiveDependenciesRemover;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
        DependenciesOperationalSet baseDependencies=(DependenciesOperationalSet) getStateOfAlgorithm();
        ExtraneousAttributesEliminator attributesEliminator=new ExtraneousAttributesEliminator(ListSetOfDependencies::new);
        return attributesEliminator.eliminateExtraneousAttributesFromLeftSidesOfDependencies(baseDependencies);
    }

    @Override
    public AlgorithmState findMinimalCoverOfFunctionalDependencies() {
        Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes=(ListSetOfDependencies) getStateOfAlgorithm();
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
    @Override
    public AlgorithmState removeTransitiveDependencies() {
        BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies=(BijectionDependenciesAndGroups) getStateOfAlgorithm();
        TransitiveDependenciesRemover dependenciesRemover=new TransitiveDependenciesRemover();
        return dependenciesRemover.removeTransitiveDependencies(potentiallyPossibleTransitiveDependencies);
    }

    @Override
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies() {
        Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies=(Map<Attributes, GroupOfFunctionalDependencies>) getStateOfAlgorithm();
        SchemasCreator creatorOfFinalSchemas=new SchemasCreator();
        return creatorOfFinalSchemas.createRelationalSchemasFromGroupsOfFunctionalDependencies(nonTransitiveDependencies);
    }
}
