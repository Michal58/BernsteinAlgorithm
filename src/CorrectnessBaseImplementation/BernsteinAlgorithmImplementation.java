package CorrectnessBaseImplementation;

import BaseTemplateElements.*;
import CorrectnessBaseImplementation.StepPerformers.*;
import CorrectnessBaseImplementation.Structures.*;

import java.util.*;

public class BernsteinAlgorithmImplementation extends BernsteinAlgorithmTemplate {

    public BernsteinAlgorithmImplementation(Collection<FunctionalDependency> functionalDependencies) {
        super(functionalDependencies);
    }

    @Override
    public AlgorithmState getFirstStateFromGivenDependencies(Collection<FunctionalDependency> dependencies) {
        return new DependenciesOperationalSet(dependencies);
    }

    @Override
    public AlgorithmState eliminateExtraneousAttributesFromLeftSidesOfDependencies() {
        DependenciesOperationalSet baseDependencies=(DependenciesOperationalSet) getStateOfAlgorithm();
        ExtraneousAttributesEliminator eliminator=new ExtraneousAttributesEliminator(DependenciesOperationalSet::new);
        return eliminator.eliminateExtraneousAttributesFromLeftSidesOfDependencies(baseDependencies);
    }

    @Override
    public AlgorithmState findMinimalCoverOfFunctionalDependencies() {
        Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes=(DependenciesOperationalSet) getStateOfAlgorithm();
        MinimalCoverFinder coverFinder=new MinimalCoverFinder(DependenciesOperationalSet::new);
        return coverFinder.findMinimalCoverOfFunctionalDependencies(functionalDependenciesWithReducedLeftAttributes);
    }

    @Override
    public AlgorithmState groupDependenciesByLeftSides() {
        Set<FunctionalDependency> minimalCover= (DependenciesOperationalSet) getStateOfAlgorithm();
        DependenciesLeftSidesGrouper dependenciesGrouper=new DependenciesLeftSidesGrouper();
        return dependenciesGrouper.groupDependenciesByLeftSides(minimalCover);
    }

    @Override
    public AlgorithmState groupBijectionDependenciesAndMergeTheirGroups() {
        Map<Attributes, GroupOfFunctionalDependencies> initialGroups=(GroupingAsDictionary) getStateOfAlgorithm();
        BijectionsAndGroupsMerger merger=new BijectionsAndGroupsMerger();
        return merger.groupBijectionDependenciesAndMergeTheirGroups(initialGroups);
    }
    @Override
    public AlgorithmState removeTransitiveDependencies() {
        BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies=(BijectionDependenciesAndGroups) getStateOfAlgorithm();
        TransitiveDependenciesRemover secondRedundantDependenciesRemover=new TransitiveDependenciesRemover();
        return secondRedundantDependenciesRemover.removeTransitiveDependencies(potentiallyPossibleTransitiveDependencies);
    }

    @Override
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies() {
        Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies=(GroupingAsDictionary) getStateOfAlgorithm();
        CreatorOfRelationalSchemas schemasCreator=new CreatorOfRelationalSchemas();
        return schemasCreator.createRelationalSchemasFromGroupsOfFunctionalDependencies(nonTransitiveDependencies);
    }
}
