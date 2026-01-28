package org.example.client.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpSession;
import org.example.admin.DTO.PaymentDTO;
import org.example.admin.model.AppUser;
import org.example.admin.model.Event;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.EventService;
import org.example.admin.service.PaymentService;
import org.example.admin.service.PaypalService;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.LinkedHashMap;

@Controller
public class ConfirmBook {

    @Autowired
    private PaypalService paypalService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @GetMapping("/event/Confirm-booking")
    public String confirmBooking(Model model, @RequestParam("eventId") Long eventId) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", model.getAttribute("error"));
        return "ConfirmBooking";
    }

    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description,
            @RequestParam("eventId") Long eventId,
            HttpSession session
    ) {
        try {
            session.setAttribute("eventId", eventId);
            String cancelUrl = "http://localhost:8080/event/payment/cancel";
            String successUrl = "http://localhost:8080/event/payment/success";
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl,
                    successUrl
            );

            for (Links links: payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println("erooooor : "+e.getMessage());
        }
        return new RedirectView("/event/Confirm-booking?eventId="+eventId);
    }

    @GetMapping("/event/payment/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerID,
                                HttpSession session,
            Principal principal, RedirectAttributes redirect) {
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/payment/confirm?paymentId=" + paymentId + "&PayerID=" + payerID,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        Long eventId = (Long) session.getAttribute("eventId");
        LinkedHashMap dataMap = (LinkedHashMap) response.getBody().getData();
        ObjectMapper mapper = new ObjectMapper();
        PaymentDTO paymentDTO = mapper.convertValue(dataMap, PaymentDTO.class);
        AppUser participant = userService.findUserByUsername(principal.getName());
        paymentDTO.setParticipantId(participant.getId());
        if(response.getBody().getStatus().equals("success")){
            paymentDTO.setStatus("completed");
            redirect.addFlashAttribute("success", response.getBody().getMessage());
        }
        else{
            paymentDTO.setStatus("failed");
            redirect.addFlashAttribute("error", response.getBody().getMessage());
        }
        paymentService.savePayment(paymentDTO);

        return "redirect:/event/Confirm-booking?eventId="+eventId;
    }

    @GetMapping("/event/payment/cancel")
    public String paymentCanceled(@RequestParam("paymentId") String paymentId,
                                  @RequestParam("PayerID") String payerID,
                                  HttpSession session,
            Principal principal, RedirectAttributes redirect) {
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl + "/payment/confirm?paymentId=" + paymentId + "&PayerID=" + payerID,
                ResponseMessage.class
        );
        Long eventId = (Long) session.getAttribute("eventId");
        PaymentDTO paymentDTO = (PaymentDTO) response.getBody().getData();
        AppUser participant = userService.findUserByUsername(principal.getName());
        paymentDTO.setParticipantId(participant.getId());
        paymentDTO.setStatus("cancelled");
        paymentService.savePayment(paymentDTO);
        redirect.addFlashAttribute("error","Payment cancelled !");
        return "redirect:/event/Confirm-booking?eventId"+eventId;
    }
}
