package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CommonElements.DependenciesOperationalSet;
import CommonElements.DependenciesSetAsState;

import java.util.Set;
import java.util.function.Supplier;

public class ExtraneousAttributesEliminator {
    private DependenciesOperationalSet operationalBaseDependenciesBeforeEliminating;
    private Supplier<DependenciesSetAsState> emptySetForReducedLeftSideDependenciesProvider;

    public ExtraneousAttributesEliminator(Supplier<DependenciesSetAsState> emptySetForReducedLeftSideDependenciesProvider){
        this.emptySetForReducedLeftSideDependenciesProvider = emptySetForReducedLeftSideDependenciesProvider;
    }

    public void eliminateLeftAttribute(FunctionalDependency givenDependency,Attribute toRemove){
        givenDependency.getLeftAttributes().remove(toRemove);
    }

    public void restoreLeftAttribute(FunctionalDependency givenDependency,Attribute toRestore){
        givenDependency.getLeftAttributes().add(toRestore);
    }

    public FunctionalDependency createDependencyWithEliminatedExtraneousAttributesOnLeftSide(FunctionalDependency dependency){
        FunctionalDependency copyOfDependency=dependency.getCopy();
        Attributes primaryAttributes=copyOfDependency.getLeftAttributes().shallowCopy();

        for (Attribute leftAttribute : primaryAttributes) {
            eliminateLeftAttribute(copyOfDependency,leftAttribute);
            boolean wasLeftAttributeExtraneous = operationalBaseDependenciesBeforeEliminating.checkIfThereIsTransitiveDependency(copyOfDependency);
            if (!wasLeftAttributeExtraneous)
                restoreLeftAttribute(copyOfDependency,leftAttribute);
        }

        return copyOfDependency;
    }
    public AlgorithmState eliminateExtraneousAttributesFromLeftSidesOfDependencies(DependenciesOperationalSet baseDependencies){
        this.operationalBaseDependenciesBeforeEliminating = baseDependencies;
        Set<FunctionalDependency> reducedLeftSideDependencies = emptySetForReducedLeftSideDependenciesProvider.get();

        for (FunctionalDependency dependency : baseDependencies) {
            FunctionalDependency reducedDependency=createDependencyWithEliminatedExtraneousAttributesOnLeftSide(dependency);
            reducedLeftSideDependencies.add(reducedDependency);
        }

        return (AlgorithmState) reducedLeftSideDependencies;
    }
}
