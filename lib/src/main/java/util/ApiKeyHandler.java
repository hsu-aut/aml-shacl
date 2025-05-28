package util;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiKeyHandler {
    private static String cachedApiKey = null;

    /**
     * Returns the cached API key if available, otherwise loads it from:
     * 1. Environment variable
     * 2. System property
     * 3. .env file in current directory
     *
     * @return OpenAI API key
     * @throws RuntimeException if no key is found
     */
    public static String getApiKey() {
        if (cachedApiKey != null) {
            return cachedApiKey;
        }

        // Try environment variable
        String key = System.getenv("OPENAI_API_KEY");

        // Try system property
        if (key == null) {
            key = System.getProperty("OPENAI_API_KEY");
        }

        // Try .env file
        if (key == null) {
            try {
                Dotenv dotenv = Dotenv.configure().directory(".").load();
                key = dotenv.get("OPENAI_API_KEY");
            } catch (Exception ignored) {
            }
        }

        if (key == null || key.isBlank()) {
            throw new RuntimeException("OpenAI API key not found. Set OPENAI_API_KEY as environment variable, system property, or in a .env file.");
        }

        cachedApiKey = key;
        return key;
    }
}
