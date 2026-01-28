package org.example.admin.service;

import org.example.admin.DTO.PaymentDTO;
import org.example.admin.Enum.PaymentsStatus;
import org.example.admin.model.AppUser;
import org.example.admin.model.Payment;
import org.example.admin.repository.PaymentRepo;
import org.example.admin.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Payment> AllPayments() {
        return paymentRepo.findAll();
    }

    public Payment savePayment(PaymentDTO paymentDTO) {
        AppUser user = userRepo.findById(paymentDTO.getParticipantId()).orElse(null);
        if(user.getRole().equals("admin")) return null;
        PaymentsStatus status = PaymentsStatus.valueOf(paymentDTO.getStatus());
        Payment payment = new Payment(paymentDTO.getAmount(), paymentDTO.getMethod(),
                status, user);
        return paymentRepo.save(payment);
    }

    public Payment updatePayment(int id, PaymentDTO paymentDTO) {
        AppUser participant = userRepo.findById(paymentDTO.getParticipantId()).orElse(null);
        return paymentRepo.findById(id).map(payment -> {
            payment.setAmount(paymentDTO.getAmount());
            payment.setMethod(paymentDTO.getMethod());
            payment.setStatus(PaymentsStatus.valueOf(paymentDTO.getStatus()));
            payment.setParticipant(participant);
            return paymentRepo.save(payment);
        }).orElse(null);
    }

    public boolean deletePayment(int id) {
        paymentRepo.deleteById(id);
        Payment payment = paymentRepo.findById(id).orElse(null);
        return payment == null;
    }

    public Payment getPaymentById(int id) { return paymentRepo.findById(id).orElse(null); }

    public Long CountCompletedPayments(){
        long count = 0;
        List<Payment> payments = paymentRepo.findAll();
        for(Payment payment : payments){
            if(payment.getStatus().equals(PaymentsStatus.valueOf("completed"))) count++;
        }
        return count;
    }
}
