package dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WordResponse {
    String word;
    List<WordResult> results;
    List<WordResult> definitions;
    List<String> examples;
}
