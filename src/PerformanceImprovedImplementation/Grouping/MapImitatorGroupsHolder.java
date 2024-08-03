package PerformanceImprovedImplementation.Grouping;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.GroupOfFunctionalDependencies;

import java.util.HashMap;
import java.util.Set;

public class MapImitatorGroupsHolder extends HashMap<Attributes, GroupOfFunctionalDependencies> {
    private Set<DependenciesGrouping> groups;
    public MapImitatorGroupsHolder(Set<DependenciesGrouping> groups){
        this.groups=groups;
    }
    public Set<DependenciesGrouping> getGroups() {
        return groups;
    }
}
