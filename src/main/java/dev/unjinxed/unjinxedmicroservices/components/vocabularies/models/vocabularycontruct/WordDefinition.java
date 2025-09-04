package dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.vocabularycontruct;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesExample;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionarieslexicalCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordDefinition {
    String word;
    List<String> definitions;
    List<OxfordDictionariesExample> examples;
    List<OxfordDictionarieslexicalCategory> lexicalCategories;
}
