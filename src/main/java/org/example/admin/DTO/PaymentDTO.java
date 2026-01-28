package org.example.admin.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDTO {

    @NotNull(message = "amount must not be null")
    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "method is required")
    private String method;

    @NotBlank(message = "status is required")
    private String status;

    @NotNull(message = "participantId is required")
    private int participantId;

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getParticipantId() { return participantId; }
    public void setParticipantId(int participantId) { this.participantId = participantId; }
}
