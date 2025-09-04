package dev.unjinxed.unjinxedmicroservices.components.vocabularies.handlers;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.OxfordDictionariesService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.WordsAPIService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct.VocabularyConstructService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

import static dev.unjinxed.unjinxedmicroservices.constants.GlobalLoggerConstants.SERVING_PATH;

@Component
@Slf4j
public class VocabularyControllerHandler {
    WordsAPIService wordsAPIService;
    OxfordDictionariesService oxfordDictionariesService;
    VocabularyConstructService vocabularyConstructService;

    public VocabularyControllerHandler(@Autowired
                                       WordsAPIService randomWordsService,
                                       @Autowired
                                       OxfordDictionariesService oxfordDictionariesService,
                                       @Autowired
                                       VocabularyConstructService vocabularyConstructService) {
        this.wordsAPIService = randomWordsService;
        this.oxfordDictionariesService = oxfordDictionariesService;
        this.vocabularyConstructService = vocabularyConstructService;
    }

    public Mono<ServerResponse> randomWordHandler(ServerRequest request) {
        log.info("serving path /random \nactual path: {}", request.path());
        return this.wordsAPIService.getRandomWordServerResponse();
    }
    public Mono<ServerResponse> wordDefinitionsHandler(ServerRequest request) {
        log.info("serving path /definition \nactual path: {}", request.path());
        String word  = getWordInPath(request);
        return Objects.nonNull(word) ? this.wordsAPIService.getWordDefinitionsServerResponse(word):
                ServerResponse.badRequest().build();
    }

    public Mono<ServerResponse> wordExamplesHandler(ServerRequest request) {
        log.info("serving path /example \nactual path: {}", request.path());
        String word  = getWordInPath(request);
        return Objects.nonNull(word) ? this.wordsAPIService.getWordExamplesServerResponse(word):
                ServerResponse.badRequest().build();
    }
    public Mono<ServerResponse> randomWordDefinitionHandler(ServerRequest request) {
        log.info(SERVING_PATH, request.path());
        return this.wordsAPIService.getRandomWordDefinitionsServerResponse();
    }

    private String getWordInPath(ServerRequest request) {
        String word = null;
        try {
            word = request.pathVariable("word");
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("IllegalArgumentException - word to search not found in path, error ", illegalArgumentException);
        }
        return word;
    }

}
