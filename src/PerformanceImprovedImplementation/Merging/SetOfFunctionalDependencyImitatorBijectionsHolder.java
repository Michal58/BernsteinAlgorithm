package PerformanceImprovedImplementation.Merging;

import BaseTemplateElements.FunctionalDependency;
import PerformanceImprovedImplementation.Grouping.GroupDependency;

import java.util.HashSet;
import java.util.Set;

public class SetOfFunctionalDependencyImitatorBijectionsHolder extends HashSet<FunctionalDependency> {
    private Set<GroupDependency> heldDependencies;
    public SetOfFunctionalDependencyImitatorBijectionsHolder(Set<GroupDependency> heldDependencies){
        this.heldDependencies=heldDependencies;
    }

    public Set<GroupDependency> getHeldDependencies() {
        return heldDependencies;
    }
}
