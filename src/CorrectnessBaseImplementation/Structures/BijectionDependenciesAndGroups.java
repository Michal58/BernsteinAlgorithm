package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BijectionDependenciesAndGroups implements AlgorithmState {
    private final Set<FunctionalDependency> bijectionsDependencies;
    private final Map<Attributes, GroupOfFunctionalDependencies> groupsOfFunctionalDependencies;

    public BijectionDependenciesAndGroups(Set<FunctionalDependency> bijectionsDependencies, Map<Attributes, GroupOfFunctionalDependencies> groupsOfFunctionalDependencies) {
        this.bijectionsDependencies = bijectionsDependencies;
        this.groupsOfFunctionalDependencies = groupsOfFunctionalDependencies;
    }

    public Set<FunctionalDependency> bijectionsDependencies() {
        return bijectionsDependencies;
    }

    public Map<Attributes, GroupOfFunctionalDependencies> groupsOfFunctionalDependencies() {
        return groupsOfFunctionalDependencies;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BijectionDependenciesAndGroups) obj;
        return Objects.equals(this.bijectionsDependencies, that.bijectionsDependencies) &&
                Objects.equals(this.groupsOfFunctionalDependencies, that.groupsOfFunctionalDependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bijectionsDependencies, groupsOfFunctionalDependencies);
    }

    @Override
    public String toString() {
        return "BijectionDependenciesAndGroups[" +
                "bijectionsDependencies=" + bijectionsDependencies + ", " +
                "groupsOfFunctionalDependencies=" + groupsOfFunctionalDependencies + ']';
    }

}
