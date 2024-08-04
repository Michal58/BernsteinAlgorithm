package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.RelationalSchema;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.FinalSchemasSet;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreatorOfRelationalSchemas {
    public AlgorithmState createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies) {
        Function<Attributes, RelationalSchema> schemaCreator= attributes ->
                new RelationalSchema(new AttributesHashSet(
                        nonTransitiveDependencies
                                .get(attributes)
                                .stream()
                                .flatMap(dependency-> Stream.concat(dependency.getLeftAttributes().stream(),dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,attributes);
        Set<RelationalSchema> schemas=
                nonTransitiveDependencies
                        .keySet()
                        .stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalSchemasSet::new));

        return (AlgorithmState) schemas;
    }
}
