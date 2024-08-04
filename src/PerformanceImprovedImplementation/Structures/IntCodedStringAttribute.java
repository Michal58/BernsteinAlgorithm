package PerformanceImprovedImplementation.Structures;

import BaseTemplateElements.Attribute;

import java.util.HashMap;

public class IntCodedStringAttribute implements Attribute {
    public static class ProducerOfIntCodedAttributes {
        private static final int IMPOSSIBLE_ENCODING=-2;
        private HashMap<String,Integer> encodingDictionary;
        private int currentCode;
        public ProducerOfIntCodedAttributes(){
            currentCode =-1;
            encodingDictionary=new HashMap<>();
        }
        public IntCodedStringAttribute getCodedAttribute(String actualAttribute){
            int possibleAttributeCode=encodingDictionary.getOrDefault(actualAttribute,IMPOSSIBLE_ENCODING);
            if (possibleAttributeCode!=IMPOSSIBLE_ENCODING)
                return new IntCodedStringAttribute(actualAttribute, possibleAttributeCode);
            else {
                currentCode++;
                encodingDictionary.put(actualAttribute,currentCode);
                return new IntCodedStringAttribute(actualAttribute, currentCode);
            }
        }
    }
    private final String actualAttribute;
    private final int code;
    private IntCodedStringAttribute(String actualAttribute,int code){
        this.actualAttribute=actualAttribute;
        this.code=code;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntCodedStringAttribute
                && code==((IntCodedStringAttribute) obj).code;
    }

    @Override
    public int hashCode() {
        return actualAttribute.hashCode();
    }

    @Override
    public String toString() {
        return actualAttribute;
    }
}
