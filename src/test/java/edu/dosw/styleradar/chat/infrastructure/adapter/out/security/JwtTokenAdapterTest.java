package edu.dosw.styleradar.chat.infrastructure.adapter.out.security;

import edu.dosw.styleradar.chat.support.Tokens;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenAdapterTest {

    private final JwtTokenAdapter adapter = new JwtTokenAdapter(Tokens.SECRETO, "COMPRADOR", Clock.systemUTC());

    @Test
    void sinClaimDeRolesAsignaElRolPorDefecto() {
        UsuarioAutenticado usuario = adapter.validar(Tokens.de(5L));

        assertThat(usuario.usuarioId()).isEqualTo(5L);
        assertThat(usuario.roles()).containsExactly("COMPRADOR");
        assertThat(usuario.authorities()).extracting("authority").containsExactly("ROLE_COMPRADOR");
    }

    @Test
    void usaLosRolesDelTokenConOSinPrefijo() {
        UsuarioAutenticado usuario = adapter.validar(Tokens.conRoles(5L, List.of("ROLE_ADMIN_ALMACEN", "COMPRADOR", " ")));

        assertThat(usuario.roles()).containsExactlyInAnyOrder("ADMIN_ALMACEN", "COMPRADOR");
    }

    @Test
    void rechazaTokenVencido() {
        assertThatThrownBy(() -> adapter.validar(Tokens.vencido(5L))).isInstanceOf(JwtException.class);
    }

    @Test
    void rechazaTokenFirmadoConOtraLlave() {
        assertThatThrownBy(() -> adapter.validar(Tokens.conOtraLlave(5L))).isInstanceOf(JwtException.class);
    }

    @Test
    void rechazaUnAlgoritmoDistintoDeHs256() {
        String secreto512 = java.util.Base64.getEncoder().encodeToString(new byte[64]);
        JwtTokenAdapter adapter512 = new JwtTokenAdapter(secreto512, "COMPRADOR", Clock.systemUTC());
        assertThatThrownBy(() -> adapter512.validar(Tokens.conAlgoritmoHs512(5L))).isInstanceOf(JwtException.class);
    }

    @Test
    void rechazaUnTokenMalFormado() {
        assertThatThrownBy(() -> adapter.validar("no-es-un-jwt")).isInstanceOf(JwtException.class);
    }

    @Test
    void rechazaSubjectNoNumerico() {
        assertThatThrownBy(() -> adapter.validar(Tokens.conSubject("abc"))).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> adapter.validar(Tokens.conSubject("0"))).isInstanceOf(JwtException.class);
    }

    @Test
    void exigeUnaLlaveValidaDeAlMenos256Bits() {
        assertThatThrownBy(() -> new JwtTokenAdapter("", "COMPRADOR", Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("256");
        assertThatThrownBy(() -> new JwtTokenAdapter("no-es-base64!", "COMPRADOR", Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Base64");
    }
}
