package org.example.admin.DTO;

import jakarta.validation.constraints.NotNull;

public class addEventDTO {
    @NotNull(message = "EventId is required")
    private int eventId;

    public int getEventId() { return eventId; }
}
