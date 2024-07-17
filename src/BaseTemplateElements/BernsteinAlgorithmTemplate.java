package BaseTemplateElements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BernsteinAlgorithmTemplate {
    private Set<FunctionalDependency> functionalDependencies;
    public BernsteinAlgorithmTemplate(Collection<FunctionalDependency> functionalDependencies){
        this.functionalDependencies= getSetOfFunctionalDependencies(functionalDependencies);
    }
    public Set<RelationalSchema> generateNormalizedDatabaseSchema(){
        // main purpose of structures e.g. RelationalSchema, Attributes - is to simplification
        // and explanation of algorithm

        // step 1
        Set<FunctionalDependency> dependenciesWithReducedLeftAttributes=
                eliminateExtraneousAttributesFromLeftSidesOfDependencies();
        // step 2
        Set<FunctionalDependency> minimalCover=
                findMinimalCoverOfFunctionalDependencies(dependenciesWithReducedLeftAttributes);
        // step 3
        Map<Attributes, GroupOfFunctionalDependencies> groupedDependencies=
                groupDependenciesByLeftSides(minimalCover);
        // step 4
        BijectionDependenciesAndGroups mergedGroups=
                groupBijectionDependenciesAndMergeTheirGroups(groupedDependencies);

        // step 5
        Map<Attributes,GroupOfFunctionalDependencies> nonTransitiveDependencies=
                removeTransitiveDependencies(mergedGroups);

        // step 6
        Set<RelationalSchema> finalDatabaseSchema=
                createRelationalSchemasFromGroupsOfFunctionalDependencies(nonTransitiveDependencies);

        return finalDatabaseSchema;
    }
    public Set<FunctionalDependency> getFunctionalDependencies() {
        return functionalDependencies;
    }
    public abstract Set<FunctionalDependency> getSetOfFunctionalDependencies(Collection<FunctionalDependency> dependencies);
    public abstract Set<FunctionalDependency> eliminateExtraneousAttributesFromLeftSidesOfDependencies();
    public abstract Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes);
    public abstract Map<Attributes, GroupOfFunctionalDependencies> groupDependenciesByLeftSides(Set<FunctionalDependency> minimalCover);
    public abstract BijectionDependenciesAndGroups groupBijectionDependenciesAndMergeTheirGroups(Map<Attributes, GroupOfFunctionalDependencies> initialGroups);
    public abstract Map<Attributes,GroupOfFunctionalDependencies> removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies);
    public abstract Set<RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes,GroupOfFunctionalDependencies> nonTransitiveDependencies);
}
