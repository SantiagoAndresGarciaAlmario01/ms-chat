package edu.dosw.styleradar.chat.domain.model;

import edu.dosw.styleradar.chat.domain.exception.ChatConsigoMismoException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionCerradaException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static edu.dosw.styleradar.chat.support.Datos.AHORA;
import static edu.dosw.styleradar.chat.support.Datos.AJENO;
import static edu.dosw.styleradar.chat.support.Datos.COMPRADOR;
import static edu.dosw.styleradar.chat.support.Datos.PUBLICACION;
import static edu.dosw.styleradar.chat.support.Datos.VENDEDOR;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static edu.dosw.styleradar.chat.support.Datos.publicacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConversacionTest {

    @Test
    void iniciaUnaConversacionAbiertaSobreUnaPublicacionDisponible() {
        Conversacion c = Conversacion.iniciar(publicacion(EstadoPublicacion.DISPONIBLE), COMPRADOR, AHORA);

        assertThat(c.getId()).isNull();
        assertThat(c.getPublicacionId()).isEqualTo(PUBLICACION);
        assertThat(c.getPrendaNombre()).isEqualTo("Chaqueta de jean");
        assertThat(c.getCompradorId()).isEqualTo(COMPRADOR);
        assertThat(c.getVendedorId()).isEqualTo(VENDEDOR);
        assertThat(c.getEstado()).isEqualTo(EstadoConversacion.ABIERTA);
        assertThat(c.getFechaCreacion()).isEqualTo(AHORA);
        assertThat(c.estaAbierta()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoPublicacion.class, names = "DISPONIBLE", mode = EnumSource.Mode.EXCLUDE)
    void noIniciaSobreUnaPublicacionNoDisponible(EstadoPublicacion estado) {
        assertThatThrownBy(() -> Conversacion.iniciar(publicacion(estado), COMPRADOR, AHORA))
                .isInstanceOf(PublicacionNoDisponibleException.class);
    }

    @Test
    void noIniciaUnChatConsigoMismo() {
        assertThatThrownBy(() -> Conversacion.iniciar(publicacion(EstadoPublicacion.DISPONIBLE), VENDEDOR, AHORA))
                .isInstanceOf(ChatConsigoMismoException.class);
    }

    @Test
    void compradorYVendedorSonParticipantes() {
        Conversacion c = conversacion(7L, EstadoConversacion.ABIERTA);
        assertThat(c.esParticipante(COMPRADOR)).isTrue();
        assertThat(c.esParticipante(VENDEDOR)).isTrue();
        assertThat(c.esParticipante(AJENO)).isFalse();
    }

    @Test
    void paraUnAjenoLaConversacionNoExiste() {
        Conversacion c = conversacion(7L, EstadoConversacion.ABIERTA);
        assertThatThrownBy(() -> c.verificarParticipante(AJENO))
                .isInstanceOf(ConversacionNoEncontradaException.class)
                .hasMessageContaining("7");
    }

    @Test
    void unParticipanteEscribeEnUnaConversacionAbierta() {
        Conversacion c = conversacion(7L, EstadoConversacion.ABIERTA);

        Mensaje m = c.escribir(VENDEDOR, new TextoMensaje("Sí, sigue disponible"), AHORA);

        assertThat(m.getId()).isNull();
        assertThat(m.getConversacionId()).isEqualTo(7L);
        assertThat(m.getAutorId()).isEqualTo(VENDEDOR);
        assertThat(m.getTexto().valor()).isEqualTo("Sí, sigue disponible");
    }

    @Test
    void noSeEscribeEnUnaConversacionCerrada() {
        Conversacion c = conversacion(7L, EstadoConversacion.ABIERTA);
        c.cerrar();
        c.cerrar();

        assertThat(c.getEstado()).isEqualTo(EstadoConversacion.CERRADA);
        assertThatThrownBy(() -> c.escribir(COMPRADOR, new TextoMensaje("Hola"), AHORA))
                .isInstanceOf(ConversacionCerradaException.class);
    }

    @Test
    void unAjenoNoPuedeEscribir() {
        Conversacion c = conversacion(7L, EstadoConversacion.CERRADA);
        assertThatThrownBy(() -> c.escribir(AJENO, new TextoMensaje("Hola"), AHORA))
                .isInstanceOf(ConversacionNoEncontradaException.class);
    }

    @Test
    void exigeLosDatosObligatorios() {
        assertThatThrownBy(() -> new Conversacion(1L, null, "x", COMPRADOR, VENDEDOR, EstadoConversacion.ABIERTA, AHORA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Conversacion(1L, PUBLICACION, "x", null, VENDEDOR, EstadoConversacion.ABIERTA, AHORA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Conversacion(1L, PUBLICACION, "x", COMPRADOR, null, EstadoConversacion.ABIERTA, AHORA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Conversacion(1L, PUBLICACION, "x", COMPRADOR, VENDEDOR, null, AHORA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Conversacion(1L, PUBLICACION, "x", COMPRADOR, VENDEDOR, EstadoConversacion.ABIERTA, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Conversacion.iniciar(null, COMPRADOR, AHORA)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Conversacion.iniciar(publicacion(EstadoPublicacion.DISPONIBLE), null, AHORA))
                .isInstanceOf(NullPointerException.class);
    }
}
