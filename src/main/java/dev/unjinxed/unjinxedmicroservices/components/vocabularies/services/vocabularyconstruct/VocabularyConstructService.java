package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.vocabularycontruct.WordDefinition;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface VocabularyConstructService {

    public Mono<WordDefinition> getRandomWordDefinition();

    public Mono<ServerResponse> getRandomWordDefinitionServerResponse();
}
