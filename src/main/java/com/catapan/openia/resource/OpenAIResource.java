package com.catapan.openia.resource;

import com.catapan.openia.dto.MyQuestion;
import com.catapan.openia.dto.MyStructuredTemplate;
import com.catapan.openia.dto.MyStructuredTemplate.PromptDeReceita;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
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
import org.jboss.logging.Logger;

import java.util.Arrays;

@Path("/openai")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public class OpenAIResource {

    private static final Logger LOGGER = Logger.getLogger(OpenAIResource.class);

    @ConfigProperty(name = "quarkus.langchain4j.openai.api-key")
    String apiKey;

    @Inject
    private ChatLanguageModel chatModel;

    /**
     * Endpoint que recebe uma pergunta e retorna a resposta correspondente gerada pelo modelo de linguagem.
     * Utiliza a API da OpenAI para gerar respostas a partir das perguntas dos usuários.
     */
    @POST
    @Path("/answer/generate")
    public Response chatWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());

        // Geração da resposta utilizando o modelo de linguagem configurado.
        String answer = chatModel.generate(question.question());

        LOGGER.info("Resposta: " + answer);

        // Criação e retorno da resposta HTTP com o texto gerado.
        return Response.ok(answer).build();
    }

    /**
     * Endpoint que utiliza um modelo personalizado de linguagem para responder perguntas.
     * Utiliza a classe OpenAiChatModelBuilder para configurar e criar um modelo de linguagem.
     */
    @POST
    @Path("/answer/model")
    public Response chatModelWithOpenAI(MyQuestion question) {
        LOGGER.info("Recebido: " + question.question());

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

        LOGGER.info("Resposta: " + customModel);
        return Response.ok(customModel.generate(question.question())).build();
    }

    /**
     * Endpoint para criar uma receita culinária com base em ingredientes especificados.
     * Utiliza o modelo de linguagem para gerar uma receita formatada a partir de um prompt estruturado.
     */
    @GET
    @Path("/recipe")
    public String foodRecipe() {
        @java.lang.SuppressWarnings("java:S1481")
        MyStructuredTemplate template = new MyStructuredTemplate();  // Estrutura base para criação de prompts.

        // Criação de um prompt de receita com ingredientes específicos.
        PromptDeReceita promptDeReceita = new PromptDeReceita();
        promptDeReceita.prato = "Assado";
        promptDeReceita.ingredientes = Arrays.asList("carne", "tomate", "cebola", "pimentao");

        // Conversão do prompt estruturado para texto que será enviado ao modelo de linguagem.
        Prompt prompt = StructuredPromptProcessor.toPrompt(promptDeReceita);

        // Geração da receita com base no texto do prompt e retorno do texto da receita.
        return chatModel.generate(prompt.text());
    }

    /**
     * Endpoint que gera uma imagem com base em uma descrição textual fornecida.
     * Utiliza a classe OpenAiImageModelBuilder para configurar e criar um modelo de geração de imagem.
     */
    @POST
    @Path("/image")
    public String generateImage(@Valid MyQuestion question) {
        try {
            LOGGER.info("Recebido: " + question.question());

            // Configuração do modelo de geração de imagem com OpenAiImageModelBuilder
            ImageModel imageModel = new OpenAiImageModel.OpenAiImageModelBuilder()
                    .apiKey(apiKey)  // Utiliza a chave API para autenticação no serviço da OpenAI.
                    // Define o modelo "dall-e", especializado na criação de imagens baseadas em descrições textuais.
                    .modelName("dall-e")
                    .build();

            // Geração da imagem a partir da descrição textual e obtenção do URL da imagem resultante.
            String response = imageModel.generate(question.question()).content().url().toURL().toString();
            LOGGER.info("Resposta: " + response);
            return response;
        } catch (Exception ex) {
            LOGGER.error("Erro ao gerar imagem: " + ex.getMessage());
            return null;  // Retorna null em caso de falha na geração da imagem.
        }
    }

}
