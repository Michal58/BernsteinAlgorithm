package BaseTemplateElements;

import java.util.Set;

public record Attributes(Set<String> setOfAttributes) {
    @Override
    public boolean equals(Object o) {
        return o instanceof Attributes
                && this.setOfAttributes.equals(((Attributes) o).setOfAttributes);
    }

    @Override
    public int hashCode() {
        return setOfAttributes.hashCode();
    }
}
