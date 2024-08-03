package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import CorrectnessBaseImplementation.Structures.GroupOfFunctionalDependencies;

import java.util.HashMap;
import java.util.Set;

public class MapImitatorGroupsHolder extends HashMap<Attributes, GroupOfFunctionalDependencies> implements AlgorithmState {
    private Set<DependenciesGrouping> groups;
    public MapImitatorGroupsHolder(Set<DependenciesGrouping> groups){
        this.groups=groups;
    }
    public Set<DependenciesGrouping> getGroups() {
        return groups;
    }
}
