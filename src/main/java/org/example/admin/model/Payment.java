package org.example.admin.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.paypal.api.payments.Participant;
import jakarta.persistence.*;
import org.example.admin.Enum.PaymentsStatus;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false)
    private String  method;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentsStatus status;

    @ManyToOne
    @JoinColumn(name = "participant_Id", nullable = false)
    @JsonBackReference
    private AppUser participant;

    public Payment() {}
    public Payment(double amount, String method, PaymentsStatus status, AppUser participant) {
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.participant = participant;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public PaymentsStatus getStatus() { return status; }
    public AppUser getParticipant() { return participant; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setMethod(String method) { this.method = method; }
    public void setStatus(PaymentsStatus status) { this.status = status; }
    public void setParticipant(AppUser participant) { this.participant = participant; }
}
