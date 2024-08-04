package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;

import java.util.Collection;
import java.util.HashSet;

public class AttributesHashSet extends HashSet<Attribute> implements Attributes {
    public AttributesHashSet(){
        super();
    }
    public AttributesHashSet(Collection<Attribute> attributes){
        super(attributes);
    }
    @Override
    public Attributes createEmptyInstance() {
        return new AttributesHashSet();
    }

    @Override
    public Attributes shallowCopy() {
        return new AttributesHashSet(this);
    }
}
