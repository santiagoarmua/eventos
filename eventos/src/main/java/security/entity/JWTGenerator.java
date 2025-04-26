package security.entity;

import org.springframework.stereotype.Component;
import utils.JWTConstants;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JWTGenerator {
    public String generate(JWTUser jwtUser) {
        // Crear claims
        Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + JWTConstants.EXPIRATION_TIME));
        claims.put(JWTConstants.USER_ID, jwtUser.getId());
        claims.put(JWTConstants.ROLE, jwtUser.getRole());
        claims.put(JWTConstants.COMPANY, jwtUser.getCompany());

        // Usar una clave secreta codificada en base64 (más segura)
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWTConstants.YOUR_SECRET);
        SecretKeySpec signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
