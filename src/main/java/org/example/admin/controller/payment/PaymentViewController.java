package org.example.admin.controller.payment;

import jakarta.validation.Valid;
import org.example.admin.DTO.PaymentDTO;
import org.example.admin.model.Payment;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.PaymentService;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentViewController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String payments(Model model) {
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/payments/", ResponseMessage.class);
        List<Payment> payments = (List<Payment>) response.getBody().getData();

        model.addAttribute("payments", payments);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", model.getAttribute("error"));
        return "payment/paymentsList";
    }

    @GetMapping("/create")
    public String create(Model model) {
        PaymentDTO paymentDTO = new PaymentDTO();
        model.addAttribute("paymentDTO", paymentDTO);
        model.addAttribute("success", false);
        model.addAttribute("error", false);
        return "payment/addPayment";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute PaymentDTO paymentDTO, BindingResult result, Model model){
        if (result.hasErrors()) return "payment/addPayment";
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(
                apiUrl+"/api/v1/payments/create", paymentDTO, ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else{
            model.addAttribute("error", response.getBody().getMessage());
        }
        return "payment/addPayment";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirect) {
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/payments/delete/" + id,
                HttpMethod.DELETE,
                null,
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success"))
            redirect.addFlashAttribute("success", response.getBody().getMessage());
        else redirect.addFlashAttribute("error", response.getBody().getMessage());
        return "redirect:/payments/";
    }

    @GetMapping("/update/{id}")
    public String updateEvent(@PathVariable int id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        if(payment != null){
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setAmount(payment.getAmount());
            paymentDTO.setMethod(payment.getMethod());
            paymentDTO.setStatus(payment.getStatus().toString());
            paymentDTO.setParticipantId(payment.getParticipant().getId());
            model.addAttribute("paymentDTO", paymentDTO);
            model.addAttribute("id",id);
            model.addAttribute("success", false);
            model.addAttribute("error", false);
            return "payment/updatePayment";
        }
        return "redirect:/payments/";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@Valid @ModelAttribute PaymentDTO paymentDTO,BindingResult result,
                              @PathVariable Long id, Model model) {
        if(result.hasErrors()) return "payment/updatePayment";
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/payments/update/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(paymentDTO,new HttpHeaders()),
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else model.addAttribute("error", response.getBody().getMessage());
        return "payment/updatePayment";
    }
}
