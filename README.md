# langchaindemo

Este projeto utiliza Quarkus, o Supersonic Subatomic Java Framework. Para mais informações sobre Quarkus, visite: [quarkus.io](https://quarkus.io/).

## Rodando a aplicação no modo de desenvolvimento

Você pode rodar sua aplicação em modo de desenvolvimento, que permite codificação em tempo real usando:
```shell
./mvnw compile quarkus:dev
```

> **_NOTA:_** O Quarkus agora inclui uma UI de Desenvolvimento, que está disponível apenas no modo de desenvolvimento em http://localhost:8080/q/dev/.

## Empacotamento e execução da aplicação

A aplicação pode ser empacotada usando:
```shell
./mvnw package
```
Isso produz o arquivo `quarkus-run.jar` no diretório `target/quarkus-app/`. Note que não é um _über-jar_, pois as dependências são copiadas para o diretório `target/quarkus-app/lib/`.

A aplicação agora pode ser executada usando:
```shell
java -jar target/quarkus-app/quarkus-run.jar
```

Para construir um _über-jar_, execute o comando:
```shell
./mvnw package -Dquarkus.package.type=uber-jar
```

A aplicação, empacotada como um _über-jar_, agora pode ser executada usando:
```shell
java -jar target/*-runner.jar
```

## Criando um executável nativo

Você pode criar um executável nativo usando:
```shell
./mvnw package -Dnative
```

Ou, se você não tiver o GraalVM instalado, pode construir o executável nativo em um contêiner usando:
```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Você pode então executar seu executável nativo com:
```shell
./target/langchaindemo-1.0.0-SNAPSHOT-runner
```

Para mais informações sobre a construção de executáveis nativos, consulte [maven tooling guide](https://quarkus.io/guides/maven-tooling).

### REST com langchain4j

Esta API permite a interação com modelos de linguagem da OpenAI para responder perguntas, gerar receitas ou imagens.

#### Endpoints:

- **POST /answer**
    - Produz texto simples.
    - Consome JSON.
    - Recebe uma pergunta e retorna uma resposta do modelo de linguagem.

- **POST /answer/model**
    - Utiliza um modelo customizado para responder perguntas.

- **GET /receita**
    - Gera uma receita com base em ingredientes especificados.

- **POST /imagem**
    - Gera uma imagem baseada em uma descrição textual.

Para mais detalhes sobre como começar com serviços REST, veja a [seção do guia relacionado](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources).
