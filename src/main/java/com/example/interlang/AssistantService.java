package com.example.interlang;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

interface Assistant {
    @SystemMessage("You are a FHIR assistant AI. You can access various tools to access FHIR resources to chat about a patient.")
    String chat(String userMessage);
}

@Service
public class AssistantService implements Assistant {

    private final Assistant assistant;
    private final FhirTools fhirTools;

    public AssistantService(@Value("${fhir.server.url}") String fhirServerUrl,
            @Value("${fhir.api.key}") String fhirApiKey,
            @Value("${openai.api.key}") String openAiApiKey) {
        this.fhirTools = new FhirTools(fhirServerUrl, fhirApiKey);

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName("gpt-4")
                .build();

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .tools(this.fhirTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Override
    public String chat(String userMessage) {
        return this.assistant.chat(userMessage);
    }
}