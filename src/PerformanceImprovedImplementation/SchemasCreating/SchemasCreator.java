package PerformanceImprovedImplementation.SchemasCreating;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.RelationalSchema;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;
import PerformanceImprovedImplementation.Grouping.DependenciesGrouping;
import PerformanceImprovedImplementation.Grouping.MapImitatorGroupsHolder;
import PerformanceImprovedImplementation.Structures.FinalImprovedSchemaSet;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemasCreator {
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies) {
        Set<DependenciesGrouping> groups=((MapImitatorGroupsHolder)nonTransitiveDependencies).getGroups();
        Function<DependenciesGrouping, RelationalSchema> schemaCreator= group ->
                new RelationalSchema(new AttributesHashSet(
                        group.stream()
                                .flatMap(dependency->
                                        Stream.concat(dependency.getLeftAttributes().stream()
                                                ,dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,group.getAnyDependency().getLeftAttributes());


        Set<RelationalSchema> schemas=
                groups.stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalImprovedSchemaSet::new));

        return (AlgorithmState) schemas;
    }
}
