package security.entity;

import org.springframework.beans.factory.annotation.Value;

import java.security.SignatureException;

public class JWTValidator {
    @Value("${jwt.secret}")
    private String secretKey;

    public JWTUser validate(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            JWTUser user = new JWTUser();
            user.setUsername(body.getSubject());
            user.setId(Long.parseLong(body.get("userId").toString()));
            user.setRole(body.get("role").toString());

            return user;

        } catch (SignatureException | IllegalArgumentException e) {
            System.out.println("Token inválido: " + e.getMessage());
            return null;
        }
    }
}
