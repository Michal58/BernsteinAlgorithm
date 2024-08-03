package Service.InputConversion;

import BaseTemplateElements.FunctionalDependency;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class InputConverter {
    private String nameOfFileToConvert;
    public InputConverter(String nameOfFileToConvert){
        this.nameOfFileToConvert=nameOfFileToConvert;
    }
    private List<FunctionalDependency> readDependenciesFromFileWithReader(BufferedReader fileReader){

        return fileReader.lines()
                .filter(line-> !line.isEmpty())
                .map(StringFormOfFunctionalDependency::new)
                .map(StringFormOfFunctionalDependency::convertToFunctionalDependency)
                .collect(Collectors.toList());
    }
    public List<FunctionalDependency> readDependenciesFromFile() throws IOException {
        File fileToRead=new File(nameOfFileToConvert);
        try (BufferedReader fileReader=new BufferedReader(new FileReader(fileToRead))) {
            return readDependenciesFromFileWithReader(fileReader);
        }

    }
}
