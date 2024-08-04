package PerformanceImprovedImplementation;

import BaseTemplateElements.*;
import CommonElements.ListSet;
import CommonElements.ListSetOfDependencies;
import CorrectnessBaseImplementation.StepPerformers.ExtraneousAttributesEliminator;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.DependenciesLeftSidesGrouper;
import PerformanceImprovedImplementation.Merging.GroupsAndBijectionsMerger;
import PerformanceImprovedImplementation.SchemasCreating.SchemasCreator;
import PerformanceImprovedImplementation.Structures.DependenciesOperationalSet;
import PerformanceImprovedImplementation.Structures.MergedGroupsAndBijectionsAsListSets;
import PerformanceImprovedImplementation.TransitiveDependenciesRemoving.TransitiveDependenciesRemover;

import java.util.Collection;
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
        return groupingPerformer.group();
    }

    @Override
    public AlgorithmState groupBijectionDependenciesAndMergeTheirGroups() {
        Set<DependenciesGrouping> initialGroups=(Set<DependenciesGrouping>) getStateOfAlgorithm();
        GroupsAndBijectionsMerger merger=new GroupsAndBijectionsMerger(initialGroups);
        return merger.mergeGroupsAndBijectionDependencies();
    }
    @Override
    public AlgorithmState removeTransitiveDependencies() {
        MergedGroupsAndBijectionsAsListSets potentiallyPossibleTransitiveDependencies=(MergedGroupsAndBijectionsAsListSets) getStateOfAlgorithm();
        TransitiveDependenciesRemover dependenciesRemover=new TransitiveDependenciesRemover();
        return dependenciesRemover.removeTransitiveDependencies(potentiallyPossibleTransitiveDependencies);
    }

    @Override
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies() {
        ListSet<DependenciesGrouping> nonTransitiveDependencies=(ListSet<DependenciesGrouping>) getStateOfAlgorithm();
        SchemasCreator creatorOfFinalSchemas=new SchemasCreator();
        return creatorOfFinalSchemas.createRelationalSchemasFromGroupsOfFunctionalDependencies(nonTransitiveDependencies);
    }
}
