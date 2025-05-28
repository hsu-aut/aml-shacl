package shacl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gpt.GptHandler;
import util.PromptLoader;

public class ShaclGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	GptHandler gptHandler;
	
	public ShaclGenerator() {
		this.gptHandler = new GptHandler();
	}
	
    /**
     * Reads a file containing multiple constraints and processes each separately.
     *
     * @param filePath Path to the input file
     * @return List of generated SHACL shapes as strings
     * @throws Exception if file reading or OpenAI call fails
     */
    public List<String> processConstraintFile(Path filePath) throws Exception {
        String content = Files.readString(filePath);

        // Split by double newlines (handles both Unix \n and Windows \r\n)
        String[] constraints = content.split("(?m)(\\r?\\n){2,}");

        List<String> shapes = new ArrayList<>();

        for (String constraint : constraints) {
            constraint = constraint.trim();
            if (!constraint.isEmpty()) {
                logger.info("Processing constraint:\n" + constraint + "\n---");
                String promptText = PromptLoader.loadShaclGenerationPrompt(constraint);
                String shape = this.generateShaclShape(promptText);
                shapes.add(shape);
            }
        }

        return shapes;
    }
    
    /**
     * Generates a SHACL shape from a given input using OpenAI's API.
     *
     * @param inputText The text input describing the RDF data or concept.
     * @return The SHACL shape as a String in Turtle syntax.
     * @throws Exception If the HTTP request fails.
     */
    public String generateShaclShape(String inputText) throws Exception {
    	String prompt = "Generate a SHACL shape for the following RDF description or dataset. " +
                "Please provide only the SHACL shape in Turtle syntax without any explanation.\n\n" +
                inputText;

        return gptHandler.getCompletion(prompt);
    }

}
