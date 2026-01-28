package org.example.admin.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
public class accessDenied {
    public  static  final Logger logger = Logger.getLogger(accessDenied.class.getName());
    @GetMapping("/access-denied")
    public String access_Denied(HttpSession session) {
        logger.info("user "+session.getAttribute("loggedInUser")+" access denied");
        return "AccessDenied";
    }
}
