package Service.InputConversion;

import BaseTemplateElements.FunctionalDependency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InputConverter {
    private String nameOfFileToConvert;
    public InputConverter(String nameOfFileToConvert){
        this.nameOfFileToConvert=nameOfFileToConvert;
    }
    public List<FunctionalDependency> readDependenciesFromFile() throws IOException {
        File fileToRead=new File(nameOfFileToConvert);
        BufferedReader fileReader=new BufferedReader(new FileReader(fileToRead));
        return fileReader.lines()
                .filter(line-> !line.isEmpty())
                .map(StringFormOfFunctionalDependency::new)
                .map(StringFormOfFunctionalDependency::convertToFunctionalDependency)
                .collect(Collectors.toList());

    }
}
