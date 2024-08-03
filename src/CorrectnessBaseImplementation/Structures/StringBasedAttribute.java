package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.Attribute;

public class StringBasedAttribute implements Attribute {
    private String stringForm;
    public StringBasedAttribute(String stringForm){
        this.stringForm=stringForm;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringBasedAttribute
                && stringForm.equals(((StringBasedAttribute) obj).stringForm);
    }

    @Override
    public int hashCode() {
        return stringForm.hashCode();
    }

    @Override
    public String toString() {
        return stringForm;
    }
}
