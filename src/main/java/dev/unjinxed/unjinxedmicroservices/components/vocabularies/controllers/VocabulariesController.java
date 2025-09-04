package dev.unjinxed.unjinxedmicroservices.components.vocabularies.controllers;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.handlers.VocabularyControllerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@RestController
public class VocabulariesController {

    VocabularyControllerHandler vocabularyControllerHandler;

    public VocabulariesController(@Autowired VocabularyControllerHandler vocabularyControllerHandler) {
        this.vocabularyControllerHandler = vocabularyControllerHandler;
    }
    @Bean
    public RouterFunction<ServerResponse> vocabulariesRoutes() {
        return RouterFunctions.route()
                .path("/vocabularies/v1", b1 ->
                        b1
                                .path("/random", b2 ->
                                        b2
                                                .GET("/word", vocabularyControllerHandler::randomWordHandler)
                                                .GET("/definition", vocabularyControllerHandler::randomWordDefinitionHandler))
                                .GET("define/{word}", vocabularyControllerHandler::wordDefinitionsHandler)
                                .GET("example/{word}", vocabularyControllerHandler::wordExamplesHandler)
                ).build();
    }
}
