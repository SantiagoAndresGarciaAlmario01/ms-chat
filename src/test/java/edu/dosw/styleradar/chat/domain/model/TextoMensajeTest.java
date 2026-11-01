package edu.dosw.styleradar.chat.domain.model;

import edu.dosw.styleradar.chat.domain.exception.TextoMensajeInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextoMensajeTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n\t"})
    void rechazaTextoVacio(String valor) {
        assertThatThrownBy(() -> new TextoMensaje(valor))
                .isInstanceOf(TextoMensajeInvalidoException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    void quitaEspaciosSobrantes() {
        assertThat(new TextoMensaje("  ¿Sigue disponible?  ").valor()).isEqualTo("¿Sigue disponible?");
    }

    @Test
    void aceptaExactamenteMilCaracteres() {
        assertThat(new TextoMensaje("a".repeat(1000)).valor()).hasSize(1000);
    }

    @Test
    void rechazaMasDeMilCaracteres() {
        assertThatThrownBy(() -> new TextoMensaje("a".repeat(1001)))
                .isInstanceOf(TextoMensajeInvalidoException.class)
                .hasMessageContaining("1000");
    }

    @Test
    void cuentaElLimiteDespuesDeQuitarEspacios() {
        assertThat(new TextoMensaje("  " + "a".repeat(1000) + "  ").valor()).hasSize(1000);
    }
}
