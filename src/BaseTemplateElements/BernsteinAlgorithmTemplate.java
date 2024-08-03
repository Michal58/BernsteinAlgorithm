package BaseTemplateElements;

import java.util.Collection;
import java.util.Set;

public abstract class BernsteinAlgorithmTemplate {
    private AlgorithmState stateOfAlgorithm;
    public BernsteinAlgorithmTemplate(Collection<FunctionalDependency> functionalDependencies){
        this.stateOfAlgorithm = getFirstStateFromGivenDependencies(functionalDependencies);
    }
    public Set<RelationalSchema> generateNormalizedDatabaseSchema(){
        // step 1
        stateOfAlgorithm = eliminateExtraneousAttributesFromLeftSidesOfDependencies();

        // step 2
        stateOfAlgorithm = findMinimalCoverOfFunctionalDependencies();

        // step 3
        stateOfAlgorithm = groupDependenciesByLeftSides();

        // step 4
        stateOfAlgorithm = groupBijectionDependenciesAndMergeTheirGroups();

        // step 5
        stateOfAlgorithm = removeTransitiveDependencies();

        // step 6
        stateOfAlgorithm = createRelationalSchemasFromGroupsOfFunctionalDependencies();

        return extractSchemasFromFinalState();
    }

    public AlgorithmState getStateOfAlgorithm() {
        return stateOfAlgorithm;
    }

    public void setStateOfAlgorithm(AlgorithmState stateOfAlgorithm) {
        this.stateOfAlgorithm = stateOfAlgorithm;
    }

    public abstract AlgorithmState getFirstStateFromGivenDependencies(Collection<FunctionalDependency> dependencies);
    public abstract AlgorithmState eliminateExtraneousAttributesFromLeftSidesOfDependencies();
    public abstract AlgorithmState findMinimalCoverOfFunctionalDependencies();
    public abstract AlgorithmState groupDependenciesByLeftSides();
    public abstract AlgorithmState groupBijectionDependenciesAndMergeTheirGroups();
    public abstract AlgorithmState removeTransitiveDependencies();
    public abstract AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies();
    public Set<RelationalSchema> extractSchemasFromFinalState(){
        return (Set<RelationalSchema>) stateOfAlgorithm;
    }
}
