package BaseTemplateElements;

import java.util.Set;

public record RelationalSchema(Attributes allAttributes, Attributes key) {
    public RelationalSchema(Attributes allAttributes, Attributes key){
        this.allAttributes=createAttributesFromSet(allAttributes);
        this.key=createAttributesFromSet(key);
    }

    private Attributes createAttributesFromSet(Set<String> actualAttributes){
        return new Attributes(actualAttributes);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RelationalSchema
                && allAttributes.equals(((RelationalSchema) obj).allAttributes)
                && key.equals(((RelationalSchema) obj).key);
    }
}