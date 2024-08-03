package Service.OutputSaving;

import BaseTemplateElements.RelationalSchema;

import java.io.*;
import java.util.Set;

public class SchemasSaver {
    private final static String outputFormat ="%50s%50s";
    private final static String leftHeaderTitle="key of schema";
    private final static String rightHeaderTitle="attributes of schema";
    private Set<RelationalSchema> schemasToSave;
    private String filePathToSave;
    public SchemasSaver(String filePathToSave, Set<RelationalSchema> schemasToSave){
        this.filePathToSave=filePathToSave;
        this.schemasToSave=schemasToSave;
    }

    public void writeHeader(BufferedWriter saveWriter) throws IOException{
        saveWriter.write(String.format(outputFormat,leftHeaderTitle,rightHeaderTitle));
        saveWriter.newLine();
    }

    public void save() throws IOException {
        File saveFile=new File(filePathToSave);
        saveFile.createNewFile();

        BufferedWriter saveWriter=new BufferedWriter(new FileWriter(saveFile));
        writeHeader(saveWriter);

        for (RelationalSchema schema : schemasToSave) {
            saveWriter.write(String.format(outputFormat,schema.key(),schema.allAttributes()));
            saveWriter.newLine();
        }
    }
}
