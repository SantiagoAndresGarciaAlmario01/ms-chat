package edu.dosw.styleradar.chat.infrastructure.adapter.out.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

/** Usuario tomado del JWT. El chat no consulta la base de usuarios: todo sale del token. */
public record UsuarioAutenticado(Long usuarioId, Set<String> roles) {

    public List<GrantedAuthority> authorities() {
        return roles.stream().sorted().<GrantedAuthority>map(rol -> new SimpleGrantedAuthority("ROLE_" + rol)).toList();
    }
}
