package edu.dosw.styleradar.chat.support;

import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/** Genera tokens firmados con la misma llave de application-test.yml, como lo haría el servicio de identidad. */
public final class Tokens {

    public static final String SECRETO = "c3R5bGVyYWRhci1tcy1jaGF0LWxsYXZlLXNvbG8tcGFyYS1wcnVlYmFzISE=";

    private Tokens() {
    }

    public static String de(Long usuarioId) {
        return firmar(Jwts.builder().subject(usuarioId.toString()), Instant.now(), SECRETO);
    }

    public static String conRoles(Long usuarioId, List<String> roles) {
        return firmar(Jwts.builder().subject(usuarioId.toString()).claim("roles", roles), Instant.now(), SECRETO);
    }

    public static String vencido(Long usuarioId) {
        return firmar(Jwts.builder().subject(usuarioId.toString()), Instant.now().minusSeconds(7200), SECRETO);
    }

    public static String conOtraLlave(Long usuarioId) {
        String otra = Base64.getEncoder().encodeToString("otra-llave-distinta-de-al-menos-32-bytes!!".getBytes());
        return firmar(Jwts.builder().subject(usuarioId.toString()), Instant.now(), otra);
    }

    public static String conSubject(String subject) {
        return firmar(Jwts.builder().subject(subject), Instant.now(), SECRETO);
    }

    public static String conAlgoritmoHs512(Long usuarioId) {
        var llave = new SecretKeySpec(new byte[64], "HmacSHA512");
        return Jwts.builder().subject(usuarioId.toString()).issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600))).signWith(llave, Jwts.SIG.HS512).compact();
    }

    public static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String firmar(io.jsonwebtoken.JwtBuilder builder, Instant emitido, String secreto) {
        var llave = new SecretKeySpec(Base64.getDecoder().decode(secreto), "HmacSHA256");
        return builder.issuedAt(Date.from(emitido)).expiration(Date.from(emitido.plusSeconds(3600)))
                .signWith(llave, Jwts.SIG.HS256).compact();
    }
}
