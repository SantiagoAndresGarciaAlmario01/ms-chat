package edu.dosw.styleradar.chat.infrastructure.adapter.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Clock;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Valida los tokens que emite el servicio de identidad (hoy el monolito) con la misma llave HS256.
 * El token del monolito solo trae el id del usuario en el subject; si trae el claim "roles" se usa,
 * y si no, se asigna el rol por defecto configurado.
 */
@Component
public class JwtTokenAdapter {

    private final JwtParser parser;
    private final Clock clock;
    private final String rolPorDefecto;

    public JwtTokenAdapter(@Value("${styleradar.security.jwt.secret-base64:}") String secretBase64,
                           @Value("${styleradar.security.jwt.rol-por-defecto:COMPRADOR}") String rolPorDefecto,
                           Clock clock) {
        byte[] llave;
        try {
            llave = Base64.getDecoder().decode(secretBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT_SECRET_BASE64 debe contener una clave Base64 válida");
        }
        if (llave.length < 32) {
            throw new IllegalStateException("JWT_SECRET_BASE64 es requerida y debe contener al menos 256 bits");
        }
        SecretKey key = new SecretKeySpec(llave, "HmacSHA256");
        this.clock = clock;
        this.rolPorDefecto = rolPorDefecto;
        this.parser = Jwts.parser().verifyWith(key).clock(() -> Date.from(clock.instant())).build();
    }

    public UsuarioAutenticado validar(String token) {
        Jws<Claims> jws = parser.parseSignedClaims(token);
        // Solo HS256: no se aceptan algoritmos elegidos por quien emite el token.
        if (!Jwts.SIG.HS256.getId().equals(jws.getHeader().getAlgorithm())) {
            throw new JwtException("Algoritmo JWT no permitido");
        }
        Claims claims = jws.getPayload();
        Date ahora = Date.from(clock.instant());
        if (claims.getExpiration() == null || claims.getIssuedAt() == null
                || !claims.getExpiration().after(ahora) || claims.getIssuedAt().after(ahora)) {
            throw new JwtException("Fechas JWT inválidas");
        }
        String subject = claims.getSubject();
        if (subject == null || !subject.matches("[1-9]\\d{0,18}")) {
            throw new JwtException("Subject JWT inválido");
        }
        return new UsuarioAutenticado(Long.valueOf(subject), roles(claims.get("roles")));
    }

    private Set<String> roles(Object claim) {
        Set<String> roles = new LinkedHashSet<>();
        if (claim instanceof Collection<?> lista) {
            lista.stream().filter(String.class::isInstance).map(String.class::cast)
                    .map(rol -> rol.startsWith("ROLE_") ? rol.substring(5) : rol)
                    .filter(rol -> !rol.isBlank())
                    .forEach(roles::add);
        }
        if (roles.isEmpty()) {
            roles.add(rolPorDefecto);
        }
        return Set.copyOf(roles);
    }
}
