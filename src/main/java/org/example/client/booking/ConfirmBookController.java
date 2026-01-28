package org.example.client.booking;


import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.example.admin.DTO.PaymentDTO;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.PaymentService;
import org.example.admin.service.PaypalService;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmBookController {

    @Autowired
    private PaypalService paypalService;

    @GetMapping("/payment/confirm")
    public ResponseEntity<ResponseMessage> confirmPayment(@RequestParam("paymentId") String paymentId,
    @RequestParam("PayerID") String payerId) {
        Payment payment;
        PaymentDTO paymentDTO = new PaymentDTO();
        try {
            payment = paypalService.executePayment(paymentId, payerId);
            paymentDTO.setAmount(Double.valueOf(payment.getTransactions().get(0).getAmount().getTotal()));
            paymentDTO.setMethod(payment.getPayer().getPaymentMethod());
            if (payment.getState().equals("approved")) {
                return ResponseEntity.ok().body(new ResponseMessage(
                        "success","payment confirmed successfully !",paymentDTO
                ));
            }
        } catch (PayPalRESTException e) {
            return ResponseEntity.ok().body(new ResponseMessage(
                    "error","Failed to confirm payment !","null"
            ));
        }
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","payment confirmed successfully !",paymentDTO
        ));
    }
}
