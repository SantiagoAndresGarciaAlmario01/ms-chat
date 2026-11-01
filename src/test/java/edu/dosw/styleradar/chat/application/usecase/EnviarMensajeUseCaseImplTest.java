package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.exception.ConversacionCerradaException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoPublicacion;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Optional;

import static edu.dosw.styleradar.chat.support.Datos.AHORA;
import static edu.dosw.styleradar.chat.support.Datos.AJENO;
import static edu.dosw.styleradar.chat.support.Datos.COMPRADOR;
import static edu.dosw.styleradar.chat.support.Datos.PUBLICACION;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static edu.dosw.styleradar.chat.support.Datos.mensaje;
import static edu.dosw.styleradar.chat.support.Datos.publicacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarMensajeUseCaseImplTest {

    private static final TextoMensaje TEXTO = new TextoMensaje("¿Podemos vernos el sábado?");

    @Mock
    private ConversacionRepositoryPort conversaciones;
    @Mock
    private MensajeRepositoryPort mensajes;
    @Mock
    private PublicacionConsultaPort publicaciones;
    private EnviarMensajeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new EnviarMensajeUseCaseImpl(conversaciones, mensajes, publicaciones, Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    @Test
    void guardaElMensajeDeUnParticipante() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.VENDIDA));
        when(mensajes.guardar(any())).thenReturn(mensaje(20L, 7L, COMPRADOR, TEXTO.valor()));

        Mensaje resultado = useCase.enviar(COMPRADOR, 7L, TEXTO);

        ArgumentCaptor<Mensaje> guardado = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajes).guardar(guardado.capture());
        assertThat(guardado.getValue().getAutorId()).isEqualTo(COMPRADOR);
        assertThat(guardado.getValue().getFechaEnvio()).isEqualTo(AHORA);
        assertThat(resultado.getId()).isEqualTo(20L);
        verify(conversaciones, never()).guardar(any());
    }

    @Test
    void siLaPublicacionFueRetiradaCierraLaConversacionYRechazaElMensaje() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.RETIRADA));

        assertThatThrownBy(() -> useCase.enviar(COMPRADOR, 7L, TEXTO)).isInstanceOf(ConversacionCerradaException.class);

        ArgumentCaptor<Conversacion> cerrada = ArgumentCaptor.forClass(Conversacion.class);
        verify(conversaciones).guardar(cerrada.capture());
        assertThat(cerrada.getValue().getEstado()).isEqualTo(EstadoConversacion.CERRADA);
        verify(mensajes, never()).guardar(any());
    }

    @Test
    void unaConversacionYaCerradaNoConsultaLaPublicacion() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.CERRADA)));

        assertThatThrownBy(() -> useCase.enviar(COMPRADOR, 7L, TEXTO)).isInstanceOf(ConversacionCerradaException.class);
        verifyNoInteractions(publicaciones);
    }

    @Test
    void laConversacionInexistenteDa404() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.enviar(COMPRADOR, 7L, TEXTO))
                .isInstanceOf(ConversacionNoEncontradaException.class);
    }

    @Test
    void unAjenoNoPuedeEscribirNiProvocaConsultas() {
        when(conversaciones.buscarPorId(7L)).thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));

        assertThatThrownBy(() -> useCase.enviar(AJENO, 7L, TEXTO)).isInstanceOf(ConversacionNoEncontradaException.class);
        verifyNoInteractions(publicaciones, mensajes);
    }
}
