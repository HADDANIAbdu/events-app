package org.example.admin.controller.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.admin.DTO.LoginDTO;
import org.example.admin.DTO.RegisterDTO;
import org.example.admin.model.AppUser;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody RegisterDTO registerDTO) {
        String message = "";
        log.info("email : "+ registerDTO.getEmail());
        AppUser appUser;
        try{
            appUser = userService.registerNewParticipant(registerDTO.getFullName(), registerDTO.getEmail(),
                    registerDTO.getPassword()
            );

            if (appUser == null) message = "email already in use";
            if(!message.isEmpty()) return ResponseEntity.status(200).body(new
                    ResponseMessage("error",message,"null"));
        }catch (Exception e){
            message = e.getMessage();
            return ResponseEntity.status(500).body(new ResponseMessage("error",message,"null"));
        }
        return ResponseEntity.ok().body(new ResponseMessage("success",
                "User registered successfully.", appUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(loginDTO.getUsername());
            if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String fullName = userService.findUserFullName(userDetails.getUsername());
            return ResponseEntity.ok().body(new ResponseMessage("success",
                    "Welcome " + fullName + " .", userDetails));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(200).body(new ResponseMessage("error", "Invalid username", "null"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(200).body(new ResponseMessage("error", "Invalid Credentials", "null"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("error", "Authentication failed", "null"));
        }
    }
}
