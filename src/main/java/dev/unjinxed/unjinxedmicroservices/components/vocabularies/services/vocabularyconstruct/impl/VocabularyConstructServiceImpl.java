package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct.impl;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesResults;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesSenses;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.vocabularycontruct.WordDefinition;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.OxfordDictionariesService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.WordsAPIService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct.VocabularyConstructService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
public class VocabularyConstructServiceImpl implements VocabularyConstructService {

    WordsAPIService randomWordsService;
    OxfordDictionariesService oxfordDictionariesService;

    public VocabularyConstructServiceImpl(@Autowired WordsAPIService randomWordsService,
                                          @Autowired OxfordDictionariesService oxfordDictionariesService) {
        this.randomWordsService = randomWordsService;
        this.oxfordDictionariesService = oxfordDictionariesService;
    }
    @Override
    public Mono<WordDefinition> getRandomWordDefinition() {
        return this.composeDefinition();
    }

    @Override
    public Mono<ServerResponse> getRandomWordDefinitionServerResponse() {
        return this.getRandomWordDefinition()
                .flatMap(wordDefinition -> ServerResponse
                        .ok()
                        .body(Mono.just(wordDefinition), new ParameterizedTypeReference<>(){}))
                .onErrorResume(HttpClientErrorException.class, httpClientErrorException -> ServerResponse
                        .status(httpClientErrorException.getStatusCode().value())
                        .body(Mono.just(httpClientErrorException.getResponseBodyAsString()), new ParameterizedTypeReference<Mono<Object>>(){})
                ).onErrorResume(Throwable.class, e -> ServerResponse
                        .status(500)
                        .body(Mono.just(e.getMessage()), new ParameterizedTypeReference<Mono<Object>>(){}));

    }

    private Mono<WordDefinition> composeDefinition() {
        return this.randomWordsService.getRandomWord()
                .map(WordResponse::getWord)
                .flatMap(this.oxfordDictionariesService::getWordDefinition)
                .map(this::buildWordDefinition)
                .flatMap(wordDefinition -> {
                    if (ObjectUtils.isEmpty(wordDefinition) ||
                            CollectionUtils.isEmpty(wordDefinition.getDefinitions()) ||
                            CollectionUtils.isEmpty(wordDefinition.getExamples())){
                        return Mono.error(new RuntimeException("Could not compose word definition"));
                    }
                    return Mono.just(wordDefinition);
                });
    }

    private WordDefinition buildWordDefinition(OxfordDictionariesResponse oxfordDictionariesResponse) {
        log.info("buildWordDefinition(): Entering... {}", oxfordDictionariesResponse);
        final WordDefinition wordDefinition = WordDefinition.builder()
                .lexicalCategories(new ArrayList<>())
                .build();
        if(ObjectUtils.isEmpty(oxfordDictionariesResponse) || StringUtil.isNullOrEmpty(oxfordDictionariesResponse.getWord())) {
            return null;
        }

        wordDefinition.setWord(oxfordDictionariesResponse.getWord());

        OxfordDictionariesSenses dictionariesSenses = oxfordDictionariesResponse.getResults()
            .stream()
            .map(OxfordDictionariesResults::getLexicalEntries)
            .flatMap(Collection::stream)
            .filter(oxfordDictionariesLexicalEntry -> Objects.nonNull(oxfordDictionariesLexicalEntry.getLexicalCategory()))
            .flatMap(oxfordDictionariesLexicalEntry -> {
                wordDefinition.getLexicalCategories().add(oxfordDictionariesLexicalEntry.getLexicalCategory());
                log.info("oxfordDictionariesLexicalEntry.lexicalCategory: {}", oxfordDictionariesLexicalEntry.getLexicalCategory());
                return oxfordDictionariesLexicalEntry.getEntries().stream();
            })
            .flatMap(oxfordDictionariesEntry -> oxfordDictionariesEntry.getSenses().stream())
            .filter(oxfordDictionariesSenses -> !CollectionUtils.isEmpty(oxfordDictionariesSenses.getDefinitions()))
            .filter(oxfordDictionariesSenses -> !CollectionUtils.isEmpty(oxfordDictionariesSenses.getExamples()))
            .reduce(OxfordDictionariesSenses.builder().definitions(new ArrayList<>()).examples(new ArrayList<>()).build(), (oxfordDictionariesSenseFinal, oxfordDictionariesSenses) -> {
                oxfordDictionariesSenseFinal.getDefinitions().addAll(oxfordDictionariesSenses.getDefinitions());
                oxfordDictionariesSenseFinal.getExamples().addAll(oxfordDictionariesSenses.getExamples());
                return oxfordDictionariesSenseFinal;
            });
        wordDefinition.setDefinitions(dictionariesSenses.getDefinitions());
        wordDefinition.setExamples(dictionariesSenses.getExamples());
        return wordDefinition;

    }
}
