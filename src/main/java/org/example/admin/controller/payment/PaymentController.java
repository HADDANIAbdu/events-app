package org.example.admin.controller.payment;

import jakarta.validation.Valid;
import org.example.admin.DTO.PaymentDTO;
import org.example.admin.model.Payment;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public ResponseEntity<ResponseMessage> AllPayments() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Payments found Successfully !", paymentService.AllPayments()
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        Payment payment = paymentService.savePayment(paymentDTO);
        if(payment != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Payment Created Successfully !", payment
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "error","Failed to create payment !", "null"
        ));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updatePayment(@Valid @RequestBody PaymentDTO paymentDTO, @PathVariable int id) {
        Payment payment = paymentService.updatePayment(id, paymentDTO);
        if(payment != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Payment Updated Successfully !", payment
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "error","Failed to update payment !", "null"
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deletePayment(@PathVariable int id) {
        if(paymentService.deletePayment(id)) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Payment Deleted Successfully !","id: "+id
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "error","Failed to delete payment !", "null"
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseMessage> countCompletedPayments() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Completed Payments Counted Successfully !",
                paymentService.CountCompletedPayments()
        ));
    }
}
