package BaseTemplateElements;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BernsteinAlgorithmTemplate {
    public record RelationalSchema(Attributes allAttributes, Attributes key) {
        public RelationalSchema(Attributes allAttributes, Attributes key){
            this.allAttributes=createAttributesFromSet(allAttributes.setOfAttributes());
            this.key=createAttributesFromSet(key.setOfAttributes());
        }

        private Attributes createAttributesFromSet(Set<String> actualAttributes){
            return new Attributes(actualAttributes);
        }
    }
    public record BijectionDependenciesAndGroups(Set<FunctionalDependency> bijectionsDependencies, Set<GroupOfFunctionalDependencies> groupsOfFunctionalDependencies) {}
    public record GroupOfFunctionalDependencies(Set<FunctionalDependency> functionalDependencies) {}
    // main purpose of record Attributes is simplification and explanation of structures in code

    private Set<FunctionalDependency> functionalDependencies;
    public BernsteinAlgorithmTemplate(Set<FunctionalDependency> functionalDependencies){
        this.functionalDependencies= new HashSet<>(functionalDependencies);
    }
    public Set<RelationalSchema> generateNormalizedDatabaseSchema(){
        // step 1
        Set<FunctionalDependency> dependenciesWithReducedLeftAttributes=
                eliminateExtraneousAttributesFromLeftSidesOfDependencies();
        // step 2
        Set<FunctionalDependency> minimalCover=
                findMinimalCoverOfFunctionalDependencies(dependenciesWithReducedLeftAttributes);
        // step 3
        Map<Attributes, GroupOfFunctionalDependencies> groupedDependencies=
                groupMinimalCoverByLeftSides(minimalCover);
        // step 4
        BijectionDependenciesAndGroups mergedDependencies=
                mergeDependenciesWithMatchingRightSidesOfElementsFromOneGroupWithLeftSidesOfAnother(groupedDependencies);

        // step 5
        Set<GroupOfFunctionalDependencies> nonTransitiveDependencies=
                removeTransitiveDependencies(mergedDependencies);

        // step 6
        Set<RelationalSchema> finalDatabaseSchema=
                createRelationalSchemasFromGroupsOfFunctionalDependencies(nonTransitiveDependencies);

        return finalDatabaseSchema;
    }

    public Set<FunctionalDependency> getFunctionalDependencies() {
        return functionalDependencies;
    }

    public abstract Set<FunctionalDependency> eliminateExtraneousAttributesFromLeftSidesOfDependencies();
    public abstract Set<FunctionalDependency> findMinimalCoverOfFunctionalDependencies(Set<FunctionalDependency> functionalDependenciesWithReducedLeftAttributes);
    public abstract Map<Attributes, GroupOfFunctionalDependencies> groupMinimalCoverByLeftSides(Set<FunctionalDependency> minimalCover);
    public abstract BijectionDependenciesAndGroups mergeDependenciesWithMatchingRightSidesOfElementsFromOneGroupWithLeftSidesOfAnother(Map<Attributes, GroupOfFunctionalDependencies> entryGroups);
    public abstract Set<GroupOfFunctionalDependencies> removeTransitiveDependencies(BijectionDependenciesAndGroups potentiallyPossibleTransitiveDependencies);
    public abstract Set<RelationalSchema> createRelationalSchemasFromGroupsOfFunctionalDependencies(Set<GroupOfFunctionalDependencies> nonTransitiveDependencies);
}
