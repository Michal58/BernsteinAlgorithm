package PerformanceImprovedImplementation.PerformanceAdaptedStructures;

import BaseTemplateElements.Attributes;

import java.util.Collection;
import java.util.Iterator;

class NewClosureAttributes extends Attributes {
    public NewClosureAttributes(Collection<String> setOfAttributes){
        super(setOfAttributes);
    }

    public String getAndThenRemoveAnyAttribute(){
        if (this.isEmpty())
            return null;
        Iterator<String> attributeGetter=this.iterator();
        String anyAttribute=attributeGetter.next();
        attributeGetter.remove();
        return anyAttribute;
    }

}