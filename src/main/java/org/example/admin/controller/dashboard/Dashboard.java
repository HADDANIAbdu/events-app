package org.example.admin.controller.dashboard;


import jakarta.validation.Valid;
import org.example.admin.DTO.UserDTO;
import org.example.admin.model.AppUser;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
public class Dashboard {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Value("${api.url}")
    private String apiUrl;

    @GetMapping("/Dashboard")
    public String dashboard(Model model, Principal principal) {
        ResponseEntity<ResponseMessage> response = restTemplate.getForEntity(
                apiUrl+"/api/v1/users/admins/count", ResponseMessage.class);
        model.addAttribute("users_count", response.getBody().getData());

        response = restTemplate.getForEntity(
                apiUrl+"/api/v1/events/count", ResponseMessage.class);
        model.addAttribute("events_count", response.getBody().getData());

        response = restTemplate.getForEntity(
                apiUrl+"/api/v1/payments/count", ResponseMessage.class);
        model.addAttribute("payments_count", response.getBody().getData());

        response = restTemplate.getForEntity(
                apiUrl+"/api/v1/users/participants/count", ResponseMessage.class);
        model.addAttribute("participants_count", response.getBody().getData());

        model.addAttribute("success", userService.findUserFullName(principal.getName()));
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        AppUser user = userService.findUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("success", false);
        model.addAttribute("error", false);
        return "user/profile";
    }

    @PostMapping("/update/profile")
    public String profile(@Valid @ModelAttribute UserDTO userDTO, BindingResult result, Model model){
        if(result.hasErrors()) return "user/profile";
        AppUser Appuser = (AppUser) model.getAttribute("user");
        AppUser user = userService.updateUser(Appuser.getId(), userDTO);
        if(user != null){
            model.addAttribute("user", user);
            model.addAttribute("success", "Profile updated successfully !");
            return "user/profile";
        }
        model.addAttribute("error","Error updating profile !");
        return "user/profile";
    }
}
