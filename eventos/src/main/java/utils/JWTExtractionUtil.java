package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import security.entity.JWTUser;

import static utils.JWTConstants.YOUR_SECRET;

public class JWTExtractionUtil {
    private final ObjectMapper objectMapper;

    public JWTExtractionUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JWTUser getPayloadFromToken(String bearerToken) {
        try {
            String token = bearerToken.replace(JWTConstants.BEARER_TOKEN, "");
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(YOUR_SECRET))
                    .parseClaimsJws(token)
                    .getBody();

            JWTUser user = new JWTUser();
            user.setUsername(claims.getSubject());
            user.setId((Integer) claims.get(JWTConstants.USER_ID));
            user.setRole((String) claims.get(JWTConstants.ROLE));
            // Si más adelante usás company o mail, se puede agregar acá
            return user;

        } catch (Exception e) {
            throw new RuntimeException("No se pudo extraer el payload del token JWT", e);
        }
    }
}
