package security.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.security.auth.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JWTGenerator jwtGenerator;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        // Armar JWTUser desde el principal
        JWTUser jwtUser = new JWTUser(user.getId(), user.getUsername(), user.getRole(), user.getCompany());

        String token = jwtGenerator.generate(jwtUser);

        // Preparar respuesta JSON
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("username", user.getUsername());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), tokenMap);
    }
}
