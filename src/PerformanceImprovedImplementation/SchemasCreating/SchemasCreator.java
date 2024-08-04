package PerformanceImprovedImplementation.SchemasCreating;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.RelationalSchema;
import CommonElements.ListSet;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Structures.FinalImprovedSchemaSet;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemasCreator {
    public FinalImprovedSchemaSet createRelationalSchemasFromGroupsOfFunctionalDependencies(ListSet<DependenciesGrouping> nonTransitiveDependenciesGroups) {
        Function<DependenciesGrouping, RelationalSchema> schemaCreator= group ->
                new RelationalSchema(new AttributesHashSet(
                        group.stream()
                                .flatMap(dependency->
                                        Stream.concat(dependency.getLeftAttributes().stream()
                                                ,dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,group.getAnyDependency().getLeftAttributes());


        FinalImprovedSchemaSet schemas=
                nonTransitiveDependenciesGroups.stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalImprovedSchemaSet::new));

        return schemas;
    }
}
