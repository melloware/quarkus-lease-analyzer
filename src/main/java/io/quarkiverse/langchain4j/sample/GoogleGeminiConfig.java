package io.quarkiverse.langchain4j.sample;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Configuration class for Google's Gemini AI chat model.
 * IMPORTANT: Must set the GOOGLE_AI_GEMINI_API_KEY environment variable.
 */
@ApplicationScoped
public class GoogleGeminiConfig {

    /**
     * Produces a ChatLanguageModel instance configured for Google's Gemini AI.
     * 
     * @return A configured ChatLanguageModel instance using Gemini AI
     */
    @Produces
    @ApplicationScoped
    ChatLanguageModel model() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GOOGLE_AI_GEMINI_API_KEY"))
                .modelName("gemini-2.0-flash")
                .build();
    }
}
