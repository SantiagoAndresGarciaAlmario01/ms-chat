package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static edu.dosw.styleradar.chat.support.Datos.AJENO;
import static edu.dosw.styleradar.chat.support.Datos.COMPRADOR;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static edu.dosw.styleradar.chat.support.Datos.mensaje;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMensajesUseCaseImplTest {

    @Mock
    private ConversacionRepositoryPort conversaciones;
    @Mock
    private MensajeRepositoryPort mensajes;
    private ConsultarMensajesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarMensajesUseCaseImpl(conversaciones, mensajes);
    }

    @Test
    void unParticipanteVeLosMensajes() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));
        when(mensajes.buscarPorConversacion(7L)).thenReturn(List.of(mensaje(1L, 7L, COMPRADOR, "Hola")));

        assertThat(useCase.consultar(COMPRADOR, 7L)).extracting("id").containsExactly(1L);
    }

    @Test
    void laConversacionInexistenteDa404() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.consultar(COMPRADOR, 7L))
                .isInstanceOf(ConversacionNoEncontradaException.class);
    }

    @Test
    void unAjenoNoVeLosMensajes() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));

        assertThatThrownBy(() -> useCase.consultar(AJENO, 7L)).isInstanceOf(ConversacionNoEncontradaException.class);
        verify(mensajes, never()).buscarPorConversacion(any());
    }
}
