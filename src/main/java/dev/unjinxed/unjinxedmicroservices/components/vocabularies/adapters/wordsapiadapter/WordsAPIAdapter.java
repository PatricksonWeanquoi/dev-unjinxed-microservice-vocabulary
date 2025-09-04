package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.enums.WordEnum;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;
import reactor.core.publisher.Mono;

public interface WordsAPIAdapter {
    Mono<WordResponse> getWord(WordEnum wordEnum, String word);
}
