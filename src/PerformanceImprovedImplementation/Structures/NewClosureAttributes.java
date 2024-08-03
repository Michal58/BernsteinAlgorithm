package PerformanceImprovedImplementation.Structures;

import BaseTemplateElements.Attribute;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;

import java.util.Collection;
import java.util.Iterator;

class NewClosureAttributes extends AttributesHashSet {
    public NewClosureAttributes(Collection<Attribute> setOfAttributes){
        super(setOfAttributes);
    }

    public Attribute getAndThenRemoveAnyAttribute(){
        if (this.isEmpty())
            return null;
        Iterator<Attribute> attributeGetter=this.iterator();
        Attribute anyAttribute=attributeGetter.next();
        attributeGetter.remove();
        return anyAttribute;
    }

}