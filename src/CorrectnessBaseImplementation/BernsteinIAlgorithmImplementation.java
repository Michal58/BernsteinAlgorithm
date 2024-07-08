package CorrectnessBase;

import java.util.Map;
import java.util.Set;

public class BernsteinIAlgorithmImplementation extends BernsteinAlgorithmTemplate {

    @Override
    public Set<FunctionalDependency> eliminateUnnecessaryAttributesFromLeftSidesOfDependencies() {
        
        return null;
    }

    @Override
    public Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> reducedAttributes) {
        return null;
    }

    @Override
    public Map<BernsteinAlgorithmTemplate.Attributes, BernsteinAlgorithmTemplate.GroupOfFunctionalDependencies> groupMinimalCoverByLeftSides(Set<FunctionalDependency> minimalCover) {
        return null;
    }

    @Override
    public Set<BernsteinAlgorithmTemplate.GroupOfFunctionalDependencies> mergeDependenciesWithMatchingRightSidesOfElementsFromOneGroupWithLeftSidesOfAnother(Map<BernsteinAlgorithmTemplate.Attributes, BernsteinAlgorithmTemplate.GroupOfFunctionalDependencies> entryGroups) {
        return null;
    }

    @Override
    public Set<BernsteinAlgorithmTemplate.RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Set<BernsteinAlgorithmTemplate.GroupOfFunctionalDependencies> mergedDependencies) {
        return null;
    }
}
