package com.catapan.openia.resource;

import com.catapan.openia.dto.MyQuestion;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

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
    @Path("/answerModel")
    public Response chatModelWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());

        ChatLanguageModel customModel = new OpenAiChatModel.OpenAiChatModelBuilder()
                .apiKey(apiKey)
                .modelName("gpt-3.5-turbo")
                .temperature(0.1).build();

        LOGGER.info("Resposta: " + customModel);
        return Response.ok(customModel.generate(question.question())).build();
    }

}
