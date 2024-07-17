package BaseTemplateElements;

import java.util.Collection;
import java.util.HashSet;

public class GroupOfFunctionalDependencies extends HashSet<FunctionalDependency> {
    public GroupOfFunctionalDependencies(Collection<FunctionalDependency> functionalDependencies){
        super(functionalDependencies);
    }
}
