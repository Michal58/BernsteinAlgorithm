package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CommonElements.DependenciesOperationalSet;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.SimpleFunctionalDependency;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MinimalCoverFinder {
    private Function<Set<FunctionalDependency>,DependenciesOperationalSet> operationalSetOfGivenDependenciesProvider;
    private DependenciesOperationalSet minimalCover;
    public MinimalCoverFinder(Function<Set<FunctionalDependency>,DependenciesOperationalSet> operationalSetOfGivenDependenciesProvider){
        this.operationalSetOfGivenDependenciesProvider=operationalSetOfGivenDependenciesProvider;
    }
    public Set<FunctionalDependency> splitRightSideOfFunctionalDependency(FunctionalDependency dependencyToSplit){
        Set<FunctionalDependency> splitDependencies=new HashSet<>();
        for (Attribute rightAttribute : dependencyToSplit.getRightAttributes()) {
            Attributes rightAttributeAsSet=new AttributesHashSet(Collections.singletonList(rightAttribute));
            splitDependencies.add(new SimpleFunctionalDependency(dependencyToSplit.getLeftAttributes(),rightAttributeAsSet));
        }
        return splitDependencies;
    }

    public Set<FunctionalDependency> transformDependenciesWithSplittingRightSides(Set<FunctionalDependency> dependencies){
        Set<FunctionalDependency> dependenciesWithSplitRightSides=new HashSet<>();
        for (FunctionalDependency dependency : dependencies)
            dependenciesWithSplitRightSides.addAll(splitRightSideOfFunctionalDependency(dependency));
        return dependenciesWithSplitRightSides;
    }

    public void removeDependencyIfItIsRedundant(FunctionalDependency doubleSideReducedDependency){
        minimalCover.remove(doubleSideReducedDependency);
        boolean doesDependencyStillAppear= minimalCover.checkIfThereIsTransitiveDependency(doubleSideReducedDependency);
        if (!doesDependencyStillAppear)
            minimalCover.add(doubleSideReducedDependency);
    }
    public AlgorithmState findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes) {
        Set<FunctionalDependency> doubleSideReducedDependencies=transformDependenciesWithSplittingRightSides(functionalDependenciesWithReducedLeftAttributes);
        minimalCover= operationalSetOfGivenDependenciesProvider.apply(doubleSideReducedDependencies);

        for (FunctionalDependency doubleSideReducedDependency : doubleSideReducedDependencies)
            removeDependencyIfItIsRedundant(doubleSideReducedDependency);

        return minimalCover;
    }
}
