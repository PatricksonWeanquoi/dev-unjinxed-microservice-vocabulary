package dev.unjinxed.unjinxedmicroservices.constants;

public class GLOBALCONSTANTS {

    private GLOBALCONSTANTS() {
        throw new IllegalStateException("Utility class; Cannot be instantiated");
    }

    // RapidAPI
    public static final String X_RAPID_API_HOST_HEADER = "X-RapidAPI-Host";
    public static final String X_RAPID_API_KEY_HEADER = "X-RapidAPI-Key";
    public static final String X_RAPID_API_DOMAIN_HEADER = "X-RapidAPI-Domain";

    // oxfordDictionaries
    public static final String OXFORD_APP_ID_HEADER = "app_id";
    public static final String OXFORD_APP_KEY_HEADER = "app_key";

}
