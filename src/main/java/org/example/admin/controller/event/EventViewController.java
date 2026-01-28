package org.example.admin.controller.event;

import jakarta.validation.Valid;
import org.example.admin.DTO.EventDTO;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.EventService;
import org.example.admin.model.Event;
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

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventViewController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public String listEvents(Model model) {
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/events/", ResponseMessage.class);
        List<Event> events = (List<Event>) response.getBody().getData();
        model.addAttribute("events", events);
        model.addAttribute("success", model.getAttribute("success"));
        model.addAttribute("error", model.getAttribute("error"));
        return "event/eventsList";
    }

    @GetMapping("/create")
    public String createEvent(Model model) {
        EventDTO eventDTO = new EventDTO();
        model.addAttribute("eventDTO", eventDTO);
        model.addAttribute("success", false);
        model.addAttribute("error", false);
        return "event/addEvent";
    }

    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute EventDTO eventDTO, BindingResult result, Model model) {
        if (result.hasErrors()) return "event/addEvent";
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(
                apiUrl+"/api/v1/events/create", eventDTO, ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else{
            model.addAttribute("error", response.getBody().getMessage());
        }
        return "event/addEvent";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirect) {
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/events/delete/" + id,
                HttpMethod.DELETE,
                null,
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success"))
            redirect.addFlashAttribute("success", response.getBody().getMessage());
        else redirect.addFlashAttribute("error", response.getBody().getMessage());
        return "redirect:/events/";
    }

    @GetMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        if(event != null){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setTitle(event.getTitle());
            eventDTO.setDescription(event.getDescription());
            eventDTO.setDate(event.getDate());
            eventDTO.setLocation(event.getLocation());
            eventDTO.setCapacity(event.getCapacity());
            model.addAttribute("eventDTO", eventDTO);
            model.addAttribute("id",id);
            model.addAttribute("success", false);
            model.addAttribute("error", false);
            return "event/updateEvent";
        }
        return "redirect:/events/";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@Valid @ModelAttribute EventDTO eventDTO,BindingResult result,
                              @PathVariable Long id, Model model) {
        if(result.hasErrors()) return "event/updateEvent";
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                apiUrl + "/api/v1/events/update/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(eventDTO,new HttpHeaders()),
                ResponseMessage.class
        );
        if(response.getBody().getStatus().equals("success")){
            model.addAttribute("success", response.getBody().getMessage());
        }
        else model.addAttribute("error", response.getBody().getMessage());
        return "event/updateEvent";
    }
}
