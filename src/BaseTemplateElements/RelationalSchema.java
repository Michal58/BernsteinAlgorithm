package BaseTemplateElements;

public record RelationalSchema(Attributes allAttributes, Attributes key) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RelationalSchema
                && allAttributes.equals(((RelationalSchema) obj).allAttributes)
                && key.equals(((RelationalSchema) obj).key);
    }

    @Override
    public String toString() {
        return String.format("%s<-%s",allAttributes,key);
    }
}