package edu.dosw.styleradar.chat.domain.model;

import org.junit.jupiter.api.Test;

import static edu.dosw.styleradar.chat.support.Datos.AHORA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MensajeTest {

    @Test
    void exponeSusDatos() {
        Mensaje mensaje = new Mensaje(5L, 7L, 1L, new TextoMensaje("Hola"), AHORA);
        assertThat(mensaje.getId()).isEqualTo(5L);
        assertThat(mensaje.getConversacionId()).isEqualTo(7L);
        assertThat(mensaje.getAutorId()).isEqualTo(1L);
        assertThat(mensaje.getTexto().valor()).isEqualTo("Hola");
        assertThat(mensaje.getFechaEnvio()).isEqualTo(AHORA);
    }

    @Test
    void exigeConversacionAutorTextoYFecha() {
        TextoMensaje texto = new TextoMensaje("Hola");
        assertThatThrownBy(() -> new Mensaje(null, null, 1L, texto, AHORA)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Mensaje(null, 7L, null, texto, AHORA)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Mensaje(null, 7L, 1L, null, AHORA)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Mensaje(null, 7L, 1L, texto, null)).isInstanceOf(NullPointerException.class);
    }
}
