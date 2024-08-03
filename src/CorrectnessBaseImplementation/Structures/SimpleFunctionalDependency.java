package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.Collection;
import java.util.stream.Collectors;

public class SimpleFunctionalDependency extends FunctionalDependency {
    public SimpleFunctionalDependency(FunctionalDependency dependencyToCopy) {
        super(dependencyToCopy);
    }

    public SimpleFunctionalDependency(Attributes leftAttributes,Attributes rightAttributes){
        super(leftAttributes,rightAttributes);
    }

    public SimpleFunctionalDependency(Collection<String> leftAttributes,Collection<String> rightAttributes){
        super(leftAttributes,rightAttributes);
    }

    @Override
    public Attributes produceSetOfAttributesFromStrings(Collection<String> attributes) {
        return attributes.stream()
                .map(StringBasedAttribute::new)
                .collect(Collectors.toCollection(AttributesHashSet::new));
    }

    @Override
    public FunctionalDependency getCopy() {
        return new SimpleFunctionalDependency(getLeftAttributes(),getRightAttributes());
    }
}
