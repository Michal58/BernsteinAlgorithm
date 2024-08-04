package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.RelationalSchema;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.FinalSchemasSet;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreatorOfRelationalSchemas {
    public FinalSchemasSet createRelationalSchemasFromGroupsOfFunctionalDependencies(Map<Attributes, GroupOfFunctionalDependencies> nonTransitiveDependencies) {
        Function<Attributes, RelationalSchema> schemaCreator= attributes ->
                new RelationalSchema(new AttributesHashSet(
                        nonTransitiveDependencies
                                .get(attributes)
                                .stream()
                                .flatMap(dependency-> Stream.concat(dependency.getLeftAttributes().stream(),dependency.getRightAttributes().stream()))
                                .collect(Collectors.toSet()))
                        ,attributes);
        FinalSchemasSet schemas=
                nonTransitiveDependencies
                        .keySet()
                        .stream()
                        .map(schemaCreator)
                        .collect(Collectors.toCollection(FinalSchemasSet::new));

        return schemas;
    }
}
