package Service.InputConversion;

import BaseTemplateElements.FunctionalDependency;
import PerformanceImprovedImplementation.Structures.IntCodedStringAttribute;

import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InputConverter {
    private String nameOfFileToConvert;
    public InputConverter(String nameOfFileToConvert){
        this.nameOfFileToConvert=nameOfFileToConvert;
    }
    private List<FunctionalDependency> readDependenciesFromFileWithReader(BufferedReader fileReader){
        IntCodedStringAttribute.ProducerOfIntCodedAttributes improvedAttributesProducer=new IntCodedStringAttribute.ProducerOfIntCodedAttributes();
        Function<StringFormOfFunctionalDependency,FunctionalDependency> functionalDependencyExtractor= stringFormOfFunctionalDependency ->
                stringFormOfFunctionalDependency.convertToAsymptoticallyImprovedFunctionalDependency(improvedAttributesProducer);
        return fileReader.lines()
                .filter(line-> !line.isEmpty())
                .map(StringFormOfFunctionalDependency::new)
                .map(functionalDependencyExtractor)
                .collect(Collectors.toList());
    }
    public List<FunctionalDependency> readDependenciesFromFile() throws IOException {
        File fileToRead=new File(nameOfFileToConvert);
        try (BufferedReader fileReader=new BufferedReader(new FileReader(fileToRead))) {
            return readDependenciesFromFileWithReader(fileReader);
        }

    }
}
