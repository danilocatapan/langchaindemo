package com.catapan.openia.resource;

import com.catapan.openia.dto.MyQuestion;
import com.catapan.openia.dto.MyStructuredTemplate;
import com.catapan.openia.dto.MyStructuredTemplate.PromptDeReceita;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Arrays;

@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public class OpenAIResource {

    private static final Logger LOGGER = Logger.getLogger(OpenAIResource.class);

    @ConfigProperty(name = "quarkus.langchain4j.openai.api-key")
    String apiKey;

    @Inject
    private ChatLanguageModel chatModel;

    @POST
    @Path("/answer")
    public Response chatWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());

        String answer = chatModel.generate(question.question());

        LOGGER.info("Resposta: " + answer);
        return Response.ok(answer).build();
    }

    @POST
    @Path("/answer/model")
    public Response chatModelWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());

        ChatLanguageModel customModel = new OpenAiChatModel.OpenAiChatModelBuilder()
                .apiKey(apiKey)
                .modelName("gpt-3.5-turbo")
                .temperature(0.1).build();

        LOGGER.info("Resposta: " + customModel);
        return Response.ok(customModel.generate(question.question())).build();
    }

    @GET
    @Path("/receita")
    public String crieReceita() {
        @java.lang.SuppressWarnings("java:S1481")
        MyStructuredTemplate template = new MyStructuredTemplate();

        PromptDeReceita promptDeReceita = new PromptDeReceita();
        promptDeReceita.prato = "Assado";
        promptDeReceita.ingredientes = Arrays.asList("carne", "tomate", "cebola", "pimentao");

        Prompt prompt = StructuredPromptProcessor.toPrompt(promptDeReceita);

        return chatModel.generate(prompt.text());
    }
}
