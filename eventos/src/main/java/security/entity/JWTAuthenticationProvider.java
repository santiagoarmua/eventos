package security.entity;

import exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

public class JWTAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationProvider.class);

    private final JWTValidator validator;

    @Autowired
    public JWTAuthenticationProvider(JWTValidator validator) {
        this.validator = validator;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        // No se usa
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        JWTAuthenticationToken jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        JWTUser jwtUser = validator.validate(token);

        if (Objects.isNull(jwtUser)) {
            LOG.error("Token inválido. No se pudo validar el usuario.");
            throw new NotFoundException("Token JWT inválido.");
        }

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(jwtUser.getRole());

        return new JWTUserDetails(jwtUser.getUsername(), token, jwtUser.getId(), grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
