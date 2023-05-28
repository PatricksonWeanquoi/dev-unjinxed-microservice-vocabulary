package dev.unjinxed.unjinxedmicroservices.components.vocabularies.controllers;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.OxfordDictionariesService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.RandomWordsService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct.VocabularyConstructService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.accept;
@Controller
public class VocabulariesController {
    @Autowired
    RandomWordsService randomWordsService;
    @Autowired
    OxfordDictionariesService oxfordDictionariesService;
    @Autowired
    VocabularyConstructService vocabularyConstructService;
    @Bean
    public RouterFunction<ServerResponse> vocabulariesRoutes() {
        return RouterFunctions.route()
                .GET("/vocabularies/get-a-word", accept(MediaType.APPLICATION_JSON),
                        request -> randomWordsService.getRandomWordServerResponse().blockOptional().get())
                .GET("/vocabularies/definition/{word}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            String word = request.pathVariable("word");
                            return this.oxfordDictionariesService.getWordDefinitionServerResponse(word).blockOptional().get();
                        }
                )
                .GET("/vocabularies/definition", accept(MediaType.APPLICATION_JSON),
                        request -> this.vocabularyConstructService.getRandomWordDefinitionServerResponse().blockOptional().get())
                .build();
    }
}
