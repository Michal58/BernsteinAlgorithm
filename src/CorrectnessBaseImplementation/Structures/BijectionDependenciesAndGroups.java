package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.Map;
import java.util.Set;

public record BijectionDependenciesAndGroups(Set<FunctionalDependency> bijectionsDependencies, Map<Attributes, GroupOfFunctionalDependencies> groupsOfFunctionalDependencies) implements AlgorithmState {

    @Override
    public String toString() {
        return "BijectionDependenciesAndGroups[" +
                "bijectionsDependencies=" + bijectionsDependencies + ", " +
                "groupsOfFunctionalDependencies=" + groupsOfFunctionalDependencies + ']';
    }

}
