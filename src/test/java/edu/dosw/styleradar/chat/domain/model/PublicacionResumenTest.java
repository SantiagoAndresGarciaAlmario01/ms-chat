package edu.dosw.styleradar.chat.domain.model;

import org.junit.jupiter.api.Test;

import static edu.dosw.styleradar.chat.support.Datos.publicacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublicacionResumenTest {

    @Test
    void soloDisponibleEstaDisponible() {
        assertThat(publicacion(EstadoPublicacion.DISPONIBLE).estaDisponible()).isTrue();
        assertThat(publicacion(EstadoPublicacion.VENDIDA).estaDisponible()).isFalse();
    }

    @Test
    void reconoceUnaPublicacionRetirada() {
        assertThat(publicacion(EstadoPublicacion.RETIRADA).fueRetirada()).isTrue();
        assertThat(publicacion(EstadoPublicacion.DONADA).fueRetirada()).isFalse();
    }

    @Test
    void exigeIdVendedorYEstado() {
        assertThatThrownBy(() -> new PublicacionResumen(null, 2L, "x", EstadoPublicacion.DISPONIBLE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PublicacionResumen(1L, null, "x", EstadoPublicacion.DISPONIBLE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PublicacionResumen(1L, 2L, "x", null))
                .isInstanceOf(NullPointerException.class);
    }
}
