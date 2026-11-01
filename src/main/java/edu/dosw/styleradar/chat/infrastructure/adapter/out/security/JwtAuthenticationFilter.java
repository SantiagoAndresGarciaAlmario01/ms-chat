package edu.dosw.styleradar.chat.infrastructure.adapter.out.security;

import edu.dosw.styleradar.chat.infrastructure.exception.SecurityProblemHandler;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Lee el header Authorization: Bearer y deja al usuario en el contexto de seguridad. */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenAdapter tokens;
    private final SecurityProblemHandler errores;

    public JwtAuthenticationFilter(JwtTokenAdapter tokens, SecurityProblemHandler errores) {
        this.tokens = tokens;
        this.errores = errores;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsuarioAutenticado usuario = tokens.validar(header.substring(7).trim());
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                    usuario, null, usuario.authorities()));
            SecurityContextHolder.setContext(context);
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            log.warn("Token Bearer rechazado");
            errores.commence(request, response, new BadCredentialsException("Token inválido"));
            return;
        }
        chain.doFilter(request, response);
    }
}
