import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BernsteinAlgorithmTemplate {
    public record RelationalSchema(Set<String> attributes) {
        public RelationalSchema(Set<String> attributes) {
            this.attributes = new HashSet<>(attributes);
        }
    }
    public record GroupOfFunctionalDependencies(Set<FunctionalDependency> functionalDependencies) {}
    public record Attributes(Set<String> attributes) {}

    private Set<FunctionalDependency> functionalDependencies;
    public BernsteinAlgorithmTemplate(Set<FunctionalDependency> functionalDependencies){
        this.functionalDependencies= new HashSet<>(functionalDependencies);
    }
    public Set<RelationalSchema> generateNormalizedSchema(){
        // step 1
        Set<FunctionalDependency> reducedAttributes=
                eliminateUnnecessaryAttributesFromLeftSidesOfDependencies();
        // step 2
        Set<FunctionalDependency> minimalCover=
                findMinimalCoverOfFunctionalDependencies(reducedAttributes);
        // step 3
        Map<Attributes, GroupOfFunctionalDependencies> groupedDependencies=
                groupMinimalCoverByLeftSides(minimalCover);
        // step 4
        Set<GroupOfFunctionalDependencies> mergedDependencies=
                mergeDependenciesWithMatchingRightSidesOfElementsFromOneGroupWithLeftSidesOfAnother(groupedDependencies);
        // step 5
        Set<RelationalSchema> finalSchema=
                createRelationalSchemasFromGroupsOfFunctionalDependencies(mergedDependencies);

        return finalSchema;
    }

    public Set<FunctionalDependency> getFunctionalDependencies() {
        return functionalDependencies;
    }

    public abstract Set<FunctionalDependency> eliminateUnnecessaryAttributesFromLeftSidesOfDependencies();
    public abstract Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> reducedAttributes);
    public abstract Map<Attributes, GroupOfFunctionalDependencies> groupMinimalCoverByLeftSides(Set<FunctionalDependency> minimalCover);
    public abstract Set<GroupOfFunctionalDependencies> mergeDependenciesWithMatchingRightSidesOfElementsFromOneGroupWithLeftSidesOfAnother(Map<Attributes, GroupOfFunctionalDependencies> entryGroups);
    public abstract Set<RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Set<GroupOfFunctionalDependencies> mergedDependencies);
}
