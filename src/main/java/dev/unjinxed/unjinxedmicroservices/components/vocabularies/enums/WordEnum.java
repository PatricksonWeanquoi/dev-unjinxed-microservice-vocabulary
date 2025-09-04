package dev.unjinxed.unjinxedmicroservices.components.vocabularies.enums;

public enum WordEnum {
    RANDOM("random"),
    DEFINITIONS("definitions"),
    EXAMPLES("examples");

    final String urlPath;
    WordEnum(String urlPath) {
        this.urlPath = urlPath;
    }

}
