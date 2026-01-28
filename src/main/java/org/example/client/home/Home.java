package org.example.client.home;

import org.example.admin.model.Event;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.EmailService;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class Home {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Value("${api.url}")
    private String apiUrl;

    @GetMapping("/Home")
    public String home(Model model) {
        logger.info("mapping home "+model.getAttribute("success"));
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/events/", ResponseMessage.class);
        List<Event> events = (List<Event>) response.getBody().getData();
        model.addAttribute("events", events);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", false);
        return "home_page";
    }

    @PostMapping("/events/book/{id}")
    public String book(RedirectAttributes redirect, @PathVariable int id, Principal principal) {
        try {

            ResponseEntity<ResponseMessage<Event>> response = restTemplate.exchange(
                    apiUrl + "/api/v1/events/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Event event =  response.getBody().getData();
            /*event.setId((Integer) data.get("id"));
            event.setTitle((String) data.get("title"));
            event.setDescription((String) data.get("description"));
            event.setDate(LocalDate.parse((String) data.get("date")));
            event.setLocation((String) data.get("location"));
            event.setCapacity((Integer) data.get("capacity"));
            event.setPrice((Double) data.get("price"));*/
            if (event.getCapacity() > 0) {
                event.setCapacity(event.getCapacity() - 1);

                restTemplate.exchange(
                        apiUrl + "/api/v1/events/update/" + id,
                        HttpMethod.PUT,
                        new HttpEntity<>(event),
                        ResponseMessage.class
                );
                String fullName = userService.findUserFullName(principal.getName());
                String userEmail = principal.getName();
                String confirmBookingUrl = apiUrl+"/event/Confirm-booking";
                String subject = "Event Booking Confirmation";
                String body = String.format(
                                "Dear %s,\n\n" +
                                "You are one step away to confirm your booking for %s event.\n" +
                                "Confirm your booking by clicking this url :\n"+
                                "%s\n\n" +
                                "Thank you!",
                        fullName, event.getTitle(), confirmBookingUrl+"?eventId="+event.getId());

                emailService.sendEmail(userEmail, subject, body);
                redirect.addFlashAttribute("success",
                        "Booked Successfully. A confirmation payment email has been sent to you.");
            } else {
                redirect.addFlashAttribute("error", "Sorry, the event is fully booked.");
            }
        } catch (MailException e) {
            redirect.addFlashAttribute("error", "Booking Failed, failed to send email.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "An error occurred while booking the event.");
        }
        return "redirect:/Home";
    }
}
