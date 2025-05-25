package ru.itis.api.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of possible transaction categories")
public enum Category {
    @Schema(name = "TICKETS", description = "Transportation tickets (flights, trains, etc.)")
    TICKETS,

    @Schema(name = "HOTELS", description = "Hotel or accommodation expenses")
    HOTELS,

    @Schema(name = "FOOD", description = "Food and dining expenses")
    FOOD,

    @Schema(name = "ENTERTAINMENT", description = "Activities, tours, sightseeing")
    ENTERTAINMENT,

    @Schema(name = "INSURANCE", description = "Travel insurance costs")
    INSURANCE,

    @Schema(name = "OTHERS_EXPENSES", description = "Other miscellaneous expenses")
    OTHERS_EXPENSES
}