package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PromptLoader {
	
	private final static String SYSTEM_PROMPT_FILE = "prompts/system.prompt";
	private final static String SHACL_GENERATION_PROMPT_FILE = "prompts/shacl-generation.prompt";
	private final static String SHACL_INTERPRETATION_PROMPT_FILE = "prompts/shacl-interpretation.prompt";
	
	
	public static String loadSystemPrompt() throws Exception {
		return loadTemplate(SYSTEM_PROMPT_FILE, new HashMap<>());
	}
	
	public static String loadShaclGenerationPrompt(String constraint) throws Exception {
		Map<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("constraint", constraint);
		return loadTemplate(SHACL_GENERATION_PROMPT_FILE, placeholders);
	}
	
	public static String loadShaclInterpretationPrompt(String result) throws Exception {
		Map<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("result", result);
		return loadTemplate(SHACL_INTERPRETATION_PROMPT_FILE, placeholders);
	}

	
	
    /**
     * Loads a template file from resources and replaces placeholders in the form ${key}.
     *
     * @param templatePath e.g. "prompts/shacl-generation.txt"
     * @param variables    map of placeholder keys and their values
     * @return the final prompt string
     * @throws Exception if loading fails
     */
    private static String loadTemplate(String templatePath, Map<String, String> variables) throws Exception {
        ClassLoader classLoader = PromptLoader.class.getClassLoader();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                classLoader.getResourceAsStream(templatePath), StandardCharsets.UTF_8))) {

            String template = reader.lines().collect(Collectors.joining("\n"));

            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                template = template.replace(placeholder, entry.getValue());
            }

            return template;
        }
    }
}
