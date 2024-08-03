package PerformanceImprovedImplementation.Structures;

import BaseTemplateElements.Attribute;

public class IntCodedStringAttribute implements Attribute {
    public static class ProducerOfIntCodedAttributes {
        private int currentCodingState;
        public ProducerOfIntCodedAttributes(){
            currentCodingState=-1;
        }
        public IntCodedStringAttribute getCodedAttribute(String actualAttribute){
            currentCodingState++;
            return new IntCodedStringAttribute(actualAttribute,currentCodingState);
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
        return Integer.hashCode(code);
    }

    @Override
    public String toString() {
        return actualAttribute;
    }
}
