package org.example.admin.repository;

import org.example.admin.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    Optional<Payment> findById(int id);
}
