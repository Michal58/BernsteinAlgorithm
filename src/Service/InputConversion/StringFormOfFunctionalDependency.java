package Service.InputConversion;

import BaseTemplateElements.FunctionalDependency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringFormOfFunctionalDependency {
    public static final String SET_FORM_OPENING_FORMAT="\\s*\\{\\s*";
    public static final String SET_FORM_CLOSING_FORMAT="\\s*}\\s*";
    public static final String INNER_ATTRIBUTE_DELIMITER_FORMAT="\\s*,\\s*";
    public static final String ENUMERATED_ATTRIBUTES_FORMAT=
            String.format("(\\w+%s)*\\w+",INNER_ATTRIBUTE_DELIMITER_FORMAT);
    public static final String ATTRIBUTE_SET_FORMAT=
            String.format("%s%s%s",
                    SET_FORM_OPENING_FORMAT,
                    ENUMERATED_ATTRIBUTES_FORMAT,
                    SET_FORM_CLOSING_FORMAT);
    public static final String DEPENDENCY_OPERAND="->";
    public static final String FUNCTIONAL_DEPENDENCY_FORMAT=String.format("^%s%s%s$",ATTRIBUTE_SET_FORMAT,DEPENDENCY_OPERAND,ATTRIBUTE_SET_FORMAT);
    public final String stringFormOfDependency;
    public StringFormOfFunctionalDependency(String stringFormOfDependency){
        this.stringFormOfDependency=stringFormOfDependency;
    }
    public record PairOfExtractedSets(Set<String> leftSide,Set<String> rightSide) {}
    public boolean isFormValid(){
        return stringFormOfDependency.matches(StringFormOfFunctionalDependency.FUNCTIONAL_DEPENDENCY_FORMAT);
    }

    public Set<String> convertStringFormOfSetToActualSet(String stringFormOfSet){
        stringFormOfSet=stringFormOfSet.replaceAll(SET_FORM_OPENING_FORMAT,"");
        stringFormOfSet=stringFormOfSet.replaceAll(SET_FORM_CLOSING_FORMAT,"");
        String[] extractedAttributes=stringFormOfSet.split(StringFormOfFunctionalDependency.INNER_ATTRIBUTE_DELIMITER_FORMAT);
        return new HashSet<>(Arrays.asList(extractedAttributes));
    }

    public PairOfExtractedSets extractSets(){
        String[] twoSets=stringFormOfDependency.split(DEPENDENCY_OPERAND);
        Set<String> leftSet=convertStringFormOfSetToActualSet(twoSets[0]);
        Set<String> rightSet=convertStringFormOfSetToActualSet(twoSets[1]);
        return new PairOfExtractedSets(leftSet,rightSet);
    }

    public FunctionalDependency convertToFunctionalDependency(){
        PairOfExtractedSets extractedSets=extractSets();
        return new FunctionalDependency(extractedSets.leftSide,extractedSets.rightSide);
    }
}
