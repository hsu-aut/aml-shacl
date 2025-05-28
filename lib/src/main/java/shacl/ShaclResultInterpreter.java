package shacl;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import gpt.GptHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShaclResultInterpreter {

    private static final String SH_NS = "http://www.w3.org/ns/shacl#";
    private GptHandler gptHandler;
    
    public ShaclResultInterpreter() {
		this.gptHandler = new GptHandler();
	}

    /**
     * Interprets SHACL validation results from a Jena model and saves natural-language explanations.
     *
     * @param resultModel the Jena model containing SHACL validation results
     * @throws Exception in case of API or IO errors
     */
    public void interpretResults(Model resultModel) throws Exception {
        List<String> explanations = new ArrayList<>();

        Property resultProp = ResourceFactory.createProperty(SH_NS + "result");
        Resource validationResultClass = ResourceFactory.createResource(SH_NS + "ValidationResult");

        // Find all SHACL validation result resources
        ResIterator resultResources = resultModel.listResourcesWithProperty(RDF.type, validationResultClass);

        int count = 0;
        while (resultResources.hasNext()) {
            Resource result = resultResources.next();
            StringBuilder sb = new StringBuilder();

            // Build a textual summary from key properties
            for (StmtIterator it = result.listProperties(); it.hasNext(); ) {
                Statement stmt = it.nextStatement();
                sb.append(stmt.getPredicate().getLocalName())
                  .append(": ")
                  .append(stmt.getObject().toString())
                  .append("\n");
            }

            String resultSummary = sb.toString();

            // Send to GPT for interpretation
            String prompt = "Explain the following SHACL validation result in simple, user-friendly language:\n\n" + resultSummary;
            String explanation = this.gptHandler.getCompletion(prompt);

            explanations.add("Result #" + (++count) + ":\n" + explanation + "\n");
        }

        saveExplanationsToFile(explanations, "validation-interpretation.txt");
    }

    /**
     * Writes a list of explanations to a text file.
     */
    private static void saveExplanationsToFile(List<String> explanations, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String exp : explanations) {
                writer.write(exp);
                writer.write("\n--------------------------\n\n");
            }
        }
    }
}
