package dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OxfordDictionariesSenses {
    List<String> definitions;
    List<Object> domainClasses;
    List<OxfordDictionariesExample> examples;
    String id;
    List<Object> semanticClasses;
    List<String> shortDefinitions;
    List<Object> subsenses;
    List<Object> synonyms;
    List<Object> thesaurusLinks;
    List<Object> notes;
}

