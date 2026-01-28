package org.example.admin.controller.user;

import jakarta.validation.Valid;
import org.example.admin.DTO.EventDTO;
import org.example.admin.DTO.UserDTO;
import org.example.admin.model.AppUser;
import org.example.admin.model.Event;
import org.example.admin.model.Payment;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.EventService;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserViewController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private UserService userService;

    @GetMapping("/admins")
    public String listUsers(Model model){
        ResponseEntity<ResponseMessage> response =
                restTemplate.getForEntity(apiUrl+"/api/v1/users/admins", ResponseMessage.class);
        List<AppUser> admins = (List<AppUser>) response.getBody().getData();
        model.addAttribute("admins", admins);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", model.getAttribute("error"));
        return "user/usersList";
    }

    @GetMapping("/create")
    public String createUser(Model model){
        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("success", false);
        model.addAttribute("error", false);
        return "user/addUser";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute UserDTO userDTO, BindingResult result, Model model){
        if(result.hasErrors()) return "user/addUser";
        String ListUrl = "/users/admins";
        if(userDTO.getRole().equals("participant")) ListUrl = "/users/participants";
        model.addAttribute("url", ListUrl);
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(
                apiUrl+"/api/v1/users/create", userDTO, ResponseMessage.class);
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else {
            model.addAttribute("error", response.getBody().getMessage());
        }
        return  "user/addUser";
    }
    @GetMapping("/update/{id}")
    public String updateEvent(@PathVariable int id, Model model) {
        AppUser user = userService.findUserById(id);
        if(user != null){
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setFullName(user.getFullName());
            userDTO.setRole(user.getRole());
            String ListUrl = "/users/admins";
            if(userDTO.getRole().equals("participant")) ListUrl = "/users/participants";
            model.addAttribute("url", ListUrl);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("id",id);
            model.addAttribute("success", false);
            model.addAttribute("error", false);
            return "user/updateUser";
        }
        return "redirect:/users/";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@Valid @ModelAttribute UserDTO userDTO,BindingResult result,
                              @PathVariable Long id, Model model) {
        if(result.hasErrors()) return "user/updateUser";
        String ListUrl = "/users/admins";
        if(userDTO.getRole().equals("participant")) ListUrl = "/users/participants";
        model.addAttribute("url", ListUrl);
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/users/update/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(userDTO,new HttpHeaders()),
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else model.addAttribute("error", response.getBody().getMessage());
        return "user/updateUser";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEvent(@PathVariable int id, RedirectAttributes redirect) {
        String urlList = "admins";
        if(userService.findUserById(id).getRole().equals("participant")) urlList = "participants";
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/users/delete/" + id,
                HttpMethod.DELETE,
                null,
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success"))
            redirect.addFlashAttribute("success", response.getBody().getMessage());
        else redirect.addFlashAttribute("error", response.getBody().getMessage());
        return "redirect:/users/"+urlList;
    }

    @GetMapping("/participants")
    public String listParticipants(Model model){
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/users/participants", ResponseMessage.class
        );
        List<AppUser> participants = (List<AppUser>) response.getBody().getData();
        model.addAttribute("participants", participants);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", model.getAttribute("error"));
        return "participant/participantsList";
    }

    @GetMapping("/participants/{id}/events")
    public String ParticipantListEvents(@PathVariable int id, Model model){
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/users/participants/"+id+"/events", ResponseMessage.class
        );
        List<Event> events = (List<Event>) response.getBody().getData();
        model.addAttribute("events", events);
        model.addAttribute("participantId", id);
        model.addAttribute("success", model.getAttribute("success"));
        return "participant/participantEvents";
    }

    @GetMapping("/participants/{id}/add-events")
    public String ParticipantAddEvents(@PathVariable int id, Model model){
        ResponseEntity<ResponseMessage<List<Event>>> allEvents = restTemplate.exchange(
                apiUrl + "/api/v1/events/",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        ResponseEntity<ResponseMessage<List<Event>>> response = restTemplate.exchange(
                apiUrl+"/api/v1/users/participants/"+id+"/events",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        List<Event> events = allEvents.getBody().getData();
        List<Event> participantEvents = response.getBody().getData();
        Set<Integer> participantEventIds = participantEvents.stream().map(Event::getId)
                .collect(Collectors.toSet());
        events.removeIf(event -> participantEventIds.contains(event.getId()));

        model.addAttribute("events", events);
        model.addAttribute("participantId", id);
        model.addAttribute("success", model.getAttribute("success"));
        return "participant/addEventsToParticipant";
    }

    @PostMapping("/participants/{id}/add-events/{eventId}")
    public String AddEventsToParticipant(@PathVariable int id, @PathVariable int eventId,
                                         RedirectAttributes redirect){
        ResponseEntity<ResponseMessage<AppUser>> response = restTemplate.exchange(
                apiUrl+"/api/v1/users/participants/"+id+"/add-events/"+eventId,
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {}
        );
        redirect.addFlashAttribute("success", response.getBody().getMessage());
        return "redirect:/users/participants/"+id+"/add-events";
    }

    @DeleteMapping("/participants/{id}/delete-event/{eventId}")
    public String DeleteEventForParticipant(@PathVariable int id, @PathVariable int eventId,
                                            RedirectAttributes redirect){
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/users/participants/" + id + "/delete-event/" + eventId,
                HttpMethod.DELETE,
                null,
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success"))
            redirect.addFlashAttribute("success", response.getBody().getMessage());
        else redirect.addFlashAttribute("error", response.getBody().getMessage());
        return "redirect:/users/participants/"+id+"/events";
    }

    @GetMapping("/participants/{id}/payments")
    public String ParticipantListPayments(@PathVariable int id, Model model){
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/users/participants/"+id+"/payments", ResponseMessage.class
        );
        List<Payment> payments = (List<Payment>) response.getBody().getData();
        model.addAttribute("payments", payments)        ;
        return "participant/participantEvents";
    }

}
