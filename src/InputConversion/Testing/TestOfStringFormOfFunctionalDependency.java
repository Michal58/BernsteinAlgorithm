package InputConversion.Testing;

import BaseTemplateElements.FunctionalDependency;
import InputConversion.StringFormOfFunctionalDependency;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOfStringFormOfFunctionalDependency {
    public record ArrayAttributesPair(String[] leftAttributes, String[] rightAttributes) {}
    public boolean performEvaluationOfWrittenDependencies(boolean shouldNegate,String[] writtenDependencies){
        return Arrays.stream(writtenDependencies).allMatch(stringDependency -> {
            StringFormOfFunctionalDependency dependencyInstance = new StringFormOfFunctionalDependency(stringDependency);
            boolean isFormValid=dependencyInstance.isFormValid();
            return shouldNegate && !isFormValid||(!shouldNegate) && isFormValid;
        });
    }

    public List<StringFormOfFunctionalDependency.PairOfExtractedSets> transformArrayAttributesIntoSets(ArrayAttributesPair[] pairs){
        return Arrays.stream(pairs).
                map(pair-> new StringFormOfFunctionalDependency.
                PairOfExtractedSets(new HashSet<>(Arrays.asList(pair.leftAttributes)),
                new HashSet<>(Arrays.asList(pair.rightAttributes)))).
                collect(Collectors.toList());
    }
    @Test
    public void testCorrectnessOfStringFormOfFunctionalDependency(){
        String[] exampleCorrectlyWrittenDependencies=new String[]{
            "{a,b,c,d,e}->{a}",
            "{a}->{b}",
            "{ab,12a}->{rs1,s2}",
            "{ at1,  at }  -> { rs1, s2 }",
            "{a1,b2} ->{a2, 22,  b3}"
        };
        String[] exampleIncorrectlyWrittenDependencies=new String[]{
            "a->b",
            "{%,ab } -> {c}",
            "{a|b|c}->{ d }",
            "{ a b, c}->{d}"
        };


        assertTrue(performEvaluationOfWrittenDependencies(false,exampleCorrectlyWrittenDependencies));
        assertTrue(performEvaluationOfWrittenDependencies(true,exampleIncorrectlyWrittenDependencies));


        ArrayAttributesPair[] validAttributes=new ArrayAttributesPair[] {
            new ArrayAttributesPair(new String[] {"a","b","c","d","e"},new String[] {"a"}),
            new ArrayAttributesPair(new String[] {"a"},new String[] {"b"}),
            new ArrayAttributesPair(new String[] {"ab","12a"},new String[] {"rs1","s2"}),
            new ArrayAttributesPair(new String[] {"at1","at"},new String[] {"rs1","s2"}),
            new ArrayAttributesPair(new String[] {"a1","b2"},new String[] {"a2","22","b3"})
        };

        List<StringFormOfFunctionalDependency.PairOfExtractedSets> extractedSets=
                Arrays.stream(exampleCorrectlyWrittenDependencies).
                map(writtenDependency->{
                    StringFormOfFunctionalDependency dependencyString=new StringFormOfFunctionalDependency(writtenDependency);
                    return dependencyString.extractSets();
                }).toList();

        List<StringFormOfFunctionalDependency.PairOfExtractedSets> transformedValidAttributes=transformArrayAttributesIntoSets(validAttributes);

        assertEquals(extractedSets,transformedValidAttributes);

        List<FunctionalDependency> validFunctionalDependencies=
                transformedValidAttributes.stream().
                        map(pairOfExtractedSets ->
                                new FunctionalDependency(pairOfExtractedSets.leftSide(),pairOfExtractedSets.rightSide())).
                        toList();

        List<FunctionalDependency> convertedStringsIntoDependencies=
                Arrays.stream(exampleCorrectlyWrittenDependencies).
                        map(writtenDependency->{
                            StringFormOfFunctionalDependency dependencyString=new StringFormOfFunctionalDependency(writtenDependency);
                            return dependencyString.convertToFunctionalDependency();
                        }).toList();

        assertEquals(convertedStringsIntoDependencies,validFunctionalDependencies);
    }
}
