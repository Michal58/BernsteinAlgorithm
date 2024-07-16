package BaseTemplateElements;

import java.util.Map;
import java.util.Set;

public record BijectionDependenciesAndGroups(Set<FunctionalDependency> bijectionsDependencies, Map<Attributes,GroupOfFunctionalDependencies> groupsOfFunctionalDependencies) {
}
