package org.example.admin.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        logger.info("roles "+ roles.toString());
        if (roles.contains("ROLE_admin")) {
            getRedirectStrategy().sendRedirect(request, response, "/Dashboard");
        } else if (roles.contains("ROLE_participant")) {
            getRedirectStrategy().sendRedirect(request, response, "/Home");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/app/login");
        }
    }
}
