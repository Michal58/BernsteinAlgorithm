package Service;

import BaseTemplateElements.BernsteinAlgorithmTemplate;
import BaseTemplateElements.FunctionalDependency;
import BaseTemplateElements.RelationalSchema;
import PerformanceImprovedImplementation.BernsteinAlgorithmPerformanceImplementation;
import Service.InputConversion.InputConverter;
import Service.OutputSaving.SchemasSaver;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Server {
    private static final int INDEX_OF_NAME_OF_SOURCE_FILE =0;
    private static final int INDEX_OF_NAME_OF_DESTINATION_FILE=1;
    public void run(String... args) throws IOException {
        InputConverter converter=new InputConverter(args[INDEX_OF_NAME_OF_SOURCE_FILE]);
        List<FunctionalDependency> dependencies=converter.readDependenciesFromFile();
        BernsteinAlgorithmTemplate algorithmInstance=new BernsteinAlgorithmPerformanceImplementation(dependencies);
        Set<RelationalSchema> normalizedSchemas=algorithmInstance.generateNormalizedDatabaseSchema();
        SchemasSaver saver=new SchemasSaver(args[INDEX_OF_NAME_OF_DESTINATION_FILE],normalizedSchemas);
        saver.save();
    }
}
