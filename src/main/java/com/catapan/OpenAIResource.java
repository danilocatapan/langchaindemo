package com.catapan;

import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/answer")
public class OpenAIResource {

    private static final Logger LOGGER = Logger.getLogger(OpenAIResource.class);

    @Inject
    private ChatLanguageModel chatModel;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response chatWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());
        String answer = chatModel.generate(question.question());
        LOGGER.info("Resposta: " + answer);
        return Response.ok(answer).build();
    }
}
