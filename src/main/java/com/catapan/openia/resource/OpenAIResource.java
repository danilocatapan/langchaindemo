package com.catapan.openia.resource;

import com.catapan.openia.dto.MyQuestion;
import com.catapan.openia.dto.MyStructuredTemplate;
import com.catapan.openia.dto.MyStructuredTemplate.PromptDeReceita;
import com.catapan.openia.errors.ErrorResponse;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import io.quarkus.arc.impl.UncaughtExceptions;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Arrays;

@Path("/openai")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
@Tag(name = "OpenAI Language Models", description = "Operações relacionadas à geração de texto," +
        " imagens e receitas utilizando os modelos de linguagem da OpenAI.")
public class OpenAIResource {

    @ConfigProperty(name = "quarkus.langchain4j.openai.api-key")
    String apiKey;

    @Inject
    private ChatLanguageModel chatModel;

    /**
     * Endpoint que recebe uma pergunta e retorna a resposta correspondente gerada pelo modelo de linguagem.
     * Utiliza a API da OpenAI para gerar respostas a partir das perguntas dos usuários.
     */
    @POST
    @Path("/answer")
    @Operation(summary = "Gerar resposta para uma pergunta",
            description = "Recebe uma pergunta e retorna a resposta gerada pelo modelo de linguagem configurado.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Resposta gerada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "400", description = "Pergunta inválida"),
            @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public Response chatWithOpenAI(@Valid MyQuestion question) {
        // Geração da resposta utilizando o modelo de linguagem configurado.
        String answer = chatModel.generate(question.question());
        // Criação e retorno da resposta HTTP com o texto gerado.
        return Response.ok(answer).build();
    }

    /**
     * Endpoint que utiliza um modelo personalizado de linguagem para responder perguntas.
     * Utiliza a classe OpenAiChatModelBuilder para configurar e criar um modelo de linguagem.
     */
    @POST
    @Path("/answer/model")
    @Operation(summary = "Gerar resposta usando um modelo de linguagem configurável",
            description = "Configura e utiliza um modelo de linguagem personalizado para gerar respostas baseadas nas perguntas dos usuários.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Resposta gerada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public Response chatModelWithOpenAI(@Valid MyQuestion question) {
        ChatLanguageModel customModel = new OpenAiChatModel.OpenAiChatModelBuilder()
                .apiKey(apiKey)  // Utiliza a chave API para autenticação no serviço da OpenAI.
                // Define o modelo de linguagem. Opções incluem:
                // "gpt-3.5-turbo": Versão otimizada do GPT-3 para respostas rápidas e custo eficiente.
                // "gpt-4": Versão mais avançada para capacidade aprimorada de entendimento e geração de texto.
                // "davinci": Variante poderosa do GPT-3, balanceando custo e performance.
                // "curie": Opção menos custosa que ainda oferece bons resultados.
                .modelName("gpt-3.5-turbo")
                // Configura a temperatura para influenciar a variabilidade das respostas:
                // Valores próximos de 0 geram respostas consistentes e previsíveis.
                // Valores médios (~0.5) oferecem equilíbrio entre previsibilidade e criatividade.
                // Valores altos (~1.0) aumentam a variabilidade e criatividade das respostas.
                .temperature(0.1)
                .build();

        return Response.ok(customModel.generate(question.question())).build();
    }

    /**
     * Endpoint para criar uma receita culinária com base em ingredientes especificados.
     * Utiliza o modelo de linguagem para gerar uma receita formatada a partir de um prompt estruturado.
     */
    @GET
    @Path("/recipe")
    @Operation(summary = "Gerar receita de comida",
            description = "Cria uma receita culinária com base nos ingredientes fornecidos. Este endpoint transforma um conjunto de ingredientes e um tipo de prato em uma receita detalhada.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Receita gerada com sucesso",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public Response foodRecipe() {
        @java.lang.SuppressWarnings("java:S1481")
        MyStructuredTemplate template = new MyStructuredTemplate();

        // Criação de um prompt de receita com ingredientes específicos.
        PromptDeReceita promptDeReceita = new PromptDeReceita();
        promptDeReceita.prato = "Assado";
        promptDeReceita.ingredientes = Arrays.asList("carne", "tomate", "cebola", "pimentao");

        // Conversão do prompt estruturado para texto que será enviado ao modelo de linguagem.
        Prompt prompt = StructuredPromptProcessor.toPrompt(promptDeReceita);

        // Geração da receita com base no texto do prompt e retorno do texto da receita.
        String recipe = chatModel.generate(prompt.text());
        return Response.ok(recipe).build();
    }

    /**
     * Endpoint que gera uma imagem com base em uma descrição textual fornecida.
     * Utiliza a classe OpenAiImageModelBuilder para configurar e criar um modelo de geração de imagem.
     */
    @POST
    @Path("/image")
    @Operation(summary = "Gerar imagem baseada em descrição textual",
            description = "Recebe uma descrição textual e gera uma imagem correspondente, retornando a URL da imagem gerada.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Imagem gerada e URL retornada com sucesso",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "400", description = "Descrição inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Descrição textual para geração da imagem",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MyQuestion.class)))
    public Response generateImage(@Valid MyQuestion question) {
        try {
            // Configuração do modelo de geração de imagem com OpenAiImageModelBuilder
            ImageModel imageModel = new OpenAiImageModel.OpenAiImageModelBuilder()
                    .apiKey(apiKey)  // Chave API para autenticação no serviço da OpenAI.
                    .modelName("dall-e")  // Modelo "dall-e" especializado na criação de imagens baseadas em descrições textuais.
                    .build();

            // Geração da imagem a partir da descrição textual e obtenção do URL da imagem resultante.
            String imageUrl = imageModel.generate(question.question()).content().url().toURL().toString();
            return Response.ok(imageUrl).build();
        } catch (Exception ex) {
            // Log do erro e retorno de uma resposta de falha
            UncaughtExceptions.LOGGER.error("Erro ao gerar imagem: " + ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao gerar imagem: " + ex.getMessage()))
                    .build();
        }
    }

}
