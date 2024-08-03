package BaseTemplateElements;

import java.util.Set;

public interface Attributes extends Set<Attribute> {
    Attributes createEmptyInstance();
    Attributes copy();
}
