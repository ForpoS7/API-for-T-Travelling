package ru.itis.api.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of possible transaction categories")
public enum Category {
    TRANSPORT,
    ACCOMMODATION,
    FOOD,
    ENTERTAINMENT,
    SHOPPING,
    HEALTH,
    COMMUNICATION,
    VISA_DOCUMENTS,
    OTHER
}