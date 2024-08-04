package CommonElements;

import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;

public interface DependenciesOperationalSet extends DependenciesSetAsState {
    Attributes constructTransitiveClosure(Attributes baseAttributes);
    boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency);
}
