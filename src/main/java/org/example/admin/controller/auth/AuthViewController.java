package org.example.admin.controller.auth;


import jakarta.validation.Valid;
import org.example.admin.DTO.LoginDTO;
import org.example.admin.DTO.RegisterDTO;
import org.example.admin.payload.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;


@Controller
@RequestMapping("/app")
public class AuthViewController {
    private  final Logger logger = Logger.getLogger(this.getClass().getName());
    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @GetMapping("/login")
    public String login(Model model) {
        LoginDTO loginDTO = new LoginDTO();
        model.addAttribute("loginDTO",loginDTO);
        model.addAttribute("error", false);
        return "auth/login";
    }

    /*@PostMapping("/perform-login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, RedirectAttributes redirectAttributes, Model model) {
        String fullUrl = apiUrl + "/api/v1/auth/login";
        // Call the login API
        logger.info("hello world");
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(
                fullUrl,
                loginDTO,
                ResponseMessage.class
        );
        if ("error".equals(response.getBody().getStatus())) {
            model.addAttribute("error",response.getBody().getMessage());
            return "auth/login";
        } else {
            redirectAttributes.addFlashAttribute("success", response.getBody().getMessage());
            return "redirect:/Dashboard";
        }
    }*/

    @GetMapping("/register")
    public String register(Model model) {
        RegisterDTO registerDTO = new RegisterDTO();
        model.addAttribute("registerDTO",registerDTO);
        model.addAttribute("success", false);
        return "auth/register";  // The registration form template
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO, BindingResult bindingResult, Model model) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
            bindingResult.addError(new FieldError("registerDTO",
                    "confirmPassword", "Passwords do not match"));
        }
        if (bindingResult.hasErrors()) return "auth/register";
        String fullUrl = apiUrl+"/api/v1/auth/register";
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(
                fullUrl,
                registerDTO,
                ResponseMessage.class
        );
        if ("success".equals(response.getBody().getStatus())) {
            model.addAttribute("success", response.getBody().getMessage());
        }
        else{
            bindingResult.addError(new FieldError("registerDTO","email",
                    response.getBody().getMessage()));
        }
        return "auth/register"; // Redirect to a success page or show success message
    }
}
