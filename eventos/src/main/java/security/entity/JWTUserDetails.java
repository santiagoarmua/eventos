package security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JWTUserDetails implements UserDetails {
    private static final long serialVersionUID = 8263959673102007882L;

    private String username;
    private String token;
    private Integer idUser;  // Adaptado: era Long id
    private Collection<? extends GrantedAuthority> authorities;

    public JWTUserDetails() {
        // Constructor vacío
    }

    public JWTUserDetails(String username, String token, Integer idUser, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.token = token;
        this.idUser = idUser;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // No se expone la contraseña
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "{\"JWTUserDetails\":{"
                + "\"username\":\"" + username + "\""
                + ", \"token\":\"" + token + "\""
                + ", \"idUser\":\"" + idUser + "\""
                + ", \"authorities\":" + authorities
                + "}}";
    }
}
