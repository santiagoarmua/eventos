package security.entity;

import exception.UnauthorizedException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import utils.JWTConstants;

import java.security.SignatureException;
import java.util.Date;
import java.util.Objects;

public class JWTUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JWTUtils.class);

    // Genera un token JWT y lo agrega como header a la respuesta
    protected static void addAuthentication(HttpServletResponse res, String username, String role) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + JWTConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, JWTConstants.YOUR_SECRET)
                .compact();
        res.addHeader(JWTConstants.AUTHORIZATION_HEADER, JWTConstants.BEARER_TOKEN + token);
    }

    // Extrae el token JWT desde la request y devuelve un Authentication válido si es correcto
    protected static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(JWTConstants.AUTHORIZATION_HEADER);
        if (Objects.nonNull(token)) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(JWTConstants.YOUR_SECRET)
                        .parseClaimsJws(token.replace(JWTConstants.BEARER_TOKEN, StringUtils.EMPTY))
                        .getBody();

                String user = claims.getSubject();
                String role = (String) claims.get("role");

                return JWTUtils.setAuthenticationToken(user, role);
            } catch (ExpiredJwtException e) {
                LOG.error("Token expirado");
                throw new UnauthorizedException("login-service.token.expired");
            } catch (SignatureException e) {
                LOG.error("Token con firma inválida");
                throw new UnauthorizedException("login-service.token.wrong-format");
            } catch (Exception e) {
                LOG.error("Error general al parsear el token");
                throw new UnauthorizedException("login-service.token.not-found");
            }
        }
        return null;
    }

    private static UsernamePasswordAuthenticationToken setAuthenticationToken(String user, String role) {
        if (Objects.nonNull(user)) {
            return new UsernamePasswordAuthenticationToken(user, null,
                    AuthorityUtils.createAuthorityList(role));
        }
        return null;
    }
}
