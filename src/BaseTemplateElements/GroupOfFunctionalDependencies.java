package BaseTemplateElements;

import java.util.Set;

public record GroupOfFunctionalDependencies(Set<FunctionalDependency> functionalDependencies) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof GroupOfFunctionalDependencies && functionalDependencies.equals(((GroupOfFunctionalDependencies) obj).functionalDependencies);
    }
}
