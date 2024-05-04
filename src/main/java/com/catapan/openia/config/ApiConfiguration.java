package com.catapan.openia.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * Classe de configuração para as definições OpenAPI do projeto.
 * Define informações globais sobre a API e configurações do servidor.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "OpenAI Chat API",
                version = "1.0.4-SNAPSHOT",
                description = "Esta API permite a interação com modelos de linguagem da OpenAI para responder perguntas, " +
                        "gerar receitas ou imagens baseadas em descrições textuais. Desenvolvida utilizando Quarkus e MicroProfile.",
                contact = @Contact(
                        name = "Suporte API",
                        url = "http://example.com/contact",
                        email = "dcatapan@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = @Server(
                url = "http://localhost:8080",
                description = "Servidor Local"
        )
)
public class ApiConfiguration {
}
