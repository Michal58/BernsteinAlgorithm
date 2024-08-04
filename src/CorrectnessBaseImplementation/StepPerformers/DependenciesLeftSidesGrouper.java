package CorrectnessBaseImplementation.StepPerformers;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;
import CorrectnessBaseImplementation.Structures.GroupingAsDictionary;
import CorrectnessBaseImplementation.Structures.HashGroupOfFunctionalDependencies;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependenciesLeftSidesGrouper {
    public AlgorithmState groupDependenciesByLeftSides(Set<FunctionalDependency> minimalCover) {
        Map<Attributes, GroupOfFunctionalDependencies> leftAttributesDictionary=new GroupingAsDictionary();
        for (FunctionalDependency dependency : minimalCover) {
            if (!leftAttributesDictionary.containsKey(dependency.getLeftAttributes())) {
                Attributes newKey=new AttributesHashSet(dependency.getLeftAttributes());
                Set<FunctionalDependency> setOfSingleDependency=new HashSet<>(List.of(dependency));
                leftAttributesDictionary.put(newKey,new HashGroupOfFunctionalDependencies(setOfSingleDependency));
            }
            else {
                GroupOfFunctionalDependencies createdGroup=leftAttributesDictionary.get(dependency.getLeftAttributes());
                createdGroup.add(dependency);
            }
        }

        return (AlgorithmState) leftAttributesDictionary;
    }
}
