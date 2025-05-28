package gpt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import util.ApiKeyHandler;
import util.PromptLoader;

public class GptHandler {
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4";

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public GptHandler() {
        this.apiKey = ApiKeyHandler.getApiKey();
        this.httpClient = HttpClient.newHttpClient();
    	this.gson = new Gson(); 
    }


	/**
     * Sends a prompt to OpenAI and returns the assistant's reply.
     *
     * @param prompt The prompt to send to the GPT model.
     * @return The model's response as plain text.
     * @throws Exception on request failure.
     */
    public String getCompletion(String prompt) throws Exception {

        // 1. Build JSON payload using Gson
        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        String systemPrompt = PromptLoader.loadSystemPrompt();
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);
        messages.add(userMessage);

        payload.add("messages", messages);

        String jsonPayload = gson.toJson(payload);

        // 2. Build and send HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error: " + response.body());
        }

        // 3. Parse response JSON to extract assistant's message
        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
        JsonArray choices = responseJson.getAsJsonArray("choices");

        if (choices == null || choices.size() == 0) {
            throw new RuntimeException("No response choices received from OpenAI.");
        }

        JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
        String content = message.get("content").getAsString();

        return content.trim();
    }

}

